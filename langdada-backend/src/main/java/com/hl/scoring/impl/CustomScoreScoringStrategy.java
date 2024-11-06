package com.hl.scoring.impl;

import cn.hutool.json.JSONUtil;
import com.hl.model.dto.question.QuestionFrameWork;
import com.hl.model.entity.App;
import com.hl.model.entity.Question;
import com.hl.model.entity.ScoringResult;
import com.hl.model.entity.UserAnswer;
import com.hl.model.vo.QuestionVO;
import com.hl.scoring.ScoringStrategy;
import com.hl.scoring.ScoringStrategyConfig;
import com.hl.service.QuestionService;
import com.hl.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义得分类评分策略
 */
@ScoringStrategyConfig(type = 0,scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        Long appId = app.getId();
        // 1.查询题目和题目结果信息
        Question questions = questionService.lambdaQuery().eq(Question::getAppId, appId).one();
        List<ScoringResult> scoringResults = scoringResultService.lambdaQuery()
                .eq(ScoringResult::getAppId, appId).orderByDesc(ScoringResult::getResultScoreRange).list();

        QuestionVO questionVO = QuestionVO.objToVo(questions);
        // 2.计算用户得分
        int totalScore = getTotalScore(choices, questionVO);

        // 3.计算最高得分,默认是最低分
        ScoringResult maxResult = scoringResults.get(scoringResults.size() - 1);
        // 遍历题目结果
        for (ScoringResult scoringResult : scoringResults) {
            // 判断用户最高得分在题目结果中那个范围
            if (totalScore >= scoringResult.getResultScoreRange()) {
                // 获取用户得分
                maxResult = scoringResult;
                break;
            }
        }
        // 4.构建返回结果
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxResult.getId());
        userAnswer.setResultName(maxResult.getResultName());
        userAnswer.setResultDesc(maxResult.getResultDesc());
        userAnswer.setResultPicture(maxResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }

    /**
     * 计算用户总得分
     *
     * @param choices 用户选择的答案列表
     * @param questionVO 问题视图对象，包含问题的相关信息
     * @return 用户的总得分
     */
    private static int getTotalScore(List<String> choices, QuestionVO questionVO) {
        // 获取问题内容列表
        List<QuestionFrameWork> questionContent = questionVO.getContent();
        // 2.统计用户得分
        int totalScore = 0;
        // 遍历题目
        for (QuestionFrameWork questionFrameWork : questionContent) {
            // 遍历选择答案
            for (String choice : choices) {
                // 遍历题目选项
                for (QuestionFrameWork.Option option : questionFrameWork.getOptions()) {
                    // 判断用户答案是否和题目选项一致
                    if (choice.equals(option.getKey())) {
                        // 累加得分
                        totalScore += option.getScore();
                    }
                }
            }
        }
        // 返回总得分
        return totalScore;
    }
}
