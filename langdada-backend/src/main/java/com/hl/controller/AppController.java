package com.hl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.annotation.AuthCheck;
import com.hl.common.BaseResponse;
import com.hl.common.DeleteRequest;
import com.hl.common.ErrorCode;
import com.hl.common.ResultUtils;
import com.hl.constant.UserConstant;
import com.hl.exception.BusinessException;
import com.hl.exception.ThrowUtils;
import com.hl.model.dto.app.AppAddRequest;
import com.hl.model.dto.app.AppEditRequest;
import com.hl.model.dto.app.AppQueryRequest;
import com.hl.model.dto.app.AppUpdateRequest;
import com.hl.model.entity.App;
import com.hl.model.entity.User;
import com.hl.model.vo.AppVO;
import com.hl.service.AppService;
import com.hl.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 应用接口
 *
 * 
 * 
 */
@RestController
@RequestMapping("/app")
@Slf4j
@Api("应用管理")
public class AppController {

    @Resource
    private AppService appService;

    /**
     * 创建应用
     *
     * @param appAddRequest 数据
     * @return id
     */
    @ApiOperation(value = "创建应用")
    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody @Valid AppAddRequest appAddRequest) {
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        Long appId = appService.addApp(appAddRequest);
        ThrowUtils.throwIf(appId == null, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(appId);
    }
    /**
     * 删除应用
     *
     * @param deleteRequest 参数
     * @return 结果
     */
    @ApiOperation(value = "删除应用")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 操作数据库
        boolean result = appService.deleteApp(deleteRequest.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新应用
     *
     * @param appUpdateRequest 参数
     * @return 结果
     */
    @ApiOperation(value = "更新应用")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest) {
        if (appUpdateRequest == null || appUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = appService.updateApp(appUpdateRequest);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取应用（封装类）
     *
     * @param id id
     * @return 应用信息
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppVO> getAppVOById(Long id) {
        // 获取封装类
        return ResultUtils.success(appService.getAppVO(id));
    }

    /**
     * 分页获取应用列表
     *
     * @param appQueryRequest 请求参数
     * @return page
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AppVO>> listAppByPage(@RequestBody AppQueryRequest appQueryRequest) {
        Page<AppVO> appPage = appService.listAppByPage(appQueryRequest);
        return ResultUtils.success(appPage);
    }

    /**
     * 分页获取当前登录用户创建的应用列表
     *
     * @param appQueryRequest 请求参数
     * @return page
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AppVO>> listMyAppVOByPage(@RequestBody AppQueryRequest appQueryRequest) {
        // 获取封装类
        return ResultUtils.success(appService.listMyAppVOByPage(appQueryRequest));
    }
}
