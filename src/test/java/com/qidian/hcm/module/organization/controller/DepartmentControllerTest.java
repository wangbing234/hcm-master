package com.qidian.hcm.module.organization.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.organization.entity.PositionEntity;
import com.qidian.hcm.module.organization.enums.OrganizationEnums;
import com.qidian.hcm.module.organization.repository.OrganizationRepository;
import com.qidian.hcm.module.organization.repository.PositionRepository;
import com.qidian.hcm.module.organization.request.CreateDepartmentRequest;
import com.qidian.hcm.module.organization.request.EnableDepartmentRequest;
import com.qidian.hcm.module.organization.request.UpdateDepartmentRequest;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TokenType;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


@SuppressWarnings("PMD")
public class DepartmentControllerTest extends BaseTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PositionRepository positionRepository;

    private JSONObject response;

    private Long departmentId;

    private Long subDepartmentId;

    private String code = RandomStringUtils.randomNumeric(8);


    @BeforeMethod
    public void setUp() {
        /*mvc = MockMvcBuilders.standaloneSetup(departmentController)
                .addFilter(jwtAuthenticationTokenFilter).build();//独立测试*/
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        client = new ClientUtils(mvc);

    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(dependsOnMethods = "testCreateDepartment", dataProvider = "defaultPermissionData")
    public void testGetDepartmentList(TokenType tokenType, String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("active", "1");
        params.add("pageNo", "1");
        params.add("pageSize", "20");

        response = client.createRequest(MethodType.get, "/api/department").setData(params)
                .setToken(tokenType)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(code));
    }

    @Test(dependsOnMethods = "testCreateDepartment")
    public void testGetDepartmentListWithKeyword() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("active", "1");
        params.add("keyword", "A");
        params.add("pageNo", "1");
        params.add("pageSize", "20");

        response = client.createRequest(MethodType.get, "/api/department").setData(params)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(dependsOnMethods = "testCreateDepartment")
    public void testGetDepartment() {
        response = client.createRequest(MethodType.get, "/api/department/" + departmentId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test
    public void testGetDepartment_NotExist() {
        response = client.createRequest(MethodType.get, "/api/department/0")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.DEPARTMENT_IS_NULL.getCode()));
    }

    @Test(dependsOnMethods = "testCreateSubDepartment")
    public void testGetSubDepartment() {
        response = client.createRequest(MethodType.get, "/api/department/" + subDepartmentId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(dependsOnMethods = "testGetDepartment")
    public void testGetPositionByDepartmentId() {

        response = client.createRequest(MethodType.get, "/api/department/" + departmentId + "/positions")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @DataProvider(name = "createDepartmentDataProvider")
    public Object[][] createCompanyDataProvider() {
        return new Object[][]{
                {TokenType.MANAGER, 1L, SUCCESS},
                {TokenType.HAVE_PERMISSION, 2L, SUCCESS},
                {TokenType.HAVE_PERMISSION, 1L, ResultCode.DO_NOT_HAVE_ACCESS.getCode()},
                {TokenType.NO_PERMISSION, 1L, ResultCode.DO_NOT_HAVE_ACCESS.getCode()}
        };
    }

    @Test(description = "创建部门", dataProvider = "createDepartmentDataProvider")
    public void testCreateDepartment(TokenType tokenType, Long parentId, String resultCode) {
        CreateDepartmentRequest createDepartmentRequest = new CreateDepartmentRequest();
        createDepartmentRequest.setAlias("中国");
        createDepartmentRequest.setCode(RandomStringUtils.randomNumeric(8));
        createDepartmentRequest.setEnable(YesNo.YES);
        createDepartmentRequest.setEnableTime(new Date());
        JSONObject customField = new JSONObject();
        customField.put("挂靠部门", "测试部门");
        createDepartmentRequest.setCustomField(customField);
        createDepartmentRequest.setFormation(10);
        createDepartmentRequest.setMaster("部门负责人");
        createDepartmentRequest.setName("测试部门");
        createDepartmentRequest.setParentId(parentId);

        response = client.createRequest(MethodType.post, "/api/department/")
                .setData(createDepartmentRequest).setToken(tokenType)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(resultCode));
        if (TokenType.MANAGER == tokenType) {
            departmentId = organizationRepository.findByCodeAndType(createDepartmentRequest.getCode(),
                    OrganizationEnums.department).get().getId();
        }
    }

    @Test(description = "创建子部门", dependsOnMethods = "testCreateDepartment")
    public void testCreateSubDepartment() {
        String code = RandomStringUtils.randomNumeric(8);
        CreateDepartmentRequest createDepartmentRequest = new CreateDepartmentRequest();
        createDepartmentRequest.setAlias("中国");
        createDepartmentRequest.setCode(code);
        createDepartmentRequest.setEnable(YesNo.YES);
        createDepartmentRequest.setEnableTime(new Date());
        createDepartmentRequest.setFormation(10);
        createDepartmentRequest.setMaster("部门负责人");
        createDepartmentRequest.setName("测试部门A");
        createDepartmentRequest.setParentId(departmentId);

        response = client.createRequest(MethodType.post, "/api/department/")
                .setData(createDepartmentRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));

        subDepartmentId = organizationRepository.findByCodeAndType(code, OrganizationEnums.department).get().getId();
    }

    @Test(description = "修改部门信息_地址", dependsOnMethods = "testCreateDepartment")
    public void testUpdateDepartment() {
        UpdateDepartmentRequest updateDepartmentRequest = new UpdateDepartmentRequest();
        updateDepartmentRequest.setAlias("中国光谷");
        updateDepartmentRequest.setCode(code);
        updateDepartmentRequest.setEnable(YesNo.YES);
        updateDepartmentRequest.setEnableTime(new Date());
        updateDepartmentRequest.setFormation(10);
        updateDepartmentRequest.setMaster("测试部门负责人");
        updateDepartmentRequest.setName("测试部门");
        updateDepartmentRequest.setParentId(1L);

        response = client.createRequest(MethodType.put, "/api/department/" + departmentId)
                .setData(updateDepartmentRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("数据库字段更新检查", organizationRepository.findById(departmentId).get().getAlias(),
                equalTo("中国光谷"));

        JSONObject customField = new JSONObject();
        customField.put("挂靠部门", "测试部门");
        updateDepartmentRequest.setCustomField(customField);

        response = client.createRequest(MethodType.put, "/api/department/" + departmentId)
                .setData(updateDepartmentRequest)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));


        updateDepartmentRequest.setParentId(subDepartmentId);
        response = client.createRequest(MethodType.put, "/api/department/" + departmentId)
                .setData(updateDepartmentRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.DEPARTMENT_IS_CYCLE.getCode()));
    }

    @Test(description = "禁用部门,存在生效的子部门", dependsOnMethods = "testCreateSubDepartment")
    public void testDisableDepartment_ExistActiveChildren() {
        EnableDepartmentRequest enableDepartmentRequest = new EnableDepartmentRequest();
        enableDepartmentRequest.setId(Lists.newArrayList(departmentId));
        enableDepartmentRequest.setEnable(YesNo.NO);

        response = client.createRequest(MethodType.put, "/api/department/enable")
                .setData(enableDepartmentRequest)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.DEPARTMENT_IS_USED.getCode()));
    }

    @Test(description = "禁用部门", dependsOnMethods = "testDisableDepartment_ExistActiveChildren")
    public void testDisableDepartment() {
        EnableDepartmentRequest enableDepartmentRequest = new EnableDepartmentRequest();
        enableDepartmentRequest.setId(Lists.newArrayList(subDepartmentId));
        enableDepartmentRequest.setEnable(YesNo.NO);

        response = client.createRequest(MethodType.put, "/api/department/enable")
                .setData(enableDepartmentRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", organizationRepository.findById(subDepartmentId).get().getEnable(), equalTo(YesNo.NO));

        enableDepartmentRequest.setId(Lists.newArrayList(departmentId));
        response = client.createRequest(MethodType.put, "/api/department/enable")
                .setData(enableDepartmentRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", organizationRepository.findById(departmentId).get().getEnable(), equalTo(YesNo.NO));
    }

    @Test(description = "禁用部门,存在有效的岗位", dependsOnMethods = "testDisableDepartment_ExistActiveChildren")
    public void testDisableDepartmentWithActivePosition() {
        PositionEntity position = positionRepository.findAll().stream().filter(p ->
                p.getDeleted() == YesNo.NO && p.getEnable() == YesNo.YES).findFirst().get();
        EnableDepartmentRequest enableDepartmentRequest = new EnableDepartmentRequest();
        enableDepartmentRequest.setId(Lists.newArrayList(position.getDepartmentId()));
        enableDepartmentRequest.setEnable(YesNo.NO);

        response = client.createRequest(MethodType.put, "/api/department/enable")
                .setData(enableDepartmentRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.DEPARTMENT_IS_USED.getCode()));
    }

    @Test(description = "启用部门，存在未生效的父部门", dependsOnMethods = "testDisableDepartment")
    public void testEnableDepartment_ExistUnActiveParent() {
        EnableDepartmentRequest enableDepartmentRequest = new EnableDepartmentRequest();
        enableDepartmentRequest.setId(Lists.newArrayList(subDepartmentId));
        enableDepartmentRequest.setEnable(YesNo.YES);

        response = client.createRequest(MethodType.put, "/api/department/enable")
                .setData(enableDepartmentRequest)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.PARENT_ORG_IS_DISABLED.getCode()));
    }

    @Test(description = "启用部门", dependsOnMethods = "testEnableDepartment_ExistUnActiveParent")
    public void testEnableDepartment() {
        EnableDepartmentRequest enableDepartmentRequest = new EnableDepartmentRequest();
        enableDepartmentRequest.setId(Lists.newArrayList(departmentId));
        enableDepartmentRequest.setEnable(YesNo.YES);

        response = client.createRequest(MethodType.put, "/api/department/enable")
                .setData(enableDepartmentRequest)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("检查数据库字段值", organizationRepository.findById(departmentId).get().getEnable(), equalTo(YesNo.YES));

        enableDepartmentRequest.setId(Lists.newArrayList(subDepartmentId));
        response = client.createRequest(MethodType.put, "/api/department/enable")
                .setData(enableDepartmentRequest)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("检查数据库字段值", organizationRepository.findById(subDepartmentId).get().getEnable(), equalTo(YesNo.YES));
    }

    @Test(description = "删除存在有效子部门的部门", dependsOnMethods = "testEnableDepartment")
    public void testDeleteDepartment_ExistActiveChildren() {
        response = client.createRequest(MethodType.delete, "/api/department/" + departmentId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.DEPARTMENT_IS_USED.getCode()));
    }

    @Test(description = "删除存在有效子岗位的部门", dependsOnMethods = "testEnableDepartment")
    public void testDeleteDepartment_ExistActivePositionUsed() {
        PositionEntity position = positionRepository.findAll().stream().filter(p ->
                p.getDeleted() == YesNo.NO && p.getEnable() == YesNo.YES).findFirst().get();

        response = client.createRequest(MethodType.delete, "/api/department/" + position.getDepartmentId())
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.DEPARTMENT_IS_USED.getCode()));
    }

    @Test(description = "删除不存在的部门")
    public void testDeleteDepartment_NotExist() {
        response = client.createRequest(MethodType.delete, "/api/department/0")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.DEPARTMENT_IS_NULL.getCode()));
    }


    @Test(description = "删除叶子节点的部门", dependsOnMethods = "testDeleteDepartment_ExistActiveChildren")
    public void testDeleteDepartment() {
        response = client.createRequest(MethodType.delete, "/api/department/" + subDepartmentId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("检验数据库数据", organizationRepository.findById(subDepartmentId).get().getDeleted(), equalTo(YesNo.YES));
    }

    @Test(description = "删除已删除的部门", dependsOnMethods = "testDeleteDepartment")
    public void testDeleteDepartment_HadDeleted() {
        response = client.createRequest(MethodType.delete, "/api/department/" + subDepartmentId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.DEPARTMENT_IS_NULL.getCode()));
    }
}