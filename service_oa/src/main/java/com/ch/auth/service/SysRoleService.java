package com.ch.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.model.system.SysRole;
import com.ch.vo.system.AssignRoleVo;

import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: SysRoleService
 * @Description:
 * @Date: 2023/4/2 17:18
 * @Version: v1.0
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 通过id查找当前用户的角色列表
     * @param userId 用户id
     * @return String为用户拥有的角色 Object为所有角色
     */
    Map<String, Object> findRoleByUserId(Long userId);

    boolean assignRoleByUSerId(AssignRoleVo assginRoleVo);
}
