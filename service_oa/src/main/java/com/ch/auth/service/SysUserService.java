package com.ch.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.model.system.SysUser;

import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: SysUserService
 * @Description:
 * @Date: 2023/4/11 13:03
 * @Version: v1.0
 */
public interface SysUserService extends IService<SysUser> {
    boolean updateStatus(Long id, Integer status);

    SysUser getByUserName(String username);

    Map<String, Object> getCurrentUser();
}
