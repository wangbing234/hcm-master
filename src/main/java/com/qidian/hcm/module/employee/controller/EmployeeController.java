package com.qidian.hcm.module.employee.controller;

import com.qidian.hcm.common.interceptor.TenantThreadHelper;
import com.qidian.hcm.common.redis.RedisService;
import com.qidian.hcm.common.utils.Result;
import com.qidian.hcm.module.authorization.dto.RolePermissionDTO;
import com.qidian.hcm.module.employee.dto.*;
import com.qidian.hcm.module.employee.entity.Employee;
import com.qidian.hcm.module.employee.request.*;
import com.qidian.hcm.module.employee.response.EmployeeBaseInfoResponse;
import com.qidian.hcm.module.employee.response.MeRolePermissionResponse;
import com.qidian.hcm.module.employee.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.qidian.hcm.common.utils.ResultGenerator.genSuccessResult;

/**
 * @author lyn
 * @date 2018/8/14 17 04
 */
@RestController
@Slf4j
@RequestMapping("/api/employees")
@Api(tags = "人事管理--员工管理")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeePermissionService employeePermissionService;

    @Autowired
    private EmployeeHistoryService employeeHistoryService;

    @Autowired
    private EmployeeContractService employeeContractService;

    @Autowired
    private EmployeeIdentityService employeeIdentityService;

    @Autowired
    private EmployeeContactService employeeContactService;

    @Autowired
    private EmployeeEmergencyContactService employeeEmergencyContactService;

    @Autowired
    private EmployeeEducationService employeeEducationService;

    @Autowired
    private EmployeeWorkExperienceService employeeWorkExperienceService;

    @Autowired
    private RedisService redisService;

    @ApiOperation(value = "员工列表", notes = "员工列表")
    @PreAuthorize("hasAuthority('backend|employeeManage|GET') or hasRole('ROLE_ADMIN')")
    @GetMapping
    public Result<Page<FilterEmployeeDTO>> pageEmployees(FilterEmployeeRequest filterEmployeeRequest) {
        Page<FilterEmployeeDTO> pageBean = employeeService.pageEmployees(filterEmployeeRequest);
        return genSuccessResult(pageBean);
    }

    @ApiOperation("员工转正")
    @PutMapping("/{id}/qualify")
    public Result qualifyEmployee(@PathVariable Long id,
                                  @RequestBody @Valid QualifyEmployeeRequest qualifyEmployeeRequest) {
        employeeService.qualifyEmployeeById(qualifyEmployeeRequest, id);
        return genSuccessResult();
    }

    @ApiOperation("员工调岗")
    @PutMapping("/{id}/transfer")
    public Result transferEmployee(@PathVariable Long id,
                                   @RequestBody @Valid TransferEmployeeRequest transferEmployeeRequest) {
        employeeService.transferEmployeeById(transferEmployeeRequest, id);
        return genSuccessResult();
    }

    @ApiOperation("员工离职")
    @PutMapping("/{id}/resignation")
    public Result resignEmployee(@PathVariable Long id,
                                 @RequestBody @Valid ResignEmployeeRequest resignEmployeeRequest) {
        employeeService.resignEmployeeById(resignEmployeeRequest, id);
        return genSuccessResult();
    }

    @ApiOperation("获取员工离职单条记录")
    @GetMapping(value = "/{id}/resignation")
    public Result getResignEmployee(@PathVariable Long id) {
        return genSuccessResult(employeeService.findResignedEmployeeById(id));
    }

    @ApiOperation(value = "获取员工历史", notes = "获取员工历史")
    @ApiImplicitParam(name = "id", value = "自定义字段ID", dataType = "long", paramType = "path", required = true)
    @GetMapping(value = "{id}/history")
    public Result<List<EmployeeHistoryDTO>> getEmployeeHistory(@PathVariable Long id) {
        return genSuccessResult(employeeHistoryService.findEmployeeHistory(id));
    }


    @ApiOperation(value = "获取员工列表选项", notes = "获取员工列表选项")
    @GetMapping(value = "option")
    public Result<List<CommonListDTO>> findAllEmployees() {
        return genSuccessResult(employeeService.findAllEmployees());
    }


    @ApiOperation(value = "员工入职", notes = "员工入职")
    @PostMapping(value = "")
    public Result addEmployee(@RequestBody @Valid EmployeeRequest addEmployeeRequest) {
        employeeService.addEmployees(addEmployeeRequest);
        return genSuccessResult();
    }

    @ApiOperation(value = "获取员工详情", notes = "获取员工详情")
    @GetMapping(value = "{id}")
    @ApiImplicitParam(name = "id", value = "自定义表单ID", dataType = "long", paramType = "path", required = true)
    public Result<EmployeeRequest> getEmployee(@PathVariable Long id) {
        return genSuccessResult(employeeService.getEmployeeDetail(id));
    }


    @ApiOperation(value = "更新员工-基本信息")
    @PutMapping(value = "{id}/basic")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeeBasicInfo(@PathVariable Long id,
                                        @RequestBody @Valid EmployeeBasicRequest addEmployeeBasicRequest) {
        employeeService.saveEmployeeBasicInfo(id, addEmployeeBasicRequest);
        return genSuccessResult();
    }

    @ApiOperation(value = "更新员工-自定义字段")
    @PutMapping(value = "{id}/customize")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeeCustomize(@PathVariable Long id,
                                        @RequestBody @Valid EmployeeCustomizedFormsDTO employeeCustomizedFormsDTO) {
        employeeService.saveEmployeeCustomize(id, employeeCustomizedFormsDTO);
        return genSuccessResult();
    }

    @ApiOperation(value = "更新员工-职位信息")
    @PutMapping(value = "{id}/position")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeePosition(@PathVariable Long id,
                                       @RequestBody @Valid List<EmployeePositionDTO> employeePositionDTOList) {
        employeeService.saveEmployeePosition(id, employeePositionDTOList);
        return genSuccessResult();
    }

    @ApiOperation(value = "更新员工-工作信息")
    @PutMapping(value = "{id}/job")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeeJob(@PathVariable Long id,
                                  @RequestBody @Valid EmployeeJobDTO employeeJobDTO) {
        employeeService.saveEmployeeJob(id, employeeJobDTO);
        return genSuccessResult();
    }

    @ApiOperation(value = "更新员工-合同信息")
    @PutMapping(value = "{id}/contract")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeeContract(@PathVariable Long id,
                                       @RequestBody @Valid List<EmployeeContractDTO> employeeContractDTOList) {
        employeeContractService.saveEmployeeContract(id, employeeContractDTOList);
        return genSuccessResult(employeeContractDTOList);
    }

    @ApiOperation(value = "更新员工-证件信息")
    @PutMapping(value = "{id}/identity")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeeIdentity(@PathVariable Long id,
                                       @RequestBody @Valid List<EmployeeIdentityDTO> employeeIdentityDTOList) {
        employeeIdentityService.saveEmployeeIdentity(id, employeeIdentityDTOList);
        return genSuccessResult(employeeIdentityDTOList);
    }

    @ApiOperation(value = "更新员工-联系信息")
    @PutMapping(value = "{id}/contact")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeeContact(@PathVariable Long id,
                                      @RequestBody @Valid List<EmployeeContactDTO> employeeIdentityDTOList) {

        employeeContactService.saveEmployeeContact(id, employeeIdentityDTOList);
        return genSuccessResult(employeeIdentityDTOList);
    }

    @ApiOperation(value = "更新员工-紧急联系人")
    @PutMapping(value = "{id}/emergency_contact")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeeEmergencyContact(@PathVariable Long id,
                                               @RequestBody @Valid List<EmployeeEmergencyContactDTO>
                                                       employeeEmergencyContactDTOList) {
        employeeEmergencyContactService.saveEmployeeEmergencyContact(id, employeeEmergencyContactDTOList);
        return genSuccessResult(employeeEmergencyContactDTOList);
    }

    @ApiOperation(value = "更新员工-教育信息")
    @PutMapping(value = "{id}/education")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeeEducation(@PathVariable Long id,
                                        @RequestBody @Valid List<EmployeeEducationDTO> employeeEducationDTOList) {
        employeeEducationService.saveEmployeeEducation(id, employeeEducationDTOList);
        return genSuccessResult(employeeEducationDTOList);
    }

    @ApiOperation(value = "更新员工-工作经历信息")
    @PutMapping(value = "{id}/work_experience")
    @ApiImplicitParam(name = "id", value = "员工Id", dataType = "long", paramType = "path", required = true)
    public Result saveEmployeeWorkExperience(@PathVariable Long id, @RequestBody @Valid
            List<EmployeeWorkExperienceDTO> employeeWorkExperienceDTOS) {
        employeeWorkExperienceService.saveEmployeeWorkExperience(id, employeeWorkExperienceDTOS);
        return genSuccessResult(employeeWorkExperienceDTOS);
    }

    @ApiOperation(value = "获取员工个人信息")
    @GetMapping("/me")
    public Result<EmployeeBaseInfoResponse> me() {
        Employee employee = employeeService.findByUserId(TenantThreadHelper.getToken().getId());
        EmployeeBaseInfoResponse employeeBaseInfo = new EmployeeBaseInfoResponse();
        BeanUtils.copyProperties(employee, employeeBaseInfo);
        return genSuccessResult(employeeBaseInfo);
    }

    @ApiOperation(value = "获取员工权限信息")
    @GetMapping("/me/permission")
    public Result<MeRolePermissionResponse> getPermission() {
        Employee employee = employeeService.findByUserId(TenantThreadHelper.getToken().getId());
        RolePermissionDTO permissionInfo = employeePermissionService.getEmployeePermission(employee.getId());
        //缓存权限
        redisService.set("user:" + employee.getUserId() + ":permission", permissionInfo, 30 * 24 * 3600L);
        MeRolePermissionResponse response = new MeRolePermissionResponse(employee.isSuperAdmin(), permissionInfo);
        return genSuccessResult(response);
    }
}
