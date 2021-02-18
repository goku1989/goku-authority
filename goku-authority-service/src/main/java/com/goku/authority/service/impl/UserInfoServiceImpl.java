package com.goku.authority.service.impl;

import com.alibaba.fastjson.JSON;
import com.goku.authority.dao.mapper.UserInfoMapper;
import com.goku.authority.dao.po.UserInfo;
import com.goku.authority.dto.UserInfoDTO;
import com.goku.authority.dto.UserLoginDTO;
import com.goku.authority.service.UserInfoService;
import com.goku.foundation.redis.RedisUtils;
import com.goku.foundation.utils.CommonUtil;
import com.goku.foundation.utils.POUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

import static com.goku.authority.constants.Constants.NOT_DELETE;
import static com.goku.authority.constants.Constants.STR_EMPTY;

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
        userInfoDTOS.forEach(e -> e.setUserPassword(null));
        return userInfoDTOS;
    }

    @Override
    public Integer registerUserInfo(UserInfoDTO userInfoDTO) {
        List<UserInfo> userInfos = userInfoMapper.selectByExample(Example.builder(UserInfo.class)
                .where(WeekendSqls.<UserInfo>custom()
                        .andEqualTo(UserInfo::getNickName, userInfoDTO.getNickName())
                        .andEqualTo(UserInfo::getDeleted, NOT_DELETE))
                .build());
        if (CollectionUtils.isNotEmpty(userInfos)) {
            return 1;
        }
        UserInfo userInfo = CommonUtil.convert(userInfoDTO, UserInfo.class);
        POUtils.initCreatPO(userInfo);
        userInfoMapper.insert(userInfo);
        return 0;
    }

    @Override
    public String doLogin(UserLoginDTO userLoginDTO) {
        List<UserInfo> userInfos = userInfoMapper.selectByExample(Example.builder(UserInfo.class)
                .where(WeekendSqls.<UserInfo>custom()
                        .andEqualTo(UserInfo::getNickName, userLoginDTO.getNickName())
                        .andEqualTo(UserInfo::getUserPassword, userLoginDTO.getUserPassword())
                        .andEqualTo(UserInfo::getDeleted, NOT_DELETE))
                .build());
        if (CollectionUtils.isNotEmpty(userInfos)) {
//            UserInfo userInfo = userInfos.get(0);
            //转string
//            String userId = String.valueOf(userInfo.getId());
//            String tokenFromRedis = (String) redisUtils.get(userId);
//            if (StringUtils.isNotEmpty(tokenFromRedis)) {
//                return tokenFromRedis;
//            }

            String token = UUID.randomUUID().toString();
//            String userInfoString = JSON.toJSONString(userInfo);
//            redisUtils.set(userId, token, 1800);
//            redisUtils.set(token, userInfoString, 1800);
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

    @Override
    public UserInfoDTO getUserByToken(String token) {
        String userJsonString = (String) redisUtils.get(token);
        if (StringUtils.isNotEmpty(userJsonString)) {
            UserInfoDTO userInfoDTO = JSON.parseObject(userJsonString, UserInfoDTO.class);
            userInfoDTO.setUserPassword(null);
            return userInfoDTO;
        }
        return new UserInfoDTO();
    }
}
