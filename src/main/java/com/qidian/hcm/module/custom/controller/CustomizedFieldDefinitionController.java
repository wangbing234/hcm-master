package com.qidian.hcm.module.custom.controller;

import com.google.common.collect.Lists;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.common.utils.Result;
import com.qidian.hcm.common.utils.ResultGenerator;
import com.qidian.hcm.module.custom.enums.TargetType;
import com.qidian.hcm.module.custom.request.OrgCustomizedFieldRequest;
import com.qidian.hcm.module.custom.response.OrgCustomizedFieldResponse;
import com.qidian.hcm.module.custom.service.CustomizedFieldDefinitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "组织机构--自定义字段相关接口")
@RestController
@RequestMapping("/api/organizations")
@Slf4j
public class CustomizedFieldDefinitionController {

    @Autowired
    private CustomizedFieldDefinitionService customizedFieldDefinitionService;

    @ApiOperation(value = "自定义字段列表", notes = "自定义字段列表")
    @ApiImplicitParam(name = "targetType", value = "目标类型", dataType = "string", paramType = "query")
    @GetMapping(value = "/customized_fields", produces = "application/json")
    public Result<List<OrgCustomizedFieldResponse>> list(@RequestParam(required = false) TargetType targetType) {
        List<OrgCustomizedFieldResponse> data;
        if (null == targetType) {
            List<TargetType> targetTypes = Lists.newArrayList(
                    TargetType.company, TargetType.department,
                    TargetType.position, TargetType.grade);
            data = customizedFieldDefinitionService.findByTargetTypes(targetTypes);
        } else {
            List<TargetType> targetTypes = Lists.newArrayList(targetType);
            data = customizedFieldDefinitionService.findByTargetTypes(targetTypes, YesNo.YES);
        }
        return ResultGenerator.genSuccessResult(data);
    }

    @ApiOperation(value = "新增自定义字段", notes = "新增自定义字段")
    @PostMapping(value = "/customized_fields", produces = "application/json")
    public Result add(@RequestBody @Valid OrgCustomizedFieldRequest request) {
        customizedFieldDefinitionService.save(request);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "修改自定义字段", notes = "修改自定义字段")
    @ApiImplicitParam(name = "id", value = "自定义字段ID", dataType = "long", paramType = "path", required = true)
    @PutMapping(value = "/customized_fields/{id}", produces = "application/json")
    public Result edit(@PathVariable Long id,
                       @RequestBody OrgCustomizedFieldRequest request) {
        request.setId(id);
        customizedFieldDefinitionService.save(request);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "启用禁用自定义字段", notes = "启用禁用自定义字段")
    @ApiImplicitParam(name = "id", value = "自定义字段ID", dataType = "long", paramType = "path", required = true)
    @PutMapping(value = "/customized_fields/{id}/toggle_active", produces = "application/json")
    public Result toggleActive(@PathVariable Long id) {
        customizedFieldDefinitionService.toggleActive(id);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "删除自定义字段", notes = "删除自定义字段")
    @ApiImplicitParam(name = "id", value = "自定义字段ID", dataType = "long", paramType = "path", required = true)
    @DeleteMapping(value = "/customized_fields/{id}", produces = "application/json")
    public Result delete(@PathVariable Long id) {
        customizedFieldDefinitionService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

}
