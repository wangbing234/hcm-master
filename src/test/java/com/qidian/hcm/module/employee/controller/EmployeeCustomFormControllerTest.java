package com.qidian.hcm.module.employee.controller;

import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.employee.enums.FormType;
import com.qidian.hcm.module.employee.repository.CustomizedEmployeeFormRepository;
import com.qidian.hcm.module.employee.request.CustomizedFormsFieldsRequest;
import com.qidian.hcm.module.employee.request.CustomizedFormsRequest;
import com.qidian.hcm.module.employee.request.EnableRequest;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

@SuppressWarnings("PMD")
public class EmployeeCustomFormControllerTest extends BaseTest {

    private JSONObject response;

    private Long ecfid;

    @Autowired
    private CustomizedEmployeeFormRepository customizedEmployeeFormRepository;

    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testGetCustomizedFormLayout() {
        this.response = client.createRequest(MethodType.get, "/api/employees/customized_forms/layout")
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    @Test(dependsOnMethods = "testUpdateCustomizedForms")
    public void testGetCustomizedForms() {
        this.response = client.createRequest(MethodType.get, "/api/employees/customized_forms")
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验查询结果", response.getJSONObject("data").toJSONString(), containsString("自动化测试"));
    }

    @Test
    public void testCreateCustomizedForms() {
        CustomizedFormsRequest request = new CustomizedFormsRequest();
        request.setType(FormType.basic);
        request.setMultiRecord(false);
        request.setOnBoard(true);
        request.setRequired(true);
        request.setTitle("自动化测试");
        List<CustomizedFormsFieldsRequest> fields = new ArrayList<>();
        request.setFields(fields);

        this.response = client.createRequest(MethodType.post, "/api/employees/customized_forms")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        this.ecfid = customizedEmployeeFormRepository.findCustomizedEmployeeFormByTitle("自动化测试").get(0).getId();
    }

    @Test
    public void testCreateCustomizedFormsWithWrongType() {
        CustomizedFormsRequest request = new CustomizedFormsRequest();
        request.setType(null);
        request.setMultiRecord(false);
        request.setOnBoard(true);
        request.setRequired(true);
        request.setTitle("自动化测试");
        List<CustomizedFormsFieldsRequest> fields = new ArrayList<>();
        request.setFields(fields);

        this.response = client.createRequest(MethodType.post, "/api/employees/customized_forms")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", ResultCode.FORM_TYPE_INCORRECT.getCode()));
    }

    @Test(dependsOnMethods = "testCreateCustomizedForms")
    public void testUpdateCustomizedForms() {
        CustomizedFormsRequest request = new CustomizedFormsRequest();
        request.setType(FormType.basic);
        request.setMultiRecord(false);
        request.setOnBoard(true);
        request.setRequired(false);
        request.setTitle("自动化测试");
        request.setIdx(11);
        List<CustomizedFormsFieldsRequest> fields = new ArrayList<>();
        request.setFields(fields);

        this.response = client.createRequest(MethodType.put, "/api/employees/customized_forms/" + ecfid)
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库状态", customizedEmployeeFormRepository
                .findCustomizedEmployeeFormByTitle("自动化测试").get(0).getRequired(), is(false));
    }

    @Test(description = "禁用自定义字段", dependsOnMethods = "testUpdateCustomizedForms")
    public void testToggleActiveCustomizedForms_C001() {
        EnableRequest request = new EnableRequest();
        request.setEnable(false);
        this.response = client.createRequest(MethodType.put,
                "/api/employees/customized_forms/" + ecfid + "/toggle_active")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库数据状态", customizedEmployeeFormRepository
                .findCustomizedEmployeeFormByTitle("自动化测试").get(0).getEnable(), is(false));

    }

    @Test(description = "启用自定义字段", dependsOnMethods = "testToggleActiveCustomizedForms_C001")
    public void testToggleActiveCustomizedForms_C002() {
        EnableRequest request = new EnableRequest();
        request.setEnable(true);
        this.response = client.createRequest(MethodType.put,
                "/api/employees/customized_forms/" + ecfid + "/toggle_active")
                .setData(request).sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库数据状态", customizedEmployeeFormRepository
                .findCustomizedEmployeeFormByTitle("自动化测试").get(0).getEnable(), is(true));
    }

    @Test(description = "删除自定义字段", dependsOnMethods = "testToggleActiveCustomizedForms_C002")
    public void testDeleteCustomizedForms_C001() {
        this.response = client.createRequest(MethodType.delete,
                "/api/employees/customized_forms/" + ecfid)
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库状态", customizedEmployeeFormRepository
                .findCustomizedEmployeeFormByTitle("自动化测试").size(), is(0));
    }

    @Test(description = "删除不存在的字段")
    public void testDeleteCustomizedForms_C002() {
        this.response = client.createRequest(MethodType.delete,
                "/api/employees/customized_forms/" + 1000L)
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", ResultCode.CUSTOMIZED_FORM_IS_NULL.getCode()));
    }

    @Test(dependsOnMethods = "testCreateCustomizedForms")
    public void testGetCustomizedFormDetail() {
        this.response = client.createRequest(MethodType.get,
                "/api/employees/customized_forms/" + ecfid)
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
        assertThat("校验数据库状态", customizedEmployeeFormRepository
                .findCustomizedEmployeeFormByTitle("自动化测试").get(0), notNullValue());

    }

    @Test
    public void testSortCustomizedFormDetail() {
        List<Long> request = newArrayList(3L, 2L, 1L);

        this.response = client.createRequest(MethodType.put,
                "/api/employees/customized_forms/sort")
                .setData(request)
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", "0"));
    }

    @Test
    public void testSortCustomizedFormDetailWithInvalidId() {
        List<Long> request = newArrayList(3L, 2L, 1L, 200L);

        this.response = client.createRequest(MethodType.put,
                "/api/employees/customized_forms/sort")
                .setData(request)
                .sendRequset().getResponse();

        assertThat("响应状态断言", response, hasEntry("code", ResultCode.PARAM_INVALID.getCode()));
    }
}