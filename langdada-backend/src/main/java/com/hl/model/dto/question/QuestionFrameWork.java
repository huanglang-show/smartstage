package com.hl.model.dto.question;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionFrameWork {
    private String title;
    private List<Option> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        /**
         * 测评类选项的结果
         */
        private String result;
        /**
         * 评分类测评选项的分数
         */
        private int score;
        /**
         * 选项的value,用于展示该选项的内容
         */
        private String value;
        /**
         * 选项的key,用于提交答案，如A,B
         */
        private String key;
    }
}
