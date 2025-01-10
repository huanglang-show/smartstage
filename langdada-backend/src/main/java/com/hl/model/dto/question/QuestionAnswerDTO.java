package com.hl.model.dto.question;

import lombok.Data;

/**
 * AI评分时的请求参数
 */
@Data
public class QuestionAnswerDTO {

    /**
     * 题目
     */
    private String title;

    /**
     * 用户答案
     */
    private String userAnswer;

}
