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

    /**
     * 通过type获取value
     */
    public static AppTypeEnum getEnumByType(Integer type) {
        for (AppTypeEnum appTypeEnum : AppTypeEnum.values()) {
            if (appTypeEnum.getType().equals(type)) {
                return appTypeEnum;
            }
        }
        return null;
    }
}
