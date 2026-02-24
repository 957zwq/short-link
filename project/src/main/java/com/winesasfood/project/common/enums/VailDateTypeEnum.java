package com.winesasfood.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 有效期类型枚举
 */
@RequiredArgsConstructor
public enum VailDateTypeEnum {

    /**
     * 永久有效期
     */
    PERMANENT(0),

    /**
     * 自定义有效期
     */
    CUSTOM(1);

    @Getter
    private final int type;

    /**
     * 根据类型值获取枚举
     *
     * @param type 类型值
     * @return 枚举对象，找不到返回 null
     */
    public static VailDateTypeEnum getByType(int type) {
        for (VailDateTypeEnum value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        return null;
    }

    /**
     * 判断是否为自定义有效期
     *
     * @param type 类型值
     * @return true-自定义，false-永久
     */
    public static boolean isCustom(int type) {
        return CUSTOM.type == type;
    }
}
