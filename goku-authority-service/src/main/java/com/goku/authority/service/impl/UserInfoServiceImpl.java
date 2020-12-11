package com.goku.authority.service.impl;

import com.goku.authority.dao.mapper.UserInfoMapper;
import com.goku.authority.dao.po.UserInfo;
import com.goku.authority.service.UserInfoService;
import com.goku.authority.service.dto.UserInfoDTO;
import com.goku.authority.service.dto.UserLoginDTO;
import com.goku.foundation.redis.RedisUtils;
import com.goku.foundation.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.goku.authority.constants.Constants.NOT_DELETE;

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
                        .andEqualTo(UserInfo::getIsDeleted, NOT_DELETE))
                .build());

        List<UserInfoDTO> userInfoDTOS = CommonUtil.convertList(userInfos, UserInfoDTO.class);
        userInfoDTOS.forEach(e -> e.setUserPassword(null));
        return userInfoDTOS;
    }

    @Override
    public Boolean registerUserInfo(UserInfoDTO userInfoDTO) {
        UserInfo userInfo = CommonUtil.convert(userInfoDTO, UserInfo.class);
        userInfo.setId(CommonUtil.getSimpleId());

        userInfo.setCreateDate(new Date());
        userInfo.setModifiedDate(new Date());
        userInfo.setCreateUser(userInfoDTO.getUserName());
        userInfo.setModifiedUser(userInfoDTO.getUserName());
        userInfo.setIsDeleted(NOT_DELETE);
        userInfoMapper.insert(userInfo);

        return true;
    }

    @Override
    public String doLogin(UserLoginDTO userLoginDTO) {
        List<UserInfo> userInfos = userInfoMapper.selectByExample(Example.builder(UserInfo.class)
                .where(WeekendSqls.<UserInfo>custom()
                        .andEqualTo(UserInfo::getNickName, userLoginDTO.getNickName())
                        .andEqualTo(UserInfo::getUserPassword, userLoginDTO.getUserPassword())
                        .andEqualTo(UserInfo::getIsDeleted, NOT_DELETE))
                .build());
        if (CollectionUtils.isNotEmpty(userInfos)) {
            UserInfo userInfo = userInfos.get(0);
            String token = UUID.randomUUID().toString();
            redisUtils.set(String.valueOf(userInfo.getId()), token, 10);
            return token;
        }

        return null;
    }
}
