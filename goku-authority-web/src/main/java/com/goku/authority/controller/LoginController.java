package com.goku.authority.controller;

import com.goku.authority.dto.UserLoginDTO;
import com.goku.authority.service.UserInfoService;
import com.goku.foundation.response.BaseResponse;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(value = "登录操作", tags = {"登录操作"})
@RestController
@RequestMapping("/v1/login")
public class LoginController {
    @Resource
    private UserInfoService userInfoService;

    @PostMapping(value = "/login")
    public BaseResponse<String> doLogin(@RequestBody UserLoginDTO userLoginDTO) {
        String token = userInfoService.doLogin(userLoginDTO);
        BaseResponse baseResponse = new BaseResponse(token);
        if (StringUtils.isEmpty(token)) {
            baseResponse.setCode("500");
            baseResponse.setMessage("用户名或密码错误");
        }
        return baseResponse;
    }

    @PostMapping(value = "/logout")
    public BaseResponse<String> doLogout(@RequestBody String token) {
        String logoutURL = userInfoService.doLogout(token);
        BaseResponse baseResponse = new BaseResponse(logoutURL);
        if (StringUtils.isEmpty(logoutURL)) {
            baseResponse.setCode("500");
            baseResponse.setMessage("没有此用户");
        }
        return baseResponse;
    }
}
