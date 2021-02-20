package com.goku.authority.service;


import com.goku.authority.dto.UserInfoDTO;
import com.goku.authority.dto.UserLoginDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserInfoService {
    List<UserInfoDTO> getUserInfos(Long userId);

    Integer registerUserInfo(UserInfoDTO userInfoDTO,
                             HttpServletRequest req,
                             HttpServletResponse res);

    String doLogin(UserLoginDTO userLoginDTO,
                   HttpServletRequest req,
                   HttpServletResponse res);

    String doLogout(String token);

    UserInfoDTO getUserByToken(HttpServletRequest req);
}
