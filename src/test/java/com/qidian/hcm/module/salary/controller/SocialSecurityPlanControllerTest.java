package com.qidian.hcm.module.salary.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.module.salary.dto.SocialSecurityPlanDTO;
import com.qidian.hcm.module.salary.enums.PointRule;
import com.qidian.hcm.module.salary.repository.SocialSecurityPlanRepository;
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

@SuppressWarnings("PMD")
public class SocialSecurityPlanControllerTest extends BaseTest {


    @Autowired
    private SocialSecurityPlanRepository socialSecurityPlanRepository;


    private static final  String ROOT = "/api/salaries/social_security_plans/";

    private JSONObject response;

    private Long ecfid;

    private String name = "de" + TestUtils.getDate(TestUtils.DATE_FORMAT3);

    @BeforeMethod
    public void setUp() {
        //集成web环境测试
        mvc = MockMvcBuilders.webAppContextSetup(this.context).addFilter(jwtAuthenticationTokenFilter).build();
        client = new ClientUtils(mvc);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test(description = "社保方案列表查询", dependsOnMethods = "testAddSocialSecurity")
    public void testListSocialSecurity() {
        this.response = client.createRequest(MethodType.get, ROOT).sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("响应data检查", response.getJSONArray("data").size(), greaterThanOrEqualTo(1));

    }

    @Test(description = "增加社保方案")
    public void testAddSocialSecurity() {
        SocialSecurityPlanDTO request = SocialSecurityPlanDTO.builder()
                .name(name).limitPoint(12)
                .effectDate(new Date(System.currentTimeMillis()))
                .pointScale(3).limitUp(12L).limitDown(3L).pointRule(PointRule.round)
                .pensionPersonalRatio(10D)
                .pensionEmployerRatio(10D)
                .historyPersonalRatio(10D)
                .historyEmployerRatio(10D).outworkPersonalRatio(10D).outworkEmployerRatio(10D)
                .injuryPersonalRatio(10D).injuryEmployerRatio(10D).birthPersonalRatio(10D)
                .birthEmployerRatio(10D).build();


        this.response = client.createRequest(MethodType.post, ROOT)
                .setData(JSON.toJSONString(request)).sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        ecfid = socialSecurityPlanRepository.findAll().get(0).getId();
    }

    @Test(description = "社保方案字段", dependsOnMethods = "testAddSocialSecurity")
    public void testEditSocialSecurity() {
        Integer scale = 1;
        name = "de" + TestUtils.getDate(TestUtils.DATE_FORMAT3);
        SocialSecurityPlanDTO request = SocialSecurityPlanDTO.builder()
                .name(name)
                .limitPoint(12).effectDate(new Date(System.currentTimeMillis())).pointScale(scale)
                .pointRule(PointRule.floor).limitUp(12L).limitDown(3L).pensionPersonalRatio(10D)
                .pensionEmployerRatio(10D).historyPersonalRatio(10D).historyEmployerRatio(10D)
                .outworkPersonalRatio(10D).outworkEmployerRatio(10D).injuryPersonalRatio(10D)
                .injuryEmployerRatio(10D).birthPersonalRatio(10D).birthEmployerRatio(10D).build();

        this.response = client.createRequest(MethodType.put, ROOT + ecfid)
                .setData(JSON.toJSONString(request)).sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("校验数据库字段", socialSecurityPlanRepository.findById(ecfid).get().getName(), is(name));
        assertThat("校验数据库字段", socialSecurityPlanRepository.findById(ecfid).get()
                .getPointScale(), equalTo(scale));
    }

    @Test(description = "删除社保方案测试", dependsOnMethods = {"testEditSocialSecurity", "testListSocialSecurity"})
    public void testDeleteSocialSecurity() {
        response = client.createRequest(MethodType.delete, ROOT + ecfid).sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is("0"));
        assertThat("校验数据库记录", socialSecurityPlanRepository.findById(ecfid).isPresent(), is(false));
    }

    @Test(description = "再次增加社保方案测试", dependsOnMethods = {"testDeleteSocialSecurity"})
    public void testAddSocialSecurityAgain() {
        testAddSocialSecurity();
    }

}