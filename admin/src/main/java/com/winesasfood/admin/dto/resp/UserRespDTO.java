package com.winesasfood.admin.dto.resp;

import lombok.Data;

@Data
public class UserRespDTO {

    private Long id;

    private String username;

    private String realName;

    private String phone;

    private String mail;
}
