package com.stan.server.security;

import com.stan.server.entity.Role;
import com.stan.server.mapper.UserMapper;
import com.stan.server.model.MyUserDetails;
import com.stan.server.model.vo.UserVO;
import com.stan.server.service.RoleService;
import org.minbox.framework.api.boot.plugin.security.delegate.ApiBootStoreDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 替换MyUserDetailService，用于验证用户名密码登录
 */
@Component
public class UserStoreDelegate implements ApiBootStoreDelegate {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleService sysRoleService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserVO userVO = userMapper.getUserInfoByName(userName);
        if (userVO == null || ObjectUtils.isEmpty(userVO)) {
            throw new UsernameNotFoundException("该用户不存在！");
        }
        //角色
        List<Role> roles = sysRoleService.getRolesFromUser(userVO.getId());
        Set<SimpleGrantedAuthority> collection = new HashSet<>();
        for (Role role : roles) {
            collection.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return new MyUserDetails(userVO.getId(), userVO.getUserName(), userVO.getUserPassword(), collection);
    }
}
