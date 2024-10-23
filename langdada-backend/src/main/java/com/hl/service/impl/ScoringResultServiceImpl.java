package com.hl.service.impl;
import java.util.Date;
import java.util.*;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hl.common.ErrorCode;
import com.hl.constant.CommonConstant;
import com.hl.exception.ThrowUtils;
import com.hl.mapper.ScoringResultMapper;
import com.hl.model.dto.scoringResult.ScoringResultQueryRequest;
import com.hl.model.entity.*;
import com.hl.model.entity.ScoringResult;
import com.hl.model.entity.ScoringResult;
import com.hl.model.enums.AppTypeEnum;
import com.hl.model.vo.ScoringResultVO;
import com.hl.model.vo.UserVO;
import com.hl.service.AppService;
import com.hl.service.ScoringResultService;
import com.hl.service.UserService;
import com.hl.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 测评结果服务实现
 *
 *
 *
 */
@Service
@Slf4j
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResult> implements ScoringResultService {

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    /**
     * 校验数据
     *
     * @param scoringResult scoringResult
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validScoringResult(ScoringResult scoringResult, boolean add) {
        ThrowUtils.throwIf(scoringResult == null, ErrorCode.PARAMS_ERROR);

        // 从对象中取值
        String resultName = scoringResult.getResultName();
        String resultDesc = scoringResult.getResultDesc();
        String resultProp = scoringResult.getResultProp();
        Integer resultScoreRange = scoringResult.getResultScoreRange();
        Long appId = scoringResult.getAppId();

        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(resultName), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isBlank(resultDesc), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isBlank(resultProp), ErrorCode.PARAMS_ERROR);
        }
        // 通过应用id查询应用
        App app = appService.getById(appId);
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR);
        // 得分类-resultScoreRange不能为空
        if (Objects.equals(app.getAppType(), AppTypeEnum.SCORE_TYPE.getType())) {
            ThrowUtils.throwIf(resultScoreRange == null, ErrorCode.PARAMS_ERROR);
        }
        // 测评类-resultProp不能为空
        if (Objects.equals(app.getAppType(), AppTypeEnum.TEST_TYPE.getType())) {
            ThrowUtils.throwIf(StringUtils.isBlank(resultProp), ErrorCode.PARAMS_ERROR);
        }

        // 修改数据时，有参数则校验
        if (StringUtils.isNotBlank(resultDesc)) {
            ThrowUtils.throwIf(resultDesc.length() > 80, ErrorCode.PARAMS_ERROR, "描述过长");
        }
        if (StringUtils.isNotBlank(resultName)) {
            ThrowUtils.throwIf(resultName.length() > 15, ErrorCode.PARAMS_ERROR, "结果名称过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param scoringResultQueryRequest scoringResultQueryRequest
     * @return queryWrapper
     */
    @Override
    public QueryWrapper<ScoringResult> getQueryWrapper(ScoringResultQueryRequest scoringResultQueryRequest) {
        QueryWrapper<ScoringResult> queryWrapper = new QueryWrapper<>();
        if (scoringResultQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
         Long id = scoringResultQueryRequest.getId();
         String resultName = scoringResultQueryRequest.getResultName();
         String resultProp = scoringResultQueryRequest.getResultProp();
         Integer resultScoreRange = scoringResultQueryRequest.getResultScoreRange();
         Long appId = scoringResultQueryRequest.getAppId();
         Long userId = scoringResultQueryRequest.getUserId();

         String sortField = scoringResultQueryRequest.getSortField();
         String sortOrder = scoringResultQueryRequest.getSortOrder();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(resultName), "resultName", resultName);
        queryWrapper.like(StringUtils.isNotBlank(resultProp), "resultProp", resultProp);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultScoreRange), "resultScoreRange", resultScoreRange);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取测评结果封装
     *
     * @param scoringResult scoringResult
     * @param request request
     * @return vo
     */
    @Override
    public ScoringResultVO getScoringResultVO(ScoringResult scoringResult, HttpServletRequest request) {
        // 对象转封装类
        ScoringResultVO scoringResultVO = ScoringResultVO.objToVo(scoringResult);
        // 关联查询用户信息
        Long userId = scoringResult.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        scoringResultVO.setUser(userVO);
        return scoringResultVO;
    }

    /**
     * 分页获取测评结果封装
     *
     * @param scoringResultPage scoringResultPage
     * @param request  request
     * @return page
     */
    @Override
    public Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringResultPage, HttpServletRequest request) {
        List<ScoringResult> scoringResultList = scoringResultPage.getRecords();
        Page<ScoringResultVO> scoringResultVOPage = new Page<>(scoringResultPage.getCurrent(), scoringResultPage.getSize(), scoringResultPage.getTotal());
        if (CollUtil.isEmpty(scoringResultList)) {
            return scoringResultVOPage;
        }
        // 对象列表 => 封装对象列表
        List<ScoringResultVO> scoringResultVOList = scoringResultList.stream().map(ScoringResultVO::objToVo).collect(Collectors.toList());

        // 关联查询用户信息
        Set<Long> userIdSet = scoringResultList.stream().map(ScoringResult::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        scoringResultVOList.forEach(scoringResultVO -> {
            Long userId = scoringResultVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            scoringResultVO.setUser(userService.getUserVO(user));
        });
        scoringResultVOPage.setRecords(scoringResultVOList);
        return scoringResultVOPage;
    }

}
