package com.goku.authority.api;

import com.goku.authority.dto.UserInfoDTO;
import com.goku.foundation.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authority", url = "127.0.0.1:20030")
public interface SSOUtils {
    @GetMapping(value = "/v1/user/getUserByToken")
    BaseResponse<UserInfoDTO> getUserByToken(@RequestParam("token") String token);
}
