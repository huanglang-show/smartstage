package com.hl.scoring;

import com.hl.common.ErrorCode;
import com.hl.exception.BusinessException;
import com.hl.model.entity.App;
import com.hl.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评分策略执行器
 */
@Service
public class ScoringStrategyExecutor {

    @Resource
    private List<ScoringStrategy> scoringStrategyList;

    /**
     * 执行评分策略
     * 根据应用类型和评分策略，执行不同的评分策略
     * @param choices 用户答案
     * @param app 应用
     * @return 用户答案
     */
    public UserAnswer doScore(List<String> choices, App app) {
        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();
        if (appType == null || appScoringStrategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }
        for (ScoringStrategy scoringStrategy : scoringStrategyList) {
            if(scoringStrategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)){
                ScoringStrategyConfig annotation = scoringStrategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                if(annotation.type() == appType && annotation.scoringStrategy() == appScoringStrategy){
                    return scoringStrategy.doScore(choices,app);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
