package com.ch.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.model.system.SysMenu;
import com.ch.vo.system.AssignMenuVo;
import com.ch.vo.system.RouterVo;

import java.util.List;
import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: SysMenuService
 * @Description:
 * @Date: 2023/4/15 13:43
 * @Version: v1.0
 */
public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> findNodes();

    boolean removeMenuById(Long id);

    List<SysMenu> findSysMenuByRoleId(Long sysRoleId);

    void doAssign(AssignMenuVo assignMenuVo);

    List<RouterVo> findUserMenuListByUserId(Long userId);

    //5 根据用户id获取到可以操作的按钮列表
    List<String> findUserPermsByUserId(Long userId);
}
