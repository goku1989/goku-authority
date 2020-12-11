package com.goku.authority.controller;

import com.goku.authority.service.UserInfoService;
import com.goku.authority.service.dto.UserInfoDTO;
import com.goku.authority.service.dto.UserLoginDTO;
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

    @PostMapping(value = "login")
    public BaseResponse<String> doLogin(@RequestBody UserLoginDTO userLoginDTO) {
        String token = userInfoService.doLogin(userLoginDTO);
        BaseResponse baseResponse = new BaseResponse(token);
        if (StringUtils.isEmpty(token)) {
            baseResponse.setCode("500");
            baseResponse.setMessage("没有此用户");
        }
        return baseResponse;
    }

    @GetMapping(value = "getUserByToken")
    public BaseResponse<UserInfoDTO> getUserByToken(@RequestParam String token) {
        return new BaseResponse<>(userInfoService.getUserByToken(token));
    }
}
