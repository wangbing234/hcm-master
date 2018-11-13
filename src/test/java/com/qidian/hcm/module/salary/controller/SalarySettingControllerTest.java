package com.qidian.hcm.module.salary.controller;

import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.module.salary.enums.SalaryPayMonth;
import com.qidian.hcm.module.salary.request.SalaryCycleDateRequest;
import com.qidian.hcm.module.salary.request.SalaryDateRequest;
import com.qidian.hcm.module.salary.request.SalaryThresholdRequest;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("PMD")
public class SalarySettingControllerTest extends BaseTest {

    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        super.client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "设置计薪周期")
    public void testSetSalaryCycleDate() {
        SalaryCycleDateRequest request = new SalaryCycleDateRequest();
        request.setFirstCycleMonth("2018-09");
        request.setStartCycleDay(12);
        JSONObject response = client.createRequest(MethodType.put, "/api/salaries/setting/cycle")
                .setData(request).sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "设置发薪日期")
    public void testSetSalaryPayDate() {
        SalaryDateRequest request = new SalaryDateRequest();
        request.setSalaryPayDate(15);
        request.setSalaryPayMonth(SalaryPayMonth.current);
        JSONObject response = client.createRequest(MethodType.put, "/api/salaries/setting/pay_date")
                .setData(request).sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "获取薪酬配置信息")
    public void testGetSalaryConfigs() {
        JSONObject response = client.createRequest(MethodType.get, "/api/salaries/setting/config")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "获取免税额列表")
    public void testGetSalaryThreshold() {
        JSONObject response = client.createRequest(MethodType.get, "/api/salaries/setting/threshold")
                .sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }

    @Test(description = "修改免税额")
    public void testUpdateSalaryTax() {
        SalaryThresholdRequest request = new SalaryThresholdRequest();
        request.setPoint(5000);
        JSONObject response = client.createRequest(MethodType.put, "/api/salaries/setting/threshold/1")
                .setData(request).sendRequset().getResponse();
        logger.info(response);
        assertThat("响应code检查", response.getString("code"), is("0"));
    }
}