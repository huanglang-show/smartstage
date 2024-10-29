package com.hl.scoring;


import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 评分策略配置注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScoringStrategyConfig {

    /**
     * 评分类型，0-得分类，1-测评类
     * @return type
     */
    int type();

    /**
     * 评分策略，0-自定义，1-AI
     * @return scoringStrategy
     */
    int scoringStrategy();


}
