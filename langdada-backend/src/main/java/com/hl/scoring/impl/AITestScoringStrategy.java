package com.hl.scoring.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hl.common.ErrorCode;
import com.hl.constant.UserConstant;
import com.hl.exception.BusinessException;
import com.hl.exception.ThrowUtils;
import com.hl.manager.AiManager;
import com.hl.model.dto.question.QuestionAnswerDTO;
import com.hl.model.dto.question.QuestionFrameWork;
import com.hl.model.entity.App;
import com.hl.model.entity.Question;
import com.hl.model.entity.UserAnswer;
import com.hl.model.enums.AppTypeEnum;
import com.hl.model.vo.QuestionVO;
import com.hl.scoring.ScoringStrategy;
import com.hl.scoring.ScoringStrategyConfig;
import com.hl.service.QuestionService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 自定义测评类评分策略
 */
@ScoringStrategyConfig(type = 1,scoringStrategy = 1)
public class AITestScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        if(!Objects.equals(app.getScoringStrategy(), AppTypeEnum.TEST_TYPE.getType())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"非测评类应用暂不支持AI生成结果！");
        }
        Long appId = app.getId();
        // 1. 根据 id 查询到题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 2. AI模型调用
        //  封装用户信息
        String userMessage = getAiTestScoringUserMessage(app, questionVO.getContent(), choices);
        String request = aiManager.doSyncStableRequest(UserConstant.SCORE_QUESTION_SYSTEM_MESSAGE, userMessage);
        // 结果处理，去除多余符号
        int start = request.indexOf("{");
        int end = request.indexOf("}");
        String json = request.substring(start, end + 1);
        UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
        // 3. 构造返回值，填充答案对象的属性
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        return userAnswer;
    }

    private String getAiTestScoringUserMessage(App app, List<QuestionFrameWork> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

}
