package com.ch.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.auth.mapper.SysRoleMapper;
import com.ch.auth.mapper.SysUserRoleMapper;
import com.ch.auth.service.SysRoleService;
import com.ch.model.system.SysRole;
import com.ch.model.system.SysUserRole;
import com.ch.vo.system.AssignRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author hui cao
 * @ClassName: SysRoleServiceImpl
 * @Description:
 * @Date: 2023/4/2 17:24
 * @Version: v1.0
 */

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    //获取所有角色列表
    @Override
    public Map<String, Object> findRoleByUserId(Long userId) {
        List<SysRole> roleList = this.list();

        LambdaQueryWrapper<SysUserRole> eq =
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId);
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(eq);

        List<Long> existRoleList = sysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        List<SysRole> sysRoles = new ArrayList<>();
        //根据角色id得到拥有的角色列表
        for (SysRole role : roleList) {
            if(existRoleList.contains(role.getId())){
                sysRoles.add(role);
            }
        }

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoleList",sysRoles);
        roleMap.put("allRoleList",roleList);
        return roleMap;
    }

    //根据用户分配角色
    @Override
    public boolean assignRoleByUSerId(AssignRoleVo assginRoleVo) {
        Long userId = assginRoleVo.getUserId();
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId,userId));
        for (Long roleId : assginRoleVo.getRoleIdList()) {
            if(StringUtils.isEmpty(roleId)){
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(roleId);
            sysUserRole.setUserId(userId);
            sysUserRoleMapper.insert(sysUserRole);
        }
        return true;
    }
}
