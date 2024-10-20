package com.hl.model.enums;


import lombok.Getter;

@Getter
public enum AppTypeEnum {

    SCORE_TYPE(0,"得分类"),
    TEST_TYPE(1,"测评类");

    private final Integer type;

    private final String value;
    AppTypeEnum(Integer type, String value){
        this.type = type;
        this.value = value;
    }
}
