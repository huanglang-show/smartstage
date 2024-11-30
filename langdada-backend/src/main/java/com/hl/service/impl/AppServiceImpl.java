package com.hl.service.impl;
import java.util.Date;
import java.util.*;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hl.common.ErrorCode;
import com.hl.constant.CommonConstant;
import com.hl.exception.BusinessException;
import com.hl.exception.ThrowUtils;
import com.hl.mapper.AppMapper;
import com.hl.model.dto.app.AppAddRequest;
import com.hl.model.dto.app.AppEditRequest;
import com.hl.model.dto.app.AppQueryRequest;
import com.hl.model.dto.app.AppUpdateRequest;
import com.hl.model.entity.App;
import com.hl.model.entity.Question;
import com.hl.model.entity.ScoringResult;
import com.hl.model.entity.User;
import com.hl.model.vo.AppVO;
import com.hl.model.vo.QuestionVO;
import com.hl.model.vo.UserVO;
import com.hl.service.AppService;
import com.hl.service.UserService;
import com.hl.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 应用服务实现
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    /**
     *
     * @param appAddRequest 数据
     * @return id
     */
    @Override
    public Long addApp(AppAddRequest appAddRequest) {
        App app = new App();
        BeanUtils.copyProperties(appAddRequest, app);
        // 当前用户
        User user = userService.getLoginUserByRequest();
        app.setUserId(user.getId());
        validApp(app, true);
        boolean save = this.save(app);
        if(!save){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return app.getId();
    }

    /**
     * 删除应用
     * @param id id
     * @return 结果
     */
    @Override
    public boolean deleteApp(Long id) {
        // 判断是否存在
        App oldApp = this.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        User user = userService.getLoginUserByRequest();
        // 仅本人或管理员可删除
        if (!oldApp.getUserId().equals(user.getId()) && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return this.removeById(id);
    }

    @Override
    public boolean reviewApp(AppUpdateRequest appUpdateRequest) {
        // 判断是否存在
        long id = appUpdateRequest.getId();
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        app.setReviewerId(userService.getLoginUserByRequest().getId());
        app.setReviewStatus(appUpdateRequest.getReviewStatus());
        app.setReviewMessage(appUpdateRequest.getReviewMessage());
        app.setReviewTime(new Date());
        // 操作数据库
        return this.updateById(app);
    }

    @Override
    public boolean editApp(AppEditRequest appEditRequest) {
        // 判断是否存在
        long id = appEditRequest.getId();
        App oldApp = this.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        User user = userService.getLoginUserByRequest();
        // 仅本人或管理员可编辑
        if (!oldApp.getUserId().equals(user.getId()) && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        App app = new App();
        BeanUtils.copyProperties(appEditRequest, app);
        // 数据校验
        validApp(app, false);
        // 操作数据库
        return this.updateById(app);
    }

    /**
     * 下线应用
     * @param id id
     * @return 结果
     */
    @Override
    public boolean offlineApp(Long id) {
        App app = this.getById(id);
        if(ObjectUtils.isEmpty(app)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return this.removeById(app);
    }

    /**
     * 校验数据
     *
     * @param app  数据
     * @param add  是否对创建的数据进行校验
     */
    @Override
    public void validApp(App app, boolean add) {
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);
        Long id = app.getId();
        String appName = app.getAppName();
        String appDesc = app.getAppDesc();
        String appIcon = app.getAppIcon();
        Integer appType = app.getAppType();
        Integer scoringStrategy = app.getScoringStrategy();
        Integer reviewStatus = app.getReviewStatus();
        String reviewMessage = app.getReviewMessage();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(appName), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(appType == null, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(scoringStrategy == null, ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        if (StringUtils.isNotBlank(appName)) {
            ThrowUtils.throwIf(appName.length() > 15, ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(appDesc)) {
            ThrowUtils.throwIf(appDesc.length() > 100, ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param appQueryRequest 查询条件
     * @return queryWrapper
     */
    @Override
    public QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest) {
        QueryWrapper<App> queryWrapper = new QueryWrapper<>();
        if (appQueryRequest == null) {
            return queryWrapper;
        }
        String appName = appQueryRequest.getAppName();
        Integer appType = appQueryRequest.getAppType();
        Integer reviewStatus = appQueryRequest.getReviewStatus();
        Date reviewTime = appQueryRequest.getReviewTime();
        Date createTime = appQueryRequest.getCreateTime();
        String sortField = appQueryRequest.getSortField();
        Long userId = appQueryRequest.getUserId();
        String sortOrder = appQueryRequest.getSortOrder();
        // 构建查询条件
        queryWrapper.eq(StringUtils.isNotBlank(appName), "appName", appName)
                .eq(appType != null, "appType", appType)
                .eq(reviewStatus != null, "reviewStatus", reviewStatus)
                .eq(reviewTime != null, "reviewTime", reviewTime)
                .eq(userId != null, "userId", userId)
                .eq(createTime != null, "createTime", createTime)
                .eq("isDelete",0)
                .orderBy(StringUtils.isNotBlank(sortField), "asc".equalsIgnoreCase(sortOrder), sortField);
        return queryWrapper;
    }

    @Override
    public Page<AppVO> listAppByPage(AppQueryRequest appQueryRequest) {
        long current = appQueryRequest.getCurrent();
        long size = appQueryRequest.getPageSize();
        // 查询数据库
        Page<App> page = this.page(new Page<>(current, size),this.getQueryWrapper(appQueryRequest));
        List<App> records = page.getRecords();
        List<AppVO> appVOList = records.stream().map(AppVO::objToVo).collect(Collectors.toList());
        Page<AppVO> appVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        // 关联查询用户信息
        Set<Long> userIdSet = records.stream().map(App::getUserId).collect(Collectors.toSet());
        userIdSet.addAll(records.stream().map(App::getReviewerId).collect(Collectors.toSet()));
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        appVOList.forEach(appVO -> {
            Long userId = appVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
                appVO.setUser(userService.getUserVO(user));
            }
            if(userIdUserListMap.containsKey(appVO.getReviewerId())){
                appVO.setReviewerName(userIdUserListMap.get(appVO.getReviewerId()).get(0).getUserName());
            }
        });
        appVOPage.setRecords(appVOList);
        return appVOPage;
    }

    /**
     * 分页获取题目封装
     *
     * @param appPage page
     * @param request   request
     * @return page
     */
    @Override
    public Page<AppVO> getAppVOPage(Page<App> appPage, HttpServletRequest request) {
        List<App> appList = appPage.getRecords();
        Page<AppVO> appVOPage = new Page<>(appPage.getCurrent(), appPage.getSize(), appPage.getTotal());
        if (CollUtil.isEmpty(appList)) {
            return appVOPage;
        }
        // 对象列表 => 封装对象列表
        List<AppVO> appVOList = appList.stream().map(AppVO::objToVo).collect(Collectors.toList());

        // 1. 关联查询用户信息
        Set<Long> userIdSet = appList.stream().map(App::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        appVOList.forEach(appVO -> {
            Long userId = appVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            appVO.setUser(userService.getUserVO(user));
        });
        appVOPage.setRecords(appVOList);
        return appVOPage;
    }

    /**
     * 获取应用封装
     *
     * @param id 数据
     * @return appVo
     */
    @Override
    public AppVO getAppVO(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = this.getOne(Wrappers.lambdaQuery(App.class).eq(App::getId, id)
                .eq(App::getIsDelete, 0));
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 对象转封装类
        AppVO appVO = AppVO.objToVo(app);
        // 1. 关联查询用户信息
        Long userId = app.getUserId();
        Long reviewerId = app.getReviewerId();
        List<Long> ids = new ArrayList<>();
        if (userId != null && userId > 0) {
            ids.add(userId);
        }
        if (reviewerId != null && reviewerId > 0) {
            ids.add(reviewerId);
        }
        List<User> users = userService.listByIds(ids);
        appVO.setUserName(users.stream().filter(item -> Objects.equals(item.getId(), userId)).map(User::getUserName).findFirst().orElse(""));
        appVO.setReviewerName(users.stream().filter(item -> Objects.equals(item.getId(), reviewerId)).map(User::getUserName).findFirst().orElse(""));
        return appVO;
    }

    @Override
    public Page<AppVO> listMyAppVOByPage(AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUserByRequest();
        appQueryRequest.setUserId(loginUser.getId());
        return this.listAppByPage(appQueryRequest);
    }


}
