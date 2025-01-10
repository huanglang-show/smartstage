package com.hl.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hl.annotation.AuthCheck;
import com.hl.common.BaseResponse;
import com.hl.common.DeleteRequest;
import com.hl.common.ErrorCode;
import com.hl.common.ResultUtils;
import com.hl.constant.UserConstant;
import com.hl.exception.BusinessException;
import com.hl.exception.ThrowUtils;
import com.hl.model.dto.userAnswer.UserAnswerAddRequest;
import com.hl.model.dto.userAnswer.UserAnswerEditRequest;
import com.hl.model.dto.userAnswer.UserAnswerQueryRequest;
import com.hl.model.dto.userAnswer.UserAnswerUpdateRequest;
import com.hl.model.entity.App;
import com.hl.model.entity.UserAnswer;
import com.hl.model.entity.User;
import com.hl.model.enums.ReviewStatusEnum;
import com.hl.model.vo.UserAnswerVO;
import com.hl.scoring.ScoringStrategy;
import com.hl.scoring.ScoringStrategyExecutor;
import com.hl.service.AppService;
import com.hl.service.UserAnswerService;
import com.hl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户答题接口
 *
 *
 *
 */
@RestController
@RequestMapping("/userAnswer")
@Slf4j
public class UserAnswerController {

    @Resource
    private UserAnswerService userAnswerService;

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    @Resource
    private ScoringStrategyExecutor scoringStrategyExecutor;

    /**
     * 创建用户答题
     *
     * @param userAnswerAddRequest 请求体
     * @param request 请求
     * @return 新写入的数据 id
     */
    @PostMapping("/add")
    @Transactional
    public BaseResponse<Long> addUserAnswer(@RequestBody UserAnswerAddRequest userAnswerAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userAnswerAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 将实体类和 DTO 进行转换
        UserAnswer userAnswer = new UserAnswer();
        BeanUtils.copyProperties(userAnswerAddRequest, userAnswer);
        userAnswer.setChoices(JSONUtil.toJsonStr(userAnswerAddRequest.getChoices()));
        // 数据校验
        userAnswerService.validUserAnswer(userAnswer, true);
        // 判断app是否存在，并且是审核通过状态
        App app = appService.getById(userAnswer.getAppId());
        ThrowUtils.throwIf(app == null || app.getReviewStatus() != ReviewStatusEnum.PASS.getStatus(), ErrorCode.PARAMS_ERROR, "应用不存在或未审核通过");
        // 填充默认值
        User loginUser = userService.getLoginUser(request);
        userAnswer.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = userAnswerService.save(userAnswer);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newUserAnswerId = userAnswer.getId();

        // 调用评分模块
        try {
            UserAnswer userAnswerWithResult = scoringStrategyExecutor.doScore(userAnswerAddRequest.getChoices(), app);
            userAnswerWithResult.setId(userAnswer.getId());
            userAnswerService.updateById(userAnswerWithResult);
        }catch (Exception e){
            log.error("评分模块调用失败",e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评分错误");
        }
        return ResultUtils.success(newUserAnswerId);
    }

    /**
     * 删除用户答题
     *
     * @param deleteRequest 请求体
     * @param request 请求
     * @return 结果
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserAnswer(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserAnswer oldUserAnswer = userAnswerService.getById(id);
        ThrowUtils.throwIf(oldUserAnswer == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserAnswer.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = userAnswerService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新用户答题（仅管理员可用）
     *
     * @param userAnswerUpdateRequest 请求体
     * @return 结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserAnswer(@RequestBody UserAnswerUpdateRequest userAnswerUpdateRequest) {
        if (userAnswerUpdateRequest == null || userAnswerUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        long id = userAnswerUpdateRequest.getId();
        UserAnswer oldUserAnswer = userAnswerService.getById(id);
        ThrowUtils.throwIf(oldUserAnswer == null, ErrorCode.NOT_FOUND_ERROR);
        // 将实体类和 DTO 进行转换
        UserAnswer userAnswer = new UserAnswer();
        BeanUtils.copyProperties(userAnswerUpdateRequest, userAnswer);
        userAnswer.setAppId(oldUserAnswer.getAppId());
        userAnswer.setChoices(JSONUtil.toJsonStr(userAnswerUpdateRequest.getChoices()));
        // 数据校验
        userAnswerService.validUserAnswer(userAnswer, false);
        // 操作数据库
        boolean result = userAnswerService.updateById(userAnswer);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户答题（封装类）
     *
     * @param id id
     * @return vo
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserAnswerVO> getUserAnswerVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        UserAnswer userAnswer = userAnswerService.getById(id);
        ThrowUtils.throwIf(userAnswer == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(userAnswerService.getUserAnswerVO(userAnswer, request));
    }

    /**
     * 分页获取用户答题列表（仅管理员可用）
     *
     * @param userAnswerQueryRequest 请求参数
     * @return page
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserAnswer>> listUserAnswerByPage(@RequestBody UserAnswerQueryRequest userAnswerQueryRequest) {
        long current = userAnswerQueryRequest.getCurrent();
        long size = userAnswerQueryRequest.getPageSize();
        // 查询数据库
        Page<UserAnswer> userAnswerPage = userAnswerService.page(new Page<>(current, size),
                userAnswerService.getQueryWrapper(userAnswerQueryRequest));
        return ResultUtils.success(userAnswerPage);
    }

    /**
     * 分页获取用户答题列表（封装类）
     *
     * @param userAnswerQueryRequest 请求参数
     * @param request request
     * @return page
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserAnswerVO>> listUserAnswerVOByPage(@RequestBody UserAnswerQueryRequest userAnswerQueryRequest,
                                                               HttpServletRequest request) {
        long current = userAnswerQueryRequest.getCurrent();
        long size = userAnswerQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserAnswer> userAnswerPage = userAnswerService.page(new Page<>(current, size),
                userAnswerService.getQueryWrapper(userAnswerQueryRequest));
        // 获取封装类
        return ResultUtils.success(userAnswerService.getUserAnswerVOPage(userAnswerPage, request));
    }

    /**
     * 分页获取当前登录用户创建的用户答题列表
     *
     * @param userAnswerQueryRequest 请求参数
     * @param request request
     * @return page
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<UserAnswerVO>> listMyUserAnswerVOByPage(@RequestBody UserAnswerQueryRequest userAnswerQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(userAnswerQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        userAnswerQueryRequest.setUserId(loginUser.getId());
        long current = userAnswerQueryRequest.getCurrent();
        long size = userAnswerQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserAnswer> userAnswerPage = userAnswerService.page(new Page<>(current, size),
                userAnswerService.getQueryWrapper(userAnswerQueryRequest));
        // 获取封装类
        return ResultUtils.success(userAnswerService.getUserAnswerVOPage(userAnswerPage, request));
    }

    /**
     * 编辑用户答题（给用户使用）
     *
     * @param userAnswerEditRequest 请求参数
     * @param request request
     * @return 结果
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editUserAnswer(@RequestBody UserAnswerEditRequest userAnswerEditRequest, HttpServletRequest request) {
        if (userAnswerEditRequest == null || userAnswerEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        long id = userAnswerEditRequest.getId();
        UserAnswer oldUserAnswer = userAnswerService.getById(id);
        ThrowUtils.throwIf(oldUserAnswer == null, ErrorCode.NOT_FOUND_ERROR);
        // 将实体类和 DTO 进行转换
        UserAnswer userAnswer = new UserAnswer();
        BeanUtils.copyProperties(userAnswerEditRequest, userAnswer);
        userAnswer.setAppId(oldUserAnswer.getAppId());
        userAnswer.setChoices(JSONUtil.toJsonStr(userAnswerEditRequest.getChoices()));
        // 数据校验
        userAnswerService.validUserAnswer(userAnswer, false);
        User loginUser = userService.getLoginUser(request);
        // 仅本人或管理员可编辑
        if (!oldUserAnswer.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = userAnswerService.updateById(userAnswer);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/generate/id")
    public BaseResponse<Long> generateUserAnswerId() {
        return ResultUtils.success(IdUtil.getSnowflakeNextId());
    }
}
