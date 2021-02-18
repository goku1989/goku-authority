package com.goku.authority.service;


import com.goku.authority.dto.UserInfoDTO;
import com.goku.authority.dto.UserLoginDTO;

import java.util.List;

public interface UserInfoService {
    List<UserInfoDTO> getUserInfos(Long userId);

    Integer registerUserInfo(UserInfoDTO userInfoDTO);

    String doLogin(UserLoginDTO userLoginDTO);

    String doLogout(String token);

    UserInfoDTO getUserByToken(String token);
}
