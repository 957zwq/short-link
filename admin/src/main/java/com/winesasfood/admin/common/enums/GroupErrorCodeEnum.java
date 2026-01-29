package com.winesasfood.admin.common.enums;

import com.winesasfood.admin.common.errorcode.IErrorCode;

/**
 * 短链接分组错误码枚举
 */
public enum GroupErrorCodeEnum implements IErrorCode {

    GROUP_NULL("B000300", "分组记录不存在"),

    GROUP_NAME_EXIST("B000301", "分组名称已存在"),

    GROUP_SAVE_ERROR("B000302", "分组记录新增失败"),

    GROUP_USER_NOT_EXIST("B000303", "用户不存在");

    private final String code;

    private final String message;

    GroupErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
