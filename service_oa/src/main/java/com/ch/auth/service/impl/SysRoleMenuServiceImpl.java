package com.ch.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.auth.mapper.SysRoleMenuMapper;
import com.ch.auth.service.SysRoleMenuService;
import com.ch.model.system.SysRoleMenu;
import org.springframework.stereotype.Service;

/**
 * @Author hui cao
 * @ClassName: SysRoleMenuServiceImpl
 * @Description:
 * @Date: 2023/4/15 13:46
 * @Version: v1.0
 */

@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {
}
