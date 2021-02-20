package com.goku.authority.controller;

import com.goku.authority.dto.UserInfoDTO;
import com.goku.authority.service.UserInfoService;
import com.goku.foundation.annotation.ParamLog;
import com.goku.foundation.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@CrossOrigin
@Api(value = "用户管理", tags = {"用户管理"})
@RestController
@RequestMapping("/v1/user")
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;

    @ParamLog(type = "查看", value = "查看用户信息")
    @ApiOperation(value = "查看用户信息")
    @GetMapping(value = "/userInfos")
    public BaseResponse<List<UserInfoDTO>> getUserInfos(@RequestParam(name = "userId") Long userId) {
        return new BaseResponse<>(userInfoService.getUserInfos(userId));
    }

    @ApiOperation(value = "注册用户信息")
    @PostMapping(value = "/userInfo")
    public BaseResponse<Integer> registerUserInfo(@RequestBody UserInfoDTO userInfoDTO) {
        BaseResponse baseResponse = new BaseResponse();
        Integer integer = userInfoService.registerUserInfo(userInfoDTO);
        baseResponse.setData(integer);
        if (integer == 0) {
            baseResponse.setMessage("注册成功");
        } else if (integer == 1) {
            baseResponse.setMessage("注册失败，用户名重复");
        }
        return baseResponse;
    }

    @ApiOperation(value = "通过token获取用户信息")
    @GetMapping(value = "/getUserByToken")
    public BaseResponse<UserInfoDTO> getUserByToken(@RequestParam String token) {
        return new BaseResponse<>(userInfoService.getUserByToken(token));
    }
}
