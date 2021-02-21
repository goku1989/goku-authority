package com.goku.authority.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goku.authority.dao.mapper.UserInfoMapper;
import com.goku.authority.dao.po.UserInfo;
import com.goku.authority.dto.UserInfoDTO;
import com.goku.authority.dto.UserLoginDTO;
import com.goku.authority.service.UserInfoService;
import com.goku.foundation.redis.RedisUtils;
import com.goku.foundation.utils.CommonUtil;
import com.goku.foundation.utils.CookieUtils;
import com.goku.foundation.utils.POUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

import static com.goku.authority.constants.Constants.*;
import static com.goku.foundation.constant.StandardConstant.USER_TOKEN;

@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public List<UserInfoDTO> getUserInfos(Long userId) {
        List<UserInfo> userInfos = userInfoMapper.selectByExample(Example.builder(UserInfo.class)
                .where(WeekendSqls.<UserInfo>custom()
                        .andEqualTo(UserInfo::getId, userId)
                        .andEqualTo(UserInfo::getDeleted, NOT_DELETE))
                .build());

        List<UserInfoDTO> userInfoDTOS = CommonUtil.convertList(userInfos, UserInfoDTO.class);
        userInfoDTOS.forEach(e -> {
            e.setUserName(null);
            e.setUserPassword(null);
            e.setUserPhone(null);
            e.setUserEmail(null);
        });
        return userInfoDTOS;
    }

    @Override
    public UserInfoDTO getUserByToken(HttpServletRequest req) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().contains(USER_TOKEN)) {
                String token = cookie.getValue();
                String strToken = (String)redisUtils.get("authority:user_token:" + token);
                if (StringUtils.isNotEmpty(strToken)) {
                    String strJson = (String)redisUtils.get("authority:user_info:" + token);
                    UserInfo userInfo = JSON.parseObject(strJson, UserInfo.class);
                    userInfoDTO.setNickName(userInfo.getNickName());
                }
                return userInfoDTO;
            }
        }
        return userInfoDTO;
    }

    @Override
    public Integer registerUserInfo(UserInfoDTO userInfoDTO,
                                    HttpServletRequest req,
                                    HttpServletResponse res) {
        List<UserInfo> userInfos = userInfoMapper.selectByExample(Example.builder(UserInfo.class)
                .where(WeekendSqls.<UserInfo>custom()
                        .andEqualTo(UserInfo::getNickName, userInfoDTO.getNickName())
                        .andEqualTo(UserInfo::getDeleted, NOT_DELETE))
                .build());
        if (CollectionUtils.isNotEmpty(userInfos)) {
            return 1;
        }
        UserInfo userInfo = CommonUtil.convert(userInfoDTO, UserInfo.class);
        userInfo.setGkcode(USER_INFO + CommonUtil.getIdByUUId());
        POUtils.initCreatPO(userInfo);
        userInfoMapper.insert(userInfo);

        String token = UUID.randomUUID().toString();

        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setNickName(userInfo.getNickName());

        String userJson = JSONObject.toJSONString(newUserInfo);

        redisUtils.set("authority:user_token:" + token, token, 60 * 30);
        redisUtils.set("authority:user_info:" + token, userJson, 60 * 30);
        CookieUtils.setCookie(req, res, USER_TOKEN, token);
        return 0;
    }

    @Override
    public String doLogin(UserLoginDTO userLoginDTO,
                          HttpServletRequest req,
                          HttpServletResponse res) {

        String token = "";
        Cookie[] cookies = req.getCookies();
        if (null != cookies && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().contains(USER_TOKEN)) {
                    token = cookie.getValue();
                }
            }
        }
        String strToken = (String)redisUtils.get("authority:user_token:" + token);

        if (StringUtils.isNotEmpty(strToken)) {
            log.info("redis中存在此token");
            return token;
        }

        List<UserInfo> userInfos = userInfoMapper.selectByExample(Example.builder(UserInfo.class)
                .where(WeekendSqls.<UserInfo>custom()
                        .andEqualTo(UserInfo::getNickName, userLoginDTO.getNickName())
                        .andEqualTo(UserInfo::getUserPassword, userLoginDTO.getUserPassword())
                        .andEqualTo(UserInfo::getDeleted, NOT_DELETE))
                .build());
        if (CollectionUtils.isNotEmpty(userInfos)) {
            token = UUID.randomUUID().toString();

            UserInfo newUser = new UserInfo();
            newUser.setNickName(userInfos.get(0).getNickName());
            String userJson = JSONObject.toJSONString(newUser);

            redisUtils.set("authority:user_token:" + token, token, 60 * 30);
            redisUtils.set("authority:user_info:" + token, userJson, 60 * 30);
            CookieUtils.setCookie(req, res, USER_TOKEN, token);
            return token;
        }


        return STR_EMPTY;
    }

    @Override
    public String doLogout(String token) {
        String userJsonString = (String) redisUtils.get(token);
        if (StringUtils.isNotEmpty(userJsonString)) {
            UserInfo userInfo = JSON.parseObject(userJsonString, UserInfo.class);
            redisUtils.del(String.valueOf(userInfo.getId()));
            redisUtils.del(token);
            return "logURL";
        } else {
            return null;
        }
    }

}
