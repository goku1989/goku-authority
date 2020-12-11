package com.goku.authority.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDTO {
    private String nickName;

    private String userName;

    private String userPassword;

    private String userEmail;

    private String userPhone;

}
