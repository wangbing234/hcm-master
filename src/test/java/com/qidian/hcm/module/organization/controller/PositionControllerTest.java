package com.qidian.hcm.module.organization.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.common.exception.BizException;
import com.qidian.hcm.common.security.JwtAuthenticationTokenFilter;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.organization.entity.GradeEntity;
import com.qidian.hcm.module.organization.entity.OrganizationEntity;
import com.qidian.hcm.module.organization.enums.OrganizationEnums;
import com.qidian.hcm.module.organization.repository.GradeRepository;
import com.qidian.hcm.module.organization.repository.OrganizationRepository;
import com.qidian.hcm.module.organization.repository.PositionRepository;
import com.qidian.hcm.module.organization.request.CreatePositionRequest;
import com.qidian.hcm.module.organization.request.EnablePositionRequest;
import com.qidian.hcm.module.organization.request.UpdatePositionRequest;
import com.qidian.hcm.module.organization.response.OrganizationTreeResponse;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TokenType;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("PMD")
public class PositionControllerTest extends BaseTest {

    @Autowired
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    private JSONObject response;

    @Autowired
    PositionController positionController;

    private CreatePositionRequest createPositionRequest;

    private String existCode;
    private Long positionId;
    private Long departmentId;

    private Long subPositionId;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    OrganizationRepository departmentRepository;


    @BeforeMethod
    public void setUp() {
        // 集成 web 环境测试
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();

        client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    // 建立会话，目的是定位到具体的租户库
    private void initSession() {
        client.createRequest(MethodType.get, "/api/position/0")
                .sendRequset().getResponse();
    }


    @Test(description = "测试创建岗位")
    public void testCreatePositionSuccessfulScenario() {
        JSONObject customField = new JSONObject();
        customField.put("创建岗位", "23");
        customField.put("测试", "49");

        initSession();
        List<OrganizationTreeResponse> departmentList = departmentRepository.findCompanyOrDepartmentTree(
                OrganizationEnums.department);
        departmentId = departmentList.get(0).getId();

        String code = RandomStringUtils.randomNumeric(8);
        this.existCode = code;
        createPositionRequest = new CreatePositionRequest("fdf", code, "gradeAlias",
                departmentId, -1L, 1L, new Date(), customField);

        response = client.createRequest(MethodType.post, "/api/position/")
                .setData(createPositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        this.positionId = positionRepository.findByCode(code).get().getId();
    }

    @Test(dependsOnMethods = "testCreatePositionSuccessfulScenario", description = "测试添加岗位,无自定义字段")
    public void testCreatePositionWithoutCustomFields() {
        String code = RandomStringUtils.randomNumeric(8);
        createPositionRequest = new CreatePositionRequest("fdf", code, "gradeAlias",
                departmentId, positionId, 1L, new Date(), null);

        response = client.createRequest(MethodType.post, "/api/position/")
                .setData(createPositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        this.subPositionId = positionRepository.findByCode(code).get().getId();
    }

    @Test(dependsOnMethods = "testCreatePositionSuccessfulScenario", description = "测试岗位重复添加")
    public void testCreatePositionDuplicationScenario() {
        JSONObject customField = new JSONObject();
        customField.put("创建岗位", "23");
        customField.put("测试", "49");

        createPositionRequest = new CreatePositionRequest("fdf", existCode, "gradeAlias",
                departmentId, -1L, 1L, new Date(), customField);
        response = client.createRequest(MethodType.post, "/api/position/")
                .setData(createPositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.POSITION_ALREADY_EXISTS.getCode()));
    }

    @DataProvider(name = "createPositionDataProvider")
    public Object[][] createCompanyDataProvider() {
        Long departmentId1 = departmentRepository.findAllByParentIdAndEnableAndDeleted(1L, YesNo.YES, YesNo.NO)
                .get(0).getId();
        Long departmentId2 = departmentRepository.findAllByParentIdAndEnableAndDeleted(2L, YesNo.YES, YesNo.NO)
                .get(0).getId();
        return new Object[][]{
                {TokenType.MANAGER, departmentId1, SUCCESS},
                {TokenType.HAVE_PERMISSION, departmentId1, ResultCode.DO_NOT_HAVE_ACCESS.getCode()},
                {TokenType.HAVE_PERMISSION, departmentId2, SUCCESS},
                {TokenType.NO_PERMISSION, departmentId1, ResultCode.DO_NOT_HAVE_ACCESS.getCode()}
        };
    }

    @Test(dependsOnMethods = "testCreatePositionDuplicationScenario", dataProvider = "defaultPermissionData")
    public void testGetPositionList(TokenType tokenType, String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("active", "1");
        params.add("keyword", "");
        params.add("pageNo", "1");
        params.add("pageSize", "20");

        response = client.createRequest(MethodType.get, "/api/position")
                .setToken(tokenType)
                .setData(params)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(code));
    }

    @Test(dependsOnMethods = "testGetPositionList")
    public void testGetPositionList_SearchKeyWord() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("active", "1");
        params.add("keyword", "1");
        params.add("pageNo", "1");
        params.add("pageSize", "20");

        response = client.createRequest(MethodType.get, "/api/position")
                .setData(params)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(dependsOnMethods = "testGetPositionList")
    public void testGetPositionById() {
        response = client.createRequest(MethodType.get, "/api/position/" + positionId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }


    @Test(description = "测试修改岗位信息_名称", dependsOnMethods = "testGetPositionById")
    public void testUpdatePositionById() {
        String code = RandomStringUtils.randomNumeric(8);
        UpdatePositionRequest updatePositionRequest = new UpdatePositionRequest();
        updatePositionRequest.setName("太空岗修改后");
        updatePositionRequest.setAlias("太空");
        updatePositionRequest.setCode(code);
        updatePositionRequest.setParentPositionId(1L);
        updatePositionRequest.setDepartmentId(departmentId);
        updatePositionRequest.setGradeId(1L);
        updatePositionRequest.setEnableTime(new Date());

        response = client.createRequest(MethodType.put, "/api/position/" + positionId)
                .setData(updatePositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("数据库字段更新检查", positionRepository.findById(positionId).get().getName(),
                equalTo("太空岗修改后"));

    }

    @Test(description = "测试修改岗位信息_自定义字段", dependsOnMethods = "testUpdatePositionById")
    public void testUpdatePosition_WithCustomized() {
        String code = RandomStringUtils.randomNumeric(8);
        JSONObject customField = new JSONObject();
        customField.put("1", "23");
        customField.put("2", "49");

        UpdatePositionRequest updatePositionRequest = new UpdatePositionRequest();
        updatePositionRequest.setName("太空岗修改后");
        updatePositionRequest.setAlias("太空");
        updatePositionRequest.setCode(code);
        updatePositionRequest.setParentPositionId(1L);
        updatePositionRequest.setDepartmentId(departmentId);
        updatePositionRequest.setGradeId(1L);
        updatePositionRequest.setEnableTime(new Date());
        updatePositionRequest.setCustomField(customField);

        response = client.createRequest(MethodType.put, "/api/position/" + positionId)
                .setData(updatePositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("数据库字段更新检查", positionRepository.findById(positionId).get().getName(),
                equalTo("太空岗修改后"));

    }

    @Test(description = "失效岗位时，必须先失效下级岗位", dependsOnMethods = "testUpdatePosition_WithCustomized")
    public void testDisablePositionWithActiveChildPosition() {
        EnablePositionRequest enablePositionRequest = new EnablePositionRequest();
        List<Long> ids = new ArrayList<>();
        ids.add(positionId);
        enablePositionRequest.setId(ids);
        enablePositionRequest.setEnable(YesNo.NO);

        response = client.createRequest(MethodType.put, "/api/position/enable")
                .setData(enablePositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"),
                is(ResultCode.DEACTIVATE_CHILD_POSITION_FIRST.getCode()));
    }

    @Test(description = "失效岗位", dependsOnMethods = "testDisablePositionWithActiveChildPosition")
    public void testDisablePositionByIdSuccessfulScenario() {
        EnablePositionRequest enablePositionRequest = new EnablePositionRequest();
        enablePositionRequest.setId(Lists.newArrayList(subPositionId));
        enablePositionRequest.setEnable(YesNo.NO);

        response = client.createRequest(MethodType.put, "/api/position/enable")
                .setData(enablePositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", positionRepository.findById(subPositionId).get()
                .getEnable(), equalTo(YesNo.NO));

        enablePositionRequest.setId(Lists.newArrayList(positionId));
        response = client.createRequest(MethodType.put, "/api/position/enable")
                .setData(enablePositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", positionRepository.findById(positionId).get()
                .getEnable(), equalTo(YesNo.NO));
    }

    @Test(description = "生效岗位时，必须先生效上级岗位", dependsOnMethods = "testDisablePositionByIdSuccessfulScenario")
    public void testEnablePositionWithActiveParentPosition() {
        EnablePositionRequest enablePositionRequest = new EnablePositionRequest();
        enablePositionRequest.setId(Lists.newArrayList(subPositionId));
        enablePositionRequest.setEnable(YesNo.YES);

        response = client.createRequest(MethodType.put, "/api/position/enable")
                .setData(enablePositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.ACTIVE_PARENT_POSITION_FIRST.getCode()));
    }

    @Test(description = "生效岗位时，必须先生效部门", dependsOnMethods = "testEnablePositionWithActiveParentPosition")
    public void testEnablePositionWithUnActiveDepartment() {
        OrganizationEntity departmentEntity = departmentRepository.findById(this.departmentId).get();
        departmentEntity.setEnable(YesNo.NO);
        departmentRepository.save(departmentEntity);

        EnablePositionRequest enablePositionRequest = new EnablePositionRequest();
        enablePositionRequest.setId(Lists.newArrayList(positionId));
        enablePositionRequest.setEnable(YesNo.YES);

        response = client.createRequest(MethodType.put, "/api/position/enable")
                .setData(enablePositionRequest)
                .sendRequset()
                .getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.ACTIVE_DEVELOPMENT_FIRST.getCode()));
        departmentEntity.setEnable(YesNo.YES);
        departmentRepository.save(departmentEntity);
    }

    @Test(description = "生效岗位时，必须先生效职级", dependsOnMethods = "testEnablePositionWithUnActiveDepartment")
    public void testEnablePositionWithUnActiveGrade() {
        String code = RandomStringUtils.randomNumeric(8);
        GradeEntity disabledGradeEntity = new GradeEntity();
        disabledGradeEntity.setName("test失效职级");
        disabledGradeEntity.setAlias("失效");
        disabledGradeEntity.setCode(code);
        disabledGradeEntity.setRank("职级");
        disabledGradeEntity.setEnable(YesNo.NO);
        disabledGradeEntity.setCustomField(null);
        gradeRepository.save(disabledGradeEntity);

        Long disableGradeId = gradeRepository.findByCode(code).get().getId();

        createPositionRequest = new CreatePositionRequest("fdf", code, "生效测试",
                departmentId, -1L, disableGradeId, new Date(), null);
        client.createRequest(MethodType.post, "/api/position/")
                .setData(createPositionRequest)
                .sendRequset().getResponse();

        Long disablePositionId = positionRepository.findByCode(code).get().getId();

        EnablePositionRequest enablePositionRequest = new EnablePositionRequest();
        enablePositionRequest.setId(Lists.newArrayList(disablePositionId));
        enablePositionRequest.setEnable(YesNo.YES);

        response = client.createRequest(MethodType.put, "/api/position/enable")
                .setData(enablePositionRequest)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.ACTIVE_GRADE_FIRST.getCode()));
    }

    @Test(description = "生效岗位", dependsOnMethods = "testEnablePositionWithUnActiveGrade")
    public void testEnablePosition() {
        EnablePositionRequest enablePositionRequest = new EnablePositionRequest();
        enablePositionRequest.setId(Lists.newArrayList(positionId));
        enablePositionRequest.setEnable(YesNo.YES);

        response = client.createRequest(MethodType.put, "/api/position/enable")
                .setData(enablePositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", positionRepository.findById(positionId).get()
                .getEnable(), equalTo(YesNo.YES));

        enablePositionRequest.setId(Lists.newArrayList(subPositionId));
        enablePositionRequest.setEnable(YesNo.YES);

        response = client.createRequest(MethodType.put, "/api/position/enable")
                .setData(enablePositionRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", positionRepository.findById(subPositionId).get()
                .getEnable(), equalTo(YesNo.YES));
    }

    @Test(description = "测试删除岗位, 存在有效的子岗位", dependsOnMethods = "testEnablePosition")
    public void testDeletePositionWithActiveChildren() {
        response = client.createRequest(MethodType.delete, "/api/position/" + positionId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"),
                is(ResultCode.DEACTIVATE_CHILD_POSITION_FIRST.getCode()));
    }

    @Test(description = "测试删除岗位", dependsOnMethods = "testDeletePositionWithActiveChildren")
    public void testDeletePositionById() {
        response = client.createRequest(MethodType.delete, "/api/position/" + subPositionId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检验数据库数据", positionRepository.findById(subPositionId)
                        .orElseThrow(() -> new BizException(ResultCode.DELETE_POSITION_FAILED)).getDeleted(),
                equalTo(YesNo.YES));
    }

    @Test(description = "测试岗位树", dependsOnMethods = "testCreatePositionSuccessfulScenario")
    public void testGetPositionTree() {
        response = client.createRequest(MethodType.get, "/api/position/tree")
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test(description = "测试岗位选项", dependsOnMethods = "testCreatePositionSuccessfulScenario")
    public void testGetPositionOption() {
        response = client.createRequest(MethodType.get, "/api/position/options?departmentId=" + departmentId)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }
}