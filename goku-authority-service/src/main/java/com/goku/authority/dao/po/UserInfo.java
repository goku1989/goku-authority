package com.goku.authority.dao.po;

import com.goku.foundation.utils.BasePO;
import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "user_info")
public class UserInfo extends BasePO {
    private String nickName;

    private String userName;

    private String userPassword;

    private String userEmail;

    private String userPhone;
}
