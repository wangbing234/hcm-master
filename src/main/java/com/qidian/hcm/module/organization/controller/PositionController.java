package com.qidian.hcm.module.organization.controller;

import com.qidian.hcm.common.utils.Result;
import com.qidian.hcm.common.utils.ResultGenerator;
import com.qidian.hcm.module.organization.request.CreatePositionRequest;
import com.qidian.hcm.module.organization.request.EnablePositionRequest;
import com.qidian.hcm.module.organization.request.UpdatePositionRequest;
import com.qidian.hcm.module.organization.response.PositionResponse;
import com.qidian.hcm.module.organization.service.PositionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/position")
@Api(tags = "组织机构--岗位相关")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @ApiOperation("获取岗位信息分页列表")
    @GetMapping()
    @PreAuthorize("hasAuthority('backend|position|GET') or hasRole('ROLE_ADMIN')")
    public Result getPositionList(@RequestParam(value = "active") Integer active,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "pageNo") Integer pageNo,
                                  @RequestParam(value = "pageSize") Integer pageSize) {
        return ResultGenerator.genSuccessResult(positionService.getPositionList(active, keyword, pageNo, pageSize));
    }

    @ApiOperation(value = "首页岗位结构tree")
    @GetMapping(value = "/tree")
    public Result tree() {
        return ResultGenerator.genSuccessResult(positionService.getPositionTree());
    }

    @ApiOperation(value = "岗位选项列表")
    @ApiImplicitParam(name = "departmentId", value = "部门ID", required = true, dataType = "int", paramType = "query")
    @GetMapping(value = "/options")
    public Result<List<PositionResponse>> option(@RequestParam Long departmentId) {
        return ResultGenerator.genSuccessResult(positionService.listPositions(departmentId));
    }

    @ApiOperation("新建岗位")
    @PostMapping()
    public Result createPosition(@RequestBody @Valid CreatePositionRequest createPositionRequest) {
        positionService.createPosition(createPositionRequest);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("删除岗位")
    @DeleteMapping("/{id}")
    public Result deletePositionById(@PathVariable Long id) {
        positionService.deletePositionById(id);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("获取岗位信息单条列表")
    @GetMapping("/{id}")
    public Result<PositionResponse> getPositionById(@PathVariable Long id) {
        return ResultGenerator.genSuccessResult(positionService.getPositionById(id));
    }

    @ApiOperation("修改岗位信息")
    @PutMapping("/{id}")
    public Result updatePosition(@PathVariable Long id,
                                 @RequestBody @Valid UpdatePositionRequest updatePositionRequest) {
        updatePositionRequest.setId(id);
        positionService.updatePositionById(updatePositionRequest);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("失效岗位")
    @PutMapping("/enable")
    public Result enablePositionById(@RequestBody EnablePositionRequest request) {
        positionService.toggleActivePositions(request);
        return ResultGenerator.genSuccessResult();
    }

}
