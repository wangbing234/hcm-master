package com.qidian.hcm.module.salary.controller;

import com.qidian.hcm.common.utils.Result;
import com.qidian.hcm.module.salary.dto.BankInfoDTO;
import com.qidian.hcm.module.salary.dto.SalaryItemAccountingDTO;
import com.qidian.hcm.module.salary.request.*;
import com.qidian.hcm.module.salary.response.EmployeeSalaryHistoryResponse;
import com.qidian.hcm.module.salary.response.EmployeeBasicInfoResponse;
import com.qidian.hcm.module.salary.response.EmployeeSalaryDetailResponse;
import com.qidian.hcm.module.salary.response.EmployeeSecurityDetailResponse;
import com.qidian.hcm.module.salary.response.SalaryItemMonthlyResponse;
import com.qidian.hcm.module.salary.response.EmployeeSalaryPageResponse;
import com.qidian.hcm.module.salary.service.EmployeeSalaryMonthlyService;
import com.qidian.hcm.module.salary.service.EmployeeSalaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.qidian.hcm.common.utils.ResultGenerator.genSuccessResult;

@RestController
@Slf4j
@RequestMapping("/api/salaries")
@Api(tags = "薪酬管理--员工信息")
public class EmployeeSalaryController {


    @Autowired
    private EmployeeSalaryService employeeSalaryService;

    @Autowired
    private EmployeeSalaryMonthlyService employeeSalaryMonthlyService;

    @ApiOperation(value = "获取薪酬历史记录")
    @GetMapping("employees/{employeeId}/history")
    public Result<List<EmployeeSalaryHistoryResponse>> getSalaryHistory(@PathVariable Long employeeId) {
        return genSuccessResult(employeeSalaryService.getSalaryHistory(employeeId));
    }

    @ApiOperation(value = "获取员工基本信息")
    @GetMapping("employees/{employeeId}/basic")
    public Result<EmployeeBasicInfoResponse> getEmployeeBasicInfo(@PathVariable Long employeeId) {
        return genSuccessResult(employeeSalaryService.getEmployeeBasicInfo(employeeId));
    }

    @ApiOperation(value = "查询员工--薪酬信息tab")
    @GetMapping("employees/{employeeId}/salary_info")
    public Result<EmployeeSalaryDetailResponse> getEmployeeSalaryInfo(@PathVariable Long employeeId) {
        return genSuccessResult(employeeSalaryService.getEmployeeSalaryInfo(employeeId));
    }

    @ApiOperation(value = "查询员工--五险一金tab")
    @GetMapping("employees/{employeeId}/security_info")
    public Result<EmployeeSecurityDetailResponse> getEmployeeSecurityDetail(@PathVariable Long employeeId) {
        return genSuccessResult(employeeSalaryService.getEmployeeSecurityDetail(employeeId));
    }


    @ApiOperation(value = "查询员工--本月明细tab")
    @GetMapping("employees/{employeeId}/monthly_detail")
    public Result<List<SalaryItemMonthlyResponse>> getEmployeeSalaryMonthly(@PathVariable Long employeeId) {
        return genSuccessResult(employeeSalaryMonthlyService.getSalaryMonthly(employeeId));
    }


    @ApiOperation(value = "编辑明细实时计算")
    @GetMapping("employees/{employeeId}/accounting")
    public Result<List<SalaryItemAccountingDTO>> getEmployeeAccounting(
            @PathVariable Long employeeId, @Valid SalaryItemAccountingDTO salaryItemMonthly) {
        return genSuccessResult(employeeSalaryMonthlyService.getEmployeeAccounting(employeeId, salaryItemMonthly));
    }

    @ApiOperation(value = "编辑本月明细")
    @PutMapping("employees/{employeeId}/monthly_detail")
    public Result saveDetailInfo(@PathVariable Long employeeId,
                                 @RequestBody @Valid List<SalaryItemMonthlyRequest> employeeSalaryMonthly) {
        employeeSalaryMonthlyService.saveDetailInfo(employeeId, employeeSalaryMonthly);
        return genSuccessResult();
    }

    @ApiOperation(value = "编辑编辑(不产生历史记录)")
    @PutMapping("employees/{employeeId}/edit")
    public Result editSalary(@PathVariable Long employeeId, @RequestBody @Valid SalaryEditRequest salaryEditRequest) {
        employeeSalaryService.editSalary(employeeId, salaryEditRequest);
        return genSuccessResult();
    }


    @ApiOperation(value = "薪酬调整(产生历史记录)")
    @PutMapping("employees/{employeeId}/adjust")
    public Result adjustSalary(@PathVariable Long employeeId, @RequestBody @Valid SalaryAdjustRequest salaryAdjust) {
        employeeSalaryService.adjustSalary(employeeId, salaryAdjust);
        return genSuccessResult();
    }

    @ApiOperation(value = "修改银行卡信息")
    @PutMapping("employees/{employeeId}/bank_info")
    public Result updateBankInfo(@PathVariable Long employeeId, @RequestBody @Valid BankInfoDTO bankInfoDTO) {
        employeeSalaryService.updateBankInfo(employeeId, bankInfoDTO);
        return genSuccessResult();
    }

    @ApiOperation(value = "修改社保方案")
    @PutMapping("employees/{employeeId}/social_security_plan")
    public Result editEmployeeSecurity(@PathVariable Long employeeId,
                                       @RequestBody @Valid EmployeeSecurityPlanRequest employeeSecurityPlanRequest) {
        employeeSalaryService.editEmployeeSecurity(employeeId, employeeSecurityPlanRequest);
        return genSuccessResult();
    }

    @ApiOperation(value = "修改公积金方案")
    @PutMapping("employees/{employeeId}/housing_fund_plans")
    public Result editEmployeeFundPlanInfo(@PathVariable Long employeeId,
                                           @RequestBody @Valid EmployeeHousingFundPlanRequest employeeHousingFundPlan) {
        employeeSalaryService.editEmployeeFundPlanInfo(employeeId, employeeHousingFundPlan);
        return genSuccessResult();
    }

    @ApiOperation(value = "修改免税额方案")
    @PutMapping("employees/{employeeId}/threshold")
    public Result editEmployeeThreshold(@PathVariable Long employeeId,
                                        @RequestBody @Valid EmployeeThresholdRequest employeeThresholdRequest) {
        employeeSalaryService.editEmployeeThreshold(employeeId, employeeThresholdRequest);
        return genSuccessResult();
    }

    @ApiOperation(value = "批量标记本月不记薪")
    @PutMapping("exclude")
    public Result salaryExclude(@RequestBody @Valid EmployeeListRequest ids) {
        employeeSalaryMonthlyService.salaryExclude(ids);
        return genSuccessResult();
    }

    @ApiOperation(value = "批量标记本月记薪")
    @PutMapping("include")
    public Result salaryInclude(@RequestBody @Valid EmployeeListRequest ids) {
        employeeSalaryMonthlyService.salaryInclude(ids);
        return genSuccessResult();
    }

    @ApiOperation(value = "获取本月员工薪酬列表")
    @GetMapping("employees")
    public Result<Page<EmployeeSalaryPageResponse>> getEmployeeSalaryPage(
            @Valid FilterEmployeesSalaryRequest filterEmployeesSalaryRequest) {
        log.info(filterEmployeesSalaryRequest.toString());
        return genSuccessResult(employeeSalaryMonthlyService.getEmployeeSalaryPage(filterEmployeesSalaryRequest));
    }

}
