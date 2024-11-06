package com.hl.model.enums;

import lombok.Getter;

@Getter
public enum ScoringStrategyEnum {

    CUSTOMIZE(0,"自定义"),
    AI(1,"AI");

    private final Integer type;

    private final String value;
    ScoringStrategyEnum(Integer type, String value){
        this.type = type;
        this.value = value;
    }
}
