package com.qidian.hcm.module.authorization.controller;

import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.BaseTest;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.authorization.dto.ConditionDTO;
import com.qidian.hcm.module.authorization.dto.PermissionDTO;
import com.qidian.hcm.module.authorization.dto.PermissionMetaDataDTO;
import com.qidian.hcm.module.authorization.dto.RolePermissionDTO;
import com.qidian.hcm.module.authorization.entity.Role;
import com.qidian.hcm.module.authorization.enums.ActionType;
import com.qidian.hcm.module.authorization.enums.MenuCode;
import com.qidian.hcm.module.authorization.enums.MenuType;
import com.qidian.hcm.module.authorization.enums.PlatformType;
import com.qidian.hcm.module.authorization.repository.RoleRepository;
import com.qidian.hcm.module.authorization.request.RoleEditRequest;
import com.qidian.hcm.module.organization.entity.OrganizationEntity;
import com.qidian.hcm.module.organization.repository.OrganizationRepository;
import com.qidian.hcm.utils.ClientUtils;
import com.qidian.hcm.utils.MethodType;
import com.qidian.hcm.utils.TestUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@SuppressWarnings("PMD")
public class RoleControllerTest extends BaseTest {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    private Long roleId;
    private Long roleId2;

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

    @Test(dependsOnMethods = "testCreateRole")
    public void testGetRoleList() {
        JSONObject response = client.createRequest(MethodType.get, "/api/authorization/roles")
                .sendRequset().getResponse();
        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test
    public void testCreateRole() {
        RoleEditRequest request = new RoleEditRequest();
        request.setName(TestUtils.getChineseName());

        JSONObject response = client.createRequest(MethodType.post, "/api/authorization/roles")
                .setData(request).sendRequset().getResponse();


        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        Role role = roleRepository.findByName(request.getName());
        assertThat("角色名称检查", request.getName(), is(role.getName()));
        roleId = role.getId();

        request.setName(TestUtils.getChineseName());
        JSONObject response2 = client.createRequest(MethodType.post, "/api/authorization/roles")
                .setData(request).sendRequset().getResponse();
        roleId2 = response2.getLong("data");
    }

    @Test(dependsOnMethods = "testCreateRole")
    public void testCreateRole_NameExist() {
        String name = roleRepository.findById(roleId).get().getName();
        RoleEditRequest request = new RoleEditRequest();
        request.setName(name);

        JSONObject response = client.createRequest(MethodType.post, "/api/authorization/roles")
                .setData(request).sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.ROLE_NAME_EXISTS.getCode()));
    }

    @Test(dependsOnMethods = "testCreateRole")
    public void testUpdateRole() {
        String name = roleRepository.findById(roleId).get().getName();
        RoleEditRequest request = new RoleEditRequest();
        request.setName(name + "角色");

        JSONObject response = client.createRequest(MethodType.put, "/api/authorization/roles/" + roleId)
                .setData(request)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        Role role = roleRepository.findByName(request.getName());
        assertThat("角色名称检查", request.getName(), is(role.getName()));
    }

    @Test(dependsOnMethods = "testCreateRole")
    public void testUpdateRole_NameExist() {
        String name = roleRepository.findById(roleId2).get().getName();
        RoleEditRequest request = new RoleEditRequest();
        request.setName(name);

        JSONObject response = client.createRequest(MethodType.put, "/api/authorization/roles/" + roleId)
                .setData(request)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.ROLE_NAME_EXISTS.getCode()));
    }

    @Test
    public void testUpdateRole_IdNotExist() {
        RoleEditRequest request = new RoleEditRequest();
        request.setName("CEO");

        JSONObject response = client.createRequest(MethodType.put, "/api/authorization/roles/0")
                .setData(request)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.ROLE_NOT_EXISTS.getCode()));
    }

    @Test(dependsOnMethods = "testUpdateRole")
    public void testDeleteRole() {
        JSONObject response = client.createRequest(MethodType.delete, "/api/authorization/roles/" + roleId)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        assertThat("数据库字段更新检查", roleRepository.findById(roleId).isPresent(), is(false));
    }

    @Test
    public void testDeleteRole_IdNotExist() {
        JSONObject response = client.createRequest(MethodType.delete, "/api/authorization/roles/0")
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.ROLE_NOT_EXISTS.getCode()));
    }

    @Test(dependsOnMethods = "testSaveRolePermission")
    public void testGetRolePermission() {
        JSONObject response = client.createRequest(MethodType.get,
                "/api/authorization/roles/" + roleId2 + "/permissions")
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
        RolePermissionDTO dto = response.getObject("data", RolePermissionDTO.class);
        assertThat("响应code检查", dto.getBackend().get(0).getCode(), is(MenuCode.organizationManage));
    }

    @Test
    public void testGetRolePermission_RoleNotExist() {
        JSONObject response = client.createRequest(MethodType.get, "/api/authorization/roles/0/permissions")
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.ROLE_NOT_EXISTS.getCode()));
    }

    @Test
    public void testSaveRolePermission() {
        RolePermissionDTO dto = this.createRolePermissionDTO();
        JSONObject response = client.createRequest(MethodType.post,
                "/api/authorization/roles/" + roleId2 + "/permissions").setData(dto)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.SUCCESS.getCode()));
    }

    @Test
    public void testSaveRolePermission_IdNotExist() {
        RolePermissionDTO dto = new RolePermissionDTO();
        JSONObject response = client.createRequest(MethodType.post,
                "/api/authorization/roles/0/permissions").setData(dto)
                .sendRequset().getResponse();

        assertThat("响应code检查", response.getString("code"), is(ResultCode.ROLE_NOT_EXISTS.getCode()));
    }

    private RolePermissionDTO createRolePermissionDTO() {
        RolePermissionDTO dto = new RolePermissionDTO();
        PermissionMetaDataDTO pmd = new PermissionMetaDataDTO();
        pmd.setPlatformType(PlatformType.backend);
        pmd.setType(MenuType.catalogue);
        pmd.setCode(MenuCode.organizationManage);
        pmd.setName("组织机构管理");

        List<OrganizationEntity> organizations = organizationRepository.findAllByParentIdAndEnable(-1, YesNo.YES);
        //读权限
        PermissionDTO permissionGet = new PermissionDTO();
        permissionGet.setAction(ActionType.GET);
        ConditionDTO conditionGet = new ConditionDTO();
        conditionGet.setField("organizationId");
        conditionGet.setOp("include");
        conditionGet.setValue(organizations.get(0).getId().toString());
        permissionGet.setConditions(Lists.newArrayList(conditionGet));
        //写权限
        PermissionDTO permissionEdit = new PermissionDTO();
        permissionEdit.setAction(ActionType.EDIT);
        ConditionDTO conditionEdit = new ConditionDTO();
        conditionEdit.setField("organizationId");
        conditionEdit.setOp("include");
        conditionEdit.setValue(organizations.get(0).getId().toString());
        permissionEdit.setConditions(Lists.newArrayList(conditionEdit));

        List<PermissionDTO> permissions = Lists.newArrayList(permissionGet, permissionEdit);
        pmd.setPermissions(permissions);
        List<PermissionMetaDataDTO> list = Lists.newArrayList(pmd);
        dto.setBackend(list);
        return dto;
    }

}