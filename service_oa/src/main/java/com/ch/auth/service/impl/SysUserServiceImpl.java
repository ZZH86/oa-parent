package com.ch.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.auth.mapper.SysUserMapper;
import com.ch.auth.service.SysUserService;
import com.ch.model.system.SysUser;
import com.ch.model.system.SysUserRole;
import com.ch.security.custom.LoginUserInfoHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: SysUserServiceImpl
 * @Description:
 * @Date: 2023/4/11 13:04
 * @Version: v1.0
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public boolean updateStatus(Long id, Integer status) {
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setStatus(status);
        return this.updateById(sysUser);
    }

    @Override
    public SysUser getByUserName(String username) {
        return baseMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public Map<String, Object> getCurrentUser() {
        SysUser sysUser = baseMapper.selectById(LoginUserInfoHelper.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put("name", sysUser.getName());
        map.put("phone", sysUser.getPhone());
        map.put("deptName", "普联技术有限公司");
        map.put("postName", "小小采购员");
        return map;
    }
}
