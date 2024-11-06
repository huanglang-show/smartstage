package com.hl.model.vo;

import cn.hutool.json.JSONUtil;
import com.hl.model.dto.question.QuestionFrameWork;
import com.hl.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目视图
 *
 *
 *
 */
@Data
public class QuestionVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private List<QuestionFrameWork> content;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param questionVO vo
     * @return question
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<QuestionFrameWork> tagList = questionVO.getContent();
        question.setQuestionContent(JSONUtil.toJsonStr(tagList));
        return question;
    }

    /**
     * 对象转封装类
     *
     * @param question question
     * @return vo
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setContent(JSONUtil.toList(question.getQuestionContent(), QuestionFrameWork.class));
        return questionVO;
    }
}
