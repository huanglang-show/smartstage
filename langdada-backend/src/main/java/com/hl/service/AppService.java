package com.hl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hl.model.dto.app.AppAddRequest;
import com.hl.model.dto.app.AppEditRequest;
import com.hl.model.dto.app.AppQueryRequest;
import com.hl.model.dto.app.AppUpdateRequest;
import com.hl.model.entity.App;
import com.hl.model.vo.AppVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 应用服务
 *
 * 
 * 
 */
public interface AppService extends IService<App> {

    /**
     * 校验数据
     *
     * @param app
     * @param add 对创建的数据进行校验
     */
    void validApp(App app, boolean add);

    /**
     * 添加应用
     *
     * @param appAddRequest 数据
     * @return id
     */
    Long addApp(AppAddRequest appAddRequest);


    /**
     * 删除应用程序
     *
     * @param id 应用程序的唯一标识符
     * @return 如果删除成功，则返回true；否则返回false
     */
    boolean deleteApp(Long id);

    /**
     * 更新应用程序信息
     *
     * @param appUpdateRequest 包含要更新的应用程序信息的请求对象
     * @return 如果更新成功，则返回true；否则返回false
     */
    boolean reviewApp(AppUpdateRequest appUpdateRequest);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest);

    Page<AppVO> listAppByPage(AppQueryRequest appQueryRequest);

    /**
     * 获取应用封装
     *
     * @param id id
     * @return
     */
    AppVO getAppVO(Long id);

    Page<AppVO> listMyAppVOByPage(AppQueryRequest appQueryRequest);

    boolean editApp(AppEditRequest appEditRequest);
}
