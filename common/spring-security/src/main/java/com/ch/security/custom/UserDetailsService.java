package com.ch.security.custom;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Author hui cao
 * @ClassName: UserDetailsService
 * @Description:
 * @Date: 2023/4/23 14:49
 * @Version: v1.0
 */
public interface UserDetailsService {
    /**
     * 根据用户名获取用户对象（获取不到直接抛异常）
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
