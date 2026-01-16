package com.winesasfood.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.winesasfood.admin.common.serialize.PhoneDesensitizationSerializer;
import lombok.Data;

/**
 * 脱密后的用户信息
 */
@Data
public class UserRespDTO {

    private Long id;

    private String username;

    private String realName;

    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    private String mail;
}
