package com.qidian.hcm.module.salary.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.module.salary.dto.BankInfoDTO;
import com.qidian.hcm.module.salary.entity.EmployeeFinancial;
import com.qidian.hcm.module.salary.entity.SalaryHistory;
import com.qidian.hcm.module.salary.enums.EmployeesFilterType;
import com.qidian.hcm.module.salary.repository.*;
import com.qidian.hcm.module.salary.request.*;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({"PMD"})
public class EmployeeSalaryControllerTest extends BaseTest {

    @Autowired
    private SalaryHistoryRepository salaryHistoryRepository;

    @Autowired
    private EmployeeBankInfoRepository employeeBankInfoRepository;

    @Autowired
    private SalaryThresholdRepository salaryThresholdRepository;

    @Autowired
    private EmployeeFinancialRepository employeeFinancialRepository;

    @Autowired
    private SocialSecurityPlanRepository socialSecurityPlanRepository;

    @Autowired
    private HousingFundPlanRepository housingFundPlanRepository;

    private static final String ROOT = "/api/salaries/";

    private JSONObject response;

    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context).addFilter(jwtAuthenticationTokenFilter).build();
        client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "获取本月员工薪酬列表")
    public void testList() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("status", EmployeesFilterType.all.name());
        params.add("include", "true");
        params.add("pageNo", "1");
        params.add("pageSize", "20");
        this.response = client.createRequest(MethodType.get, ROOT + "employees").setData(params)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }

    private String getEmployeePrefix() {
        return ROOT + "employees/" + employeeId_HavePrtmission + "/";
    }

    private void commonGet(String suffix) {
        this.response = client.createRequest(MethodType.get, getEmployeePrefix() + suffix)
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }


    @Test(description = "获取薪酬历史记录")
    public void testHistoryList() {
        commonGet("history");
    }

    @Test(description = "获取员工基本信息")
    public void testBasicInfo() {
        commonGet("basic");
    }

    @Test(description = "查询员工--薪酬信息tab")
    public void testSalaryInfo() {
        commonGet("salary_info");
    }

    @Test(description = "查询员工--五险一金tab")
    public void testSecurityDetail() {
        commonGet("security_info");
    }

    @Test(description = "查询员工--本月明细tab")
    public void testSalaryMonthly() {
        commonGet("monthly_detail");
    }

    @Test(description = "编辑明细实时计算")
    public void testSalaryAccounting() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", "personalPensionEmployer");
        params.add("value", "1000");
        this.response = client.createRequest(MethodType.get, getEmployeePrefix() + "accounting")
                .setData(params).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }

    //monthly_detail
    @Test(description = "编辑本月明细")
    public void testSalaryMonthlyDetail() {
        List<SalaryItemMonthlyRequest> employeeSalaryMonthly = new ArrayList<>();
        SalaryItemMonthlyRequest personalOutwork = new SalaryItemMonthlyRequest();
        personalOutwork.setCode("personalOutwork");
        personalOutwork.setId(17L);
        personalOutwork.setModified(true);
        personalOutwork.setValue(100D);
        employeeSalaryMonthly.add(personalOutwork);
        SalaryItemMonthlyRequest dutyDays = new SalaryItemMonthlyRequest();
        dutyDays.setCode("dutyDays");
        dutyDays.setId(21L);
        dutyDays.setModified(true);
        dutyDays.setValue(100D);
        employeeSalaryMonthly.add(dutyDays);
        this.response = client.createRequest(MethodType.put, getEmployeePrefix() + "monthly_detail")
                .setData(JSON.toJSONString(employeeSalaryMonthly)).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }

    @Test(description = "编辑编辑(不产生历史记录)")
    public void testEditSalary() {
        SalaryEditRequest salaryEditRequest = new SalaryEditRequest();
        salaryEditRequest.setSalary(8000D);
        this.response = client.createRequest(MethodType.put, getEmployeePrefix() + "edit")
                .setData(JSON.toJSONString(salaryEditRequest)).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }

    @Test(description = "薪酬调整(产生历史记录)", dependsOnMethods = "testEditSalary")
    public void testAdjustSalary() {
        Double salary = 10000D;
        SalaryAdjustRequest salaryAdjustRequest = new SalaryAdjustRequest();
        salaryAdjustRequest.setSalary(10000D);
        salaryAdjustRequest.setRemark("牛逼的没办法");
        this.response = client.createRequest(MethodType.put, getEmployeePrefix() + "adjust")
                .setData(JSON.toJSONString(salaryAdjustRequest)).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
        List<SalaryHistory> emp = salaryHistoryRepository.findByEmployeeId(employeeId_HavePrtmission);
        Double dbSalary = emp.get(emp.size() - 1).getAfterAdjust();
        assertThat("响应code检查", dbSalary, is(salary));
    }

    @Test(description = "修改银行卡信息")
    public void testUpdateBankInfo() {
        String cardNo = "643434343434343434";
        BankInfoDTO bankInfoDTO = new BankInfoDTO();
        bankInfoDTO.setBankAddress("保利国际中心");
        bankInfoDTO.setBankName("中国银行");
        bankInfoDTO.setCardNo(cardNo);
        this.response = client.createRequest(MethodType.put, getEmployeePrefix() + "bank_info")
                .setData(JSON.toJSONString(bankInfoDTO)).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
        String dbCardNo = employeeBankInfoRepository.findByEmployeeId(employeeId_HavePrtmission).get().getCardNo();
        assertThat("修改银行卡信息卡号", cardNo, is(dbCardNo));
    }

    @Test(description = "修改社保方案")
    public void testEditEmployeeSecurity() {
        Double securityBase = 4000D;
        EmployeeSecurityPlanRequest request = new EmployeeSecurityPlanRequest();
        request.setSocialSecurityBase(securityBase);
        request.setSocialSecurityPlanId(socialSecurityPlanRepository.findAll().get(0).getId());
        this.response = client.createRequest(MethodType.put, getEmployeePrefix() + "social_security_plan")
                .setData(JSON.toJSONString(request)).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
        Double dbSecurityBase = employeeFinancialRepository
                .findByEmployeeId(employeeId_HavePrtmission).get().getSocialSecurityBase();
        assertThat("公积金校验基数", dbSecurityBase, is(securityBase));
    }


    @Test(description = "修改公积金方案")
    public void testEditEmployeeFundPlanInfo() {
        Double housingFundBase = 3000D;
        String account = "9786564545";
        EmployeeHousingFundPlanRequest request = new EmployeeHousingFundPlanRequest();
        request.setHousingFundPlanId(housingFundPlanRepository.findAll().get(0).getId());
        request.setHousingFundBase(housingFundBase);
        request.setAccount(account);
        this.response = client.createRequest(MethodType.put, getEmployeePrefix() + "housing_fund_plans")
                .setData(JSON.toJSONString(request)).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
        EmployeeFinancial employeeFinancial = employeeFinancialRepository
                .findByEmployeeId(employeeId_HavePrtmission).get();
        Double dbHousingFundBase = employeeFinancial.getHousingFundBase();
        String dbAccount = employeeFinancial.getHousingFundAccount();
        assertThat("公积金校验基数", dbHousingFundBase, is(housingFundBase));
        assertThat("公积金账号修改校验", dbAccount, is(account));
    }

    @Test(description = "修改免税额方案")
    public void testEmployeeThreshold() {
        EmployeeThresholdRequest request = new EmployeeThresholdRequest();
        request.setId(salaryThresholdRepository.findAll().get(0).getId());
        this.response = client.createRequest(MethodType.put, getEmployeePrefix() + "threshold")
                .setData(JSON.toJSONString(request)).sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }


    @Test(description = "批量标记本月不记薪")
    public void testSalaryExclude() {
        EmployeeListRequest request = new EmployeeListRequest();
        request.setIds(newArrayList(employeeId_HavePrtmission));
        this.response = client.createRequest(MethodType.put, ROOT + "exclude").setData(JSON.toJSONString(request))
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }


    @Test(description = "批量标记本月记薪")
    public void testSalaryInclude() {
        EmployeeListRequest request = new EmployeeListRequest();
        request.setIds(newArrayList(employeeId_HavePrtmission));
        this.response = client.createRequest(MethodType.put, ROOT + "include").setData(JSON.toJSONString(request))
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString(RESULT_CODE), is(SUCCESS));
    }
}
