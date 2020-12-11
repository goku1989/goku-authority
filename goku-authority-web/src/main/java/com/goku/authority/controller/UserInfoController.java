package com.goku.authority.controller;

import com.goku.authority.service.UserInfoService;
import com.goku.authority.service.dto.UserInfoDTO;
import com.goku.foundation.annotation.ParamLog;
import com.goku.foundation.response.BaseResponse;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "用户管理", tags = {"用户管理"})
@RestController
@RequestMapping("/v1/user")
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;

    @ParamLog(type = "查看", value = "查看用户信息")
    @GetMapping(value = "userInfos")
    public BaseResponse<List<UserInfoDTO>> getUserInfos(@RequestParam(name = "userId", required = false) Long userId) {
        return new BaseResponse<>(userInfoService.getUserInfos(userId));
    }

    @PostMapping(value = "userInfo")
    public BaseResponse<Boolean> registerUserInfo(@RequestBody UserInfoDTO userInfoDTO) {
        return new BaseResponse<>(userInfoService.registerUserInfo(userInfoDTO));
    }
}
