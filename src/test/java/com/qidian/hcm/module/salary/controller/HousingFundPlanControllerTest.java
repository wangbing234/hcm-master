package com.qidian.hcm.module.salary.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.module.salary.dto.HousingFundPlanDTO;
import com.qidian.hcm.module.salary.enums.PointRule;
import com.qidian.hcm.module.salary.repository.HousingFundPlanRepository;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings({"PMD"})
public class HousingFundPlanControllerTest extends BaseTest {

    @Autowired
    private HousingFundPlanRepository housingFundPlanRepository;

    private static final  String ROOT = "/api/salaries/housing_fund_plans/";

    private JSONObject response;

    private Long ecfid;

    private String name = "de" + TestUtils.getDate(TestUtils.DATE_FORMAT3);


    @BeforeMethod
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(jwtAuthenticationTokenFilter).build();//集成web环境测试
        client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "公积金方案列表查询", dependsOnMethods = "testAddHousingFunPlan")
    public void testGetHousingFunPlanList() {
        this.response = client.createRequest(MethodType.get, ROOT).sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("响应data检查", response.getJSONArray("data").size(), greaterThanOrEqualTo(1));
    }

    @Test(description = "增加公积金方案")
    public void testAddHousingFunPlan() {
        HousingFundPlanDTO request = HousingFundPlanDTO.builder()
                .fundAddingEmployerRatio(10D)
                .fundAddingPersonalRatio(10D).fundEmployerRatio(10D).fundPersonalRatio(10D)
                .limitDown(10L).limitPoint(10).limitUp(10L).name(name).pointRule(PointRule.floor)
                .pointScale(2).effectDate(new Date(System.currentTimeMillis())).build();

        this.response = client.createRequest(MethodType.post, ROOT).setData(JSON.toJSONString(request))
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        ecfid = housingFundPlanRepository.findAll().get(0).getId();
    }

    @Test(description = "公积金方案字段编辑", dependsOnMethods = "testAddHousingFunPlan")
    public void testEditHousingFunPlan() {
        Integer scale = 3;
        name = "de" + TestUtils.getDate(TestUtils.DATE_FORMAT3);
        HousingFundPlanDTO request = HousingFundPlanDTO.builder()
                .fundAddingEmployerRatio(11D)
                .fundAddingPersonalRatio(10D).fundEmployerRatio(10D).fundPersonalRatio(10D)
                .limitDown(10L).limitPoint(10).limitUp(10L).name(name).pointRule(PointRule.floor)
                .pointScale(scale).effectDate(new Date(System.currentTimeMillis())).build();

        this.response = client.createRequest(MethodType.put, ROOT + ecfid)
                .setData(JSON.toJSONString(request)).sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("校验数据库字段", housingFundPlanRepository.findById(ecfid).get().getName(), is(name));
        assertThat("校验数据库字段", housingFundPlanRepository.findById(ecfid).get()
                .getPointScale(), equalTo(scale));
    }

    @Test(description = "删除公积金方案测试", dependsOnMethods = {"testEditHousingFunPlan", "testGetHousingFunPlanList"})
    public void testDeleteHousingFunPlan() {
        response = client.createRequest(MethodType.delete, ROOT + ecfid).sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));

        assertThat("校验数据库记录", housingFundPlanRepository.findById(ecfid).isPresent(), is(false));

    }

    @Test(description = "再次增加公积金方案--提供数据", dependsOnMethods = "testDeleteHousingFunPlan")
    public void testAddHousingFunPlanAgain() {
        testAddHousingFunPlan();
    }

}