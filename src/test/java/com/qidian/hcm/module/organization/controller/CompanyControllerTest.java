package com.qidian.hcm.module.organization.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.organization.repository.OrganizationRepository;
import com.qidian.hcm.module.organization.request.CreateCompanyRequest;
import com.qidian.hcm.module.organization.request.EnableCompanyRequest;
import com.qidian.hcm.module.organization.request.UpdateCompanyRequest;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qidian.hcm.module.organization.enums.OrganizationEnums.company;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("PMD")
public class CompanyControllerTest extends BaseTest {


    @Autowired
    private OrganizationRepository organizationRepository;

    private String companyCode;
    private Long companyId;

    private Long subCompanyId;

    private JSONObject response;

    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        super.client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "获取公司列表", dataProvider = "defaultPermissionData")
    public void testGetCompanyList(TokenType tokenType, String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("active", "1");
        params.add("pageNo", "1");
        params.add("pageSize", "20");

        response = client.createRequest(MethodType.get, "/api/company/")
                .setData(params).setToken(tokenType).sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(code));
    }

    @Test(dependsOnMethods = {"testCreateSubCompany"})
    public void testGetCompanyListWithKeyword() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("active", "1");
        params.add("keyword", "A");
        params.add("pageNo", "1");
        params.add("pageSize", "20");

        response = client.createRequest(MethodType.get, "/api/company/")
                .setData(params).sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(SUCCESS));
    }

    @Test(description = "获取公司列表", dataProvider = "defaultPermissionData")
    public void testTree(TokenType tokenType, String code) {
        response = client.createRequest(MethodType.get, "/api/company/tree")
                .setToken(tokenType)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(code));
    }

    @Test(description = "获取公司列表（选择框）")
    public void testTreeSelection() {
        response = client.createRequest(MethodType.get, "/api/company/tree/selection")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(SUCCESS));
    }

    @Test
    public void testCompanyTree() {
        response = client.createRequest(MethodType.get, "/api/company/companyTree")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(SUCCESS));
    }

    @Test(dependsOnMethods = {"testCreateCompany"})
    public void testGetCompanyById() {
        response = client.createRequest(MethodType.get, "/api/company/" + companyId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(SUCCESS));
    }

    @Test(description = "获取不存在的公司", dependsOnMethods = {"testCreateCompany"})
    public void testGetCompanyWithNotExistId() {
        response = client.createRequest(MethodType.get, "/api/company/0")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.COMPANY_IS_NULL.getCode()));
    }

    @Test(description = "删除不存在的公司", dependsOnMethods = {"testGetCompanyList"})
    public void testDeleteCompanyWithNotExistId() {
        response = client.createRequest(MethodType.delete, "/api/company/0")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.COMPANY_IS_NULL.getCode()));
    }

    @Test(description = "顶级公司不能删除", dependsOnMethods = "testGetCompanyList")
    public void testDeleteCompanyById_C001() {
        response = client.createRequest(MethodType.delete, "/api/company/" + 1L)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("12001"));
    }

    @Test(description = "删除公司,公司存在生效的下级单位", dependsOnMethods = "testGetCompanyList")
    public void testDeleteCompanyById_C002() {
        response = client.createRequest(MethodType.delete, "/api/company/" + companyId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.COMPANY_IS_USED.getCode()));
    }

    @Test(description = "删除公司", dependsOnMethods = "testEnableCompany")
    public void testDeleteCompanyById_C003() {
        response = client.createRequest(MethodType.delete, "/api/company/" + subCompanyId)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("数据库公司信息已删除", organizationRepository.findById(subCompanyId).get().getDeleted(), equalTo(YesNo.YES));
    }

    @Test(dependsOnMethods = {"testUpdateCompany"}, description = "禁用公司,存在生效下级公司")
    public void testDisableCompany_ExistActiveChildren() {
        EnableCompanyRequest enableCompanyRequest = new EnableCompanyRequest();
        List<Long> ids = new ArrayList<>();
        ids.add(companyId);
        enableCompanyRequest.setId(ids);
        enableCompanyRequest.setEnable(YesNo.NO);

        response = client.createRequest(MethodType.put, "/api/company/enable")
                .setData(enableCompanyRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.COMPANY_IS_USED.getCode()));
    }

    @Test(dependsOnMethods = {"testDisableCompany_ExistActiveChildren"}, description = "禁用公司")
    public void testDisableCompany() {
        EnableCompanyRequest enableCompanyRequest = new EnableCompanyRequest();
        enableCompanyRequest.setId(Lists.newArrayList(subCompanyId));
        enableCompanyRequest.setEnable(YesNo.NO);

        response = client.createRequest(MethodType.put, "/api/company/enable")
                .setData(enableCompanyRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", organizationRepository.findById(subCompanyId).get().getEnable(), equalTo(YesNo.NO));

        enableCompanyRequest.setId(Lists.newArrayList(companyId));

        response = client.createRequest(MethodType.put, "/api/company/enable")
                .setData(enableCompanyRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", organizationRepository.findById(companyId).get().getEnable(), equalTo(YesNo.NO));
    }


    @Test(dependsOnMethods = {"testDisableCompany"}, description = "启用公司,存在未生效的父公司")
    public void testEnableCompany_ExistUnActiveParent() {
        EnableCompanyRequest enableCompanyRequest = new EnableCompanyRequest();
        enableCompanyRequest.setId(Lists.newArrayList(subCompanyId));
        enableCompanyRequest.setEnable(YesNo.YES);

        response = client.createRequest(MethodType.put, "/api/company/enable")
                .setData(enableCompanyRequest)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.PARENT_ORG_IS_DISABLED.getCode()));
    }

    @Test(dependsOnMethods = {"testEnableCompany_ExistUnActiveParent"}, description = "启用公司")
    public void testEnableCompany() {
        EnableCompanyRequest enableCompanyRequest = new EnableCompanyRequest();
        enableCompanyRequest.setId(Lists.newArrayList(companyId));
        enableCompanyRequest.setEnable(YesNo.YES);

        response = client.createRequest(MethodType.put, "/api/company/enable")
                .setData(enableCompanyRequest)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", organizationRepository.findById(companyId).get().getEnable(), equalTo(YesNo.YES));

        enableCompanyRequest.setId(Lists.newArrayList(subCompanyId));
        response = client.createRequest(MethodType.put, "/api/company/enable")
                .setData(enableCompanyRequest)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("检查数据库字段值", organizationRepository.findById(subCompanyId).get().getEnable(), equalTo(YesNo.YES));
    }

    @DataProvider(name = "createCompanyDataProvider")
    public Object[][] createCompanyDataProvider() {
        return new Object[][]{
                {TokenType.MANAGER, 1L, SUCCESS},
                {TokenType.HAVE_PERMISSION, 1L, ResultCode.DO_NOT_HAVE_ACCESS.getCode()},
                {TokenType.HAVE_PERMISSION, 2L, SUCCESS},
                {TokenType.NO_PERMISSION, 1L, ResultCode.DO_NOT_HAVE_ACCESS.getCode()}
        };
    }

    @Test(description = "创建公司", dataProvider = "createCompanyDataProvider")
    public void testCreateCompany(TokenType tokenType, Long parentId, String resultCode) {
        CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest();
        createCompanyRequest.setAddress("湖北省武汉市洪山区");
        createCompanyRequest.setAlias("奇点测试");
        createCompanyRequest.setCode(RandomStringUtils.randomNumeric(8));
        JSONObject customField = new JSONObject();
        customField.put("挂靠部门", "国务院");
        createCompanyRequest.setCustomField(customField);
        createCompanyRequest.setEnableTime(new Date());
        createCompanyRequest.setLegalPerson("大头");
        createCompanyRequest.setRegisterAddress("中国台北市");
        createCompanyRequest.setParentId(parentId);
        createCompanyRequest.setName("神秘公司");
        createCompanyRequest.setMaster("小头");

        response = client.createRequest(MethodType.post, "/api/company")
                .setData(createCompanyRequest).setToken(tokenType)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(resultCode));
        if (TokenType.MANAGER == tokenType) {
            this.companyCode = createCompanyRequest.getCode();
            this.companyId = organizationRepository.findByCodeAndType(companyCode, company).get().getId();//取出公司ID
        }
    }

    @Test(dependsOnMethods = {"testCreateCompany"}, description = "创建公司,组织架构代码已存在")
    public void testCreateCompany_ExistCode() {
        CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest();
        createCompanyRequest.setAddress("湖北省武汉市洪山区");
        createCompanyRequest.setAlias("奇点测试");
        createCompanyRequest.setCode(companyCode);
        JSONObject customField = new JSONObject();
        customField.put("挂靠部门", "国务院");
        createCompanyRequest.setCustomField(customField);
        createCompanyRequest.setEnableTime(new Date());
        createCompanyRequest.setLegalPerson("大头");
        createCompanyRequest.setRegisterAddress("中国台北市");
        createCompanyRequest.setParentId(1L);
        createCompanyRequest.setName("神秘公司");
        createCompanyRequest.setMaster("小头");

        response = client.createRequest(MethodType.post, "/api/company")
                .setData(createCompanyRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.ORG_CODE_EXISTS.getCode()));
    }

    @Test(dependsOnMethods = {"testCreateCompany"}, description = "创建公司，无自定义字段")
    public void testCreateSubCompany() {
        String companyCode = RandomStringUtils.randomNumeric(8);
        CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest();
        createCompanyRequest.setAddress("湖北省武汉市洪山区");
        createCompanyRequest.setAlias("奇点测试");
        createCompanyRequest.setCode(companyCode);
        createCompanyRequest.setEnableTime(new Date());
        createCompanyRequest.setLegalPerson("大头");
        createCompanyRequest.setRegisterAddress("中国台北市");
        createCompanyRequest.setParentId(companyId);
        createCompanyRequest.setName("神秘公司A");
        createCompanyRequest.setMaster("小头");

        response = client.createRequest(MethodType.post, "/api/company")
                .setData(createCompanyRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        subCompanyId = organizationRepository.findByCodeAndType(companyCode, company).get().getId();//取出公司ID
    }

    @Test(dependsOnMethods = {"testCreateCompany"})
    public void testUpdateCompany() {
        UpdateCompanyRequest updateCompanyRequest = new UpdateCompanyRequest();
        updateCompanyRequest.setId(companyId);
        updateCompanyRequest.setAddress("湖北省武汉市洪山区保利时代");
        updateCompanyRequest.setAlias("奇点测试啊");
        updateCompanyRequest.setCode(companyCode);
        JSONObject customField = new JSONObject();
        customField.put("挂靠部门", "国务院");
        updateCompanyRequest.setCustomField(customField);
        updateCompanyRequest.setEnableTime(new Date());
        updateCompanyRequest.setLegalPerson("大头");
        updateCompanyRequest.setRegisterAddress("中国台北市");
        updateCompanyRequest.setParentId(1L);
        updateCompanyRequest.setName("神秘公司");
        updateCompanyRequest.setMaster("小头");

        response = client.createRequest(MethodType.put, "/api/company/" + companyId)
                .setData(updateCompanyRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("数据库字段更新检查", organizationRepository.findById(companyId).get().getAddress(),
                equalTo("湖北省武汉市洪山区保利时代"));
    }

    @Test
    public void testUpdateRootCompany() {
        UpdateCompanyRequest updateCompanyRequest = new UpdateCompanyRequest();
        updateCompanyRequest.setId(1L);
        updateCompanyRequest.setAddress("上海SOHO");
        updateCompanyRequest.setAlias("有招");
        updateCompanyRequest.setCode("00000000");

        updateCompanyRequest.setEnableTime(new Date());
        updateCompanyRequest.setLegalPerson("大头");
        updateCompanyRequest.setRegisterAddress("中国台北市");
        updateCompanyRequest.setParentId(-1L);
        updateCompanyRequest.setName("有招");
        updateCompanyRequest.setMaster("范总");

        response = client.createRequest(MethodType.put, "/api/company/1")
                .setData(updateCompanyRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("数据库字段更新检查", organizationRepository.findById(1L).get().getAddress(),
                equalTo("上海SOHO"));

        updateCompanyRequest.setParentId(companyId);
        response = client.createRequest(MethodType.put, "/api/company/1")
                .setData(updateCompanyRequest)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.COMPANY_IS_CYCLE.getCode()));
    }
}