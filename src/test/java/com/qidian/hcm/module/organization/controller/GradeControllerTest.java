package com.qidian.hcm.module.organization.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.organization.repository.GradeRepository;
import com.qidian.hcm.module.organization.request.CreateGradeRequest;
import com.qidian.hcm.module.organization.request.EnableGradeRequest;
import com.qidian.hcm.module.organization.request.UpdateGradeRequest;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("PMD")
public class GradeControllerTest extends BaseTest {
    @Autowired
    private GradeRepository gradeRepository;

    private Long gradeId;
    private String code;

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

    @Test(dependsOnMethods = "testCreateGrade")
    public void testGetGradeList() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("active", "1");
        params.add("keyword", "");
        params.add("pageNo", "1");
        params.add("pageSize", "20");
        JSONObject response = client.createRequest(MethodType.get, "/api/grade").setData(params)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(dependsOnMethods = "testCreateGrade")
    public void testGetGradeListWithKeyword() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("active", "1");
        params.add("keyword", "技术");
        params.add("pageNo", "1");
        params.add("pageSize", "20");
        JSONObject response = client.createRequest(MethodType.get, "/api/grade").setData(params)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test
    public void testCreateGrade() {
        CreateGradeRequest createGradeRequest = new CreateGradeRequest();
        createGradeRequest.setName("技术经理");
        createGradeRequest.setAlias("技术经理");
        createGradeRequest.setCode(TestUtils.getDate(TestUtils.DATE_FORMAT3).substring(6));
        createGradeRequest.setRank("T1");
        createGradeRequest.setEnableTime(new Date());
        JSONObject customField = new JSONObject();
        customField.put("挂靠部门", "测试部门");
        createGradeRequest.setCustomField(customField);

        JSONObject response = client.createRequest(MethodType.post, "/api/grade/")
                .setData(createGradeRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        this.gradeId = gradeRepository.findByCode(createGradeRequest.getCode()).get().getId();
        this.code = gradeRepository.findByCode(createGradeRequest.getCode()).get().getCode();
    }

    @Test(dependsOnMethods = "testCreateGrade")
    public void testCreateGradeDuplicateCode() {
        CreateGradeRequest createGradeRequest = new CreateGradeRequest();
        createGradeRequest.setName("技术经理");
        createGradeRequest.setAlias("技术经理");
        createGradeRequest.setCode(this.code);
        createGradeRequest.setRank("T1");
        createGradeRequest.setEnableTime(new Date());
        JSONObject customField = new JSONObject();
        customField.put("挂靠部门", "测试部门");
        createGradeRequest.setCustomField(customField);

        JSONObject response = client.createRequest(MethodType.post, "/api/grade/")
                .setData(createGradeRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.GRADE_ALREADY_EXISTS.getCode()));
    }

    @Test(dependsOnMethods = "testCreateGrade")
    public void testUpdateGrade() {
        UpdateGradeRequest updateGradeRequest = new UpdateGradeRequest();
        updateGradeRequest.setName("技术经理");
        updateGradeRequest.setAlias("技经");
        updateGradeRequest.setCode(RandomStringUtils.randomNumeric(8));
        updateGradeRequest.setRank("T1");
        updateGradeRequest.setEnableTime(new Date());
        JSONObject customField = new JSONObject();
        customField.put("挂靠部门", "测试部门");
        updateGradeRequest.setCustomField(customField);

        JSONObject response = client.createRequest(MethodType.put, "/api/grade/" + gradeId)
                .setData(updateGradeRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("数据库字段更新检查", gradeRepository.findById(gradeId).get().getAlias(),
                equalTo("技经"));
    }

    @Test(dependsOnMethods = "testCreateGrade")
    public void testUpdateGradeWithWrongId() {
        UpdateGradeRequest updateGradeRequest = new UpdateGradeRequest();
        updateGradeRequest.setName("技术经理");
        updateGradeRequest.setAlias("技经");
        updateGradeRequest.setCode(RandomStringUtils.randomNumeric(8));
        updateGradeRequest.setRank("T1");
        updateGradeRequest.setEnableTime(new Date());
        JSONObject customField = new JSONObject();
        customField.put("挂靠部门", "测试部门");
        updateGradeRequest.setCustomField(customField);

        JSONObject response = client.createRequest(MethodType.put, "/api/grade/" + gradeId + 1000)
                .setData(updateGradeRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.GRADE_NOT_EXISTS.getCode()));
    }

    @Test(dependsOnMethods = "testCreateGrade")
    public void testGetGrade() {
        JSONObject response = client.createRequest(MethodType.get, "/api/grade/" + gradeId)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(dependsOnMethods = "testCreateGrade")
    public void testGetGradeWithWrongId() {
        JSONObject response = client.createRequest(MethodType.get, "/api/grade/" + gradeId + 1000)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.GRADE_NOT_EXISTS.getCode()));
    }

    @Test(dependsOnMethods = "testCreateGrade")
    public void testEnableGrade() {
        EnableGradeRequest request = new EnableGradeRequest();
        request.setId(ImmutableList.of(gradeId));
        request.setEnable(YesNo.YES);
        JSONObject response = client.createRequest(MethodType.put, "/api/grade/enable")
                .setData(request)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("数据库字段更新检查", gradeRepository.findById(gradeId).get().getEnable(),
                equalTo(YesNo.YES));
    }

    @Test(dependsOnMethods = "testEnableGrade")
    public void testDisabledGrade() {
        EnableGradeRequest request = new EnableGradeRequest();
        request.setId(ImmutableList.of(gradeId));
        request.setEnable(YesNo.NO);
        JSONObject response = client.createRequest(MethodType.put, "/api/grade/enable")
                .setData(request)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("数据库字段更新检查", gradeRepository.findById(gradeId).get().getEnable(),
                equalTo(YesNo.NO));
    }


    @Test(dependsOnMethods = "testCreateGrade")
    public void testDeleteGrade() {
        JSONObject response = client.createRequest(MethodType.delete, "/api/grade/" + gradeId)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("数据库字段更新检查", gradeRepository.findById(gradeId).get().getDelete(),
                equalTo(YesNo.YES));
    }

    @Test(dependsOnMethods = "testCreateGrade")
    public void testDeleteGrade_NOTEXIST() {
        JSONObject response = client.createRequest(MethodType.delete, "/api/grade/0")
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.GRADE_NOT_EXISTS.getCode()));
    }


}