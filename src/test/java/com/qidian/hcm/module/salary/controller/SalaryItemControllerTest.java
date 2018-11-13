package com.qidian.hcm.module.salary.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.salary.enums.PointRule;
import com.qidian.hcm.module.salary.enums.SalaryType;
import com.qidian.hcm.module.salary.repository.SalaryItemRepository;
import com.qidian.hcm.module.salary.request.SalaryItemRequest;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({"PMD"})
public class SalaryItemControllerTest extends BaseTest {

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    private static final String ROOT = "/api/salaries/items/";

    private JSONObject response;

    private Long ecfid;

    private String name = "de" + TestUtils.getDate(TestUtils.DATE_FORMAT3);

    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context).addFilter(jwtAuthenticationTokenFilter).build();
        client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "薪资项目列表查询", dependsOnMethods = "testSalaryItemAdd")
    public void testSalaryItemList() {
        this.response = client.createRequest(MethodType.get, ROOT).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
        assertThat("响应data检查", response.getJSONArray("data").size(), greaterThanOrEqualTo(1));
    }

    @Test(description = "薪资项目", dependsOnMethods = "testSalaryItemAdd")
    public void testSalaryItemOption() {
        this.response = client.createRequest(MethodType.get, ROOT + "option").sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
        assertThat("响应data检查", response.getJSONArray("data").size(), greaterThanOrEqualTo(1));
    }

    @Test(description = "增加薪资项目")
    public void testSalaryItemAdd() {
        SalaryItemRequest request = SalaryItemRequest.builder().name(name).type(SalaryType.laborCost)
                .pointScale(2).pointRule(PointRule.floor).formula("3+2").display(Boolean.TRUE).build();

        this.response = client.createRequest(MethodType.post, ROOT).setData(JSON.toJSONString(request))
                .sendRequset().getResponse();
        ecfid = response.getLong("data");
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }

    @Test(description = "编辑薪资项目", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit() {
        commonSalaryItemEdit(SalaryType.laborCost,"3+3");
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
        assertThat("校验数据库字段", salaryItemRepository.findById(ecfid).get().getName(), is(name));
    }

    @Test(description = "编辑薪资项目001", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit001() {
        commonSalaryItemEdit(SalaryType.laborCost,"${employerBasicHousingFund}+${employerAddingHousingFund}+1");
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
        assertThat("校验数据库字段", salaryItemRepository.findById(ecfid).get().getName(), is(name));
    }

    @Test(description = "编辑薪资项目002", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit002() {
        commonSalaryItemEdit(SalaryType.laborCost,"${personalOutwork}+${employerAddingHousingFund}+1");
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }

    @Test(description = "编辑薪资项目003", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit003() {
        commonSalaryItemEdit(SalaryType.taxBeforeAdd,
                "#{service.getTaxBeforeAdd()}+${employerAddingHousingFund}+1");
        System.out.println(response.getString(RESULT_CODE));
        assertThat("响应code检查", response.getString(RESULT_CODE),
                is(ResultCode.SALARY_ITEM_IS_CYCLE.getCode()));
    }

    @Test(description = "编辑薪资项目004", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit004() {
        commonSalaryItemEdit(SalaryType.external,
                "#{service.getTaxBeforeAdd()}+#{service.getTaxBeforeMinus()}+" +
                        "#{service.getTaxAfterAdd()}+#{service.getTaxAfterMinus()}" +
                        "+${employerAddingHousingFund}+1");
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }

    @Test(description = "编辑薪资项目0041", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit0041() {
        commonSalaryItemEdit(SalaryType.laborCost,
                "#{service.getExternal()}+1");
        assertThat("响应code检查", response.getString(RESULT_CODE),
                is(ResultCode.SALARY_ITEM_IS_CYCLE.getCode()));
    }

    @Test(description = "编辑薪资项目005", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit005() {
        commonSalaryItemEdit(SalaryType.external,
                "service.minusDate(service.getCurrentDate(),service.plusYears(service.getCurrentDate(),1),1)" +
                        "+service.minusDate(service.plusMonths(service.getCurrentDate(),1)," +
                        "service.plusWeeks(service.getCurrentDate(),2),2)" +
                        "+service.minusDate(service.plusDays(service.getCurrentDate(),1)," +
                        "service.plusHours(service.getCurrentDate(),2),3)" +
                        "+service.minusDate(service.plusDays(service.getCurrentDate(),1)," +
                        "service.plusHours(service.getCurrentDate(),2),4)" +
                        "+service.minusDate(service.plusDays(service.getCurrentDate(),1)," +
                        "service.plusHours(service.getCurrentDate(),2),5)+service.getMonthDays()");
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }

    @Test(description = "编辑薪资项目006", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit006() {
        commonSalaryItemEdit(SalaryType.external,
                "service.minusDate(service.getCurrentDate(),service.plusYears(service.getCurrentDate(),1),6)");
        assertThat("响应code检查", response.getString(RESULT_CODE),
                is(ResultCode.SALARY_ITEM_PARSE_ERROR.getCode()));
    }

    @Test(description = "编辑薪资项目007", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit007() {
        commonSalaryItemEdit(SalaryType.external,
                "${employerBasicHousingFund-1}+1");
        assertThat("响应code检查", response.getString(RESULT_CODE),
                is(ResultCode.SALARY_ITEM_CODE_NOT_EXISTS.getCode()));
    }

    @Test(description = "编辑薪资项目008", dependsOnMethods = {"testSalaryItemAdd"})
    public void testSalaryItemEdit008() {
        commonSalaryItemEdit(SalaryType.external,
                "service.minusDate(323)+1/0");
        assertThat("响应code检查", response.getString(RESULT_CODE),
                is(ResultCode.SALARY_ITEM_PARSE_ERROR.getCode()));
    }


    private void commonSalaryItemEdit(SalaryType salaryType, String formula) {
        name = "de" + TestUtils.getDate(TestUtils.DATE_FORMAT3);
        SalaryItemRequest request = SalaryItemRequest.builder().name(name).type(salaryType)
                .pointScale(2).pointRule(PointRule.round).formula(formula).display(Boolean.TRUE).build();
        this.response = client.createRequest(MethodType.put, ROOT + ecfid)
                .setData(JSON.toJSONString(request)).sendRequset().getResponse();
    }


    @Test(description = "删除薪资项目测试", dependsOnMethods = {"testSalaryItemEdit", "testSalaryItemList"})
    public void testSalaryItemDelete() {
        response = client.createRequest(MethodType.delete, ROOT + ecfid).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
        assertThat("校验数据库记录", salaryItemRepository.findById(ecfid).isPresent(), is(false));
    }
}
