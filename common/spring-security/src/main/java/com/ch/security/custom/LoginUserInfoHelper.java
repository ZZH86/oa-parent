package com.ch.security.custom;

/**
 * @Author hui cao
 * @ClassName: LoginUserInfoHelper
 * @Description: 获取当前用户信息帮助类
 * @Date: 2023/5/13 15:47
 * @Version: v1.0
 */
public class LoginUserInfoHelper {

    private  static ThreadLocal<Long> userId = new ThreadLocal<>();
    private static ThreadLocal<String> username = new ThreadLocal<>();

    public static void setUserId(Long _userId) {
        userId.set(_userId);
    }
    public static Long getUserId() {
        return userId.get();
    }
    public static void removeUserId() {
        userId.remove();
    }
    public static void setUsername(String _username) {
        username.set(_username);
    }
    public static String getUsername() {
        return username.get();
    }
    public static void removeUsername() {
        username.remove();
    }
}
