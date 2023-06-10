package com.ch.auth.service.impl;

import com.ch.auth.service.SysMenuService;
import com.ch.auth.service.SysUserService;
import com.ch.model.system.SysUser;
import com.ch.security.custom.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author hui cao
 * @ClassName: UserDetailsServiceImpl
 * @Description:
 * @Date: 2023/4/23 14:50
 * @Version: v1.0
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUserName(username);
        if(sysUser == null){
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(sysUser.getStatus() == 0){
            throw new RuntimeException("账号已经停用！");
        }

        List<String> userPermsByUserId = sysMenuService.findUserPermsByUserId(sysUser.getId());

        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        for (String s : userPermsByUserId) {
            authList.add(new SimpleGrantedAuthority(s.trim()));
        }
        return new CustomUser(sysUser, authList);
    }
}
