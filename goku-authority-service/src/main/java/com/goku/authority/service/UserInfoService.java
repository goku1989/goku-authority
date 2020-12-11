package com.goku.authority.service;

import com.goku.authority.service.dto.UserInfoDTO;
import com.goku.authority.service.dto.UserLoginDTO;

import java.util.List;

public interface UserInfoService {
    List<UserInfoDTO> getUserInfos(Long userId);

    Boolean registerUserInfo(UserInfoDTO userInfoDTO);

    String doLogin(UserLoginDTO userLoginDTO);
}
