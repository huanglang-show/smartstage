package com.hl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hl.model.dto.scoringResult.ScoringResultQueryRequest;
import com.hl.model.entity.ScoringResult;
import com.hl.model.vo.ScoringResultVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 测评结果服务
 *
 *
 *
 */
public interface ScoringResultService extends IService<ScoringResult> {

    /**
     * 校验数据
     *
     * @param scoringResult
     * @param add 对创建的数据进行校验
     */
    void validScoringResult(ScoringResult scoringResult, boolean add);

    /**
     * 获取查询条件
     *
     * @param scoringResultQueryRequest
     * @return
     */
    QueryWrapper<ScoringResult> getQueryWrapper(ScoringResultQueryRequest scoringResultQueryRequest);
    
    /**
     * 获取测评结果封装
     *
     * @param scoringResult
     * @param request
     * @return
     */
    ScoringResultVO getScoringResultVO(ScoringResult scoringResult, HttpServletRequest request);

    /**
     * 分页获取测评结果封装
     *
     * @param scoringResultPage
     * @param request
     * @return
     */
    Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringResultPage, HttpServletRequest request);
}
