package com.hl.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum ReviewStatusEnum {

    REVIEWING(0,"待审核"),
    PASS(1,"通过"),
    REJECT(2,"拒绝");

    private final int status;

    private final String value;

    ReviewStatusEnum(int status, String value)
    {
        this.status = status;
        this.value = value;
    }

    /**
     * 根据状态码获取枚举值
     * @param status 状态
     * @return 枚举值
     */
    public static ReviewStatusEnum getValueByStatus(int status)
    {
        for (ReviewStatusEnum anEnum : ReviewStatusEnum.values())
        {
            if (anEnum.status == status)
            {
                return anEnum;
            }
        }
        return null;
    }


    /**
     * 获取所有审核状态的值
     * @return 包含所有审核状态值的List集合
     */
    public static List<Integer> getValues(){
        return Arrays.stream(ReviewStatusEnum.values()).map(ReviewStatusEnum::getStatus).collect(Collectors.toList());
    }

}
