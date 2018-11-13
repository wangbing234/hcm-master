package com.qidian.hcm.module.workflow.manager.controller;

import com.qidian.hcm.common.utils.Result;
import com.qidian.hcm.common.utils.ResultGenerator;
import com.qidian.hcm.module.center.config.TenantDataSourceProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/workflow/manager")
@Api(tags = "流程审批--流程定义")
public class ManagerController {

    @Autowired
    private TenantDataSourceProvider tenantDataSourceProvider;

    @ApiOperation("发布流程")
    @PostMapping("deploy")
    public Result<String> getCompanyList(@RequestParam(value = "json") String json) {
        RepositoryService cls = tenantDataSourceProvider.getProcessEngine().getRepositoryService();
        return ResultGenerator.genSuccessResult(cls.toString());
    }

    @ApiOperation("根据包路径发布流程")
    @PostMapping("classpath/deploy")
    public Result<String> deploymentProcessDefinitionByClasspath() {
        //与流程定义和部署对象相关的Service
        Deployment deployment = tenantDataSourceProvider.getProcessEngine().getRepositoryService()
                .createDeployment()//创建一个部署对象
                .name("流程定义")//添加部署名称/Users/wangbing/IdeaProjects/hcm/src/main/resources
                .addClasspathResource("process/MyProcess1.bpmn")//从classpath的资源中加载，一次只能加载一个文件
                .deploy();//完成部署
        log.info("部署ID：{},部署名称:{}", deployment.getId(), deployment.getName());
        return ResultGenerator.genSuccessResult();
    }


    /*
     * 查询流程定义
     */
    @ApiOperation("查询流程定义")
    @GetMapping("list")
    public Result<List<ProcessDefinition>> findProcessDefinition() {
        //与流程定义和部署对象相关的Service
        List<ProcessDefinition> list = tenantDataSourceProvider.getProcessEngine().getRepositoryService()
                .createProcessDefinitionQuery()//创建一个流程定义查询
                /*指定查询条件,where条件*/
                //.deploymentId(deploymentId)//使用部署对象ID查询
                //.processDefinitionId(processDefinitionId)//使用流程定义ID查询
                //.processDefinitionKey(processDefinitionKey)//使用流程定义的KEY查询
                //.processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询

                /*排序*/
                .orderByProcessDefinitionVersion().asc()//按照版本的升序排列
                //.orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列

                .list();//返回一个集合列表，封装流程定义
//        ProcessDefinition definition = list.get(0);
//        ProcessInstance processInstance = tenantDataSourceProvider.getProcessEngine().getRuntimeService()
// .startProcessInstanceByKey(definition.getKey());
        //.singleResult();//返回唯一结果集
        //.count();//返回结果集数量
        //.listPage(firstResult, maxResults)//分页查询

        return ResultGenerator.genSuccessResult(list);
    }

    /*
     * 查询流程定义
     */
    @ApiOperation("发起流程")
    @PostMapping("process/{key}")
    public Result startProcessInstance(@PathVariable("key") String key) {
        tenantDataSourceProvider.getProcessEngine().getRuntimeService().startProcessInstanceByKey(key);
        return ResultGenerator.genSuccessResult();
    }
}
