package com.ch.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.auth.mapper.SysMenuMapper;
import com.ch.auth.mapper.SysRoleMenuMapper;
import com.ch.auth.service.SysMenuService;
import com.ch.auth.service.SysRoleMenuService;
import com.ch.auth.utils.MenuHelper;
import com.ch.model.system.SysMenu;
import com.ch.model.system.SysRoleMenu;
import com.ch.vo.system.AssignMenuVo;
import com.ch.vo.system.MetaVo;
import com.ch.vo.system.RouterVo;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.objects.Global.print;

/**
 * @Author hui cao
 * @ClassName: SysMenuServiceImpl
 * @Description:
 * @Date: 2023/4/15 13:45
 * @Version: v1.0
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    //获得菜单
    @Override
    public List<SysMenu> findNodes() {
        //1 查询所有菜单的数据
        //List<SysMenu> list = this.list();
        List<SysMenu> list = baseMapper.selectList(null);
        //2 构建树形结构
        return MenuHelper.buildTree(list);
    }

    //删除菜单
    @Override
    public boolean removeMenuById(Long id) {
        //判断当前菜单是否有下一层菜单
        LambdaQueryWrapper<SysMenu> sysMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysMenuLambdaQueryWrapper.eq(SysMenu::getParentId,id);
        Integer i = baseMapper.selectCount(sysMenuLambdaQueryWrapper);
        if(i == 0){
            return this.removeById(id);
        }else {
            return false;
        }
    }

    //查询所有菜单和角色分配的菜单
    @Override
    public List<SysMenu> findSysMenuByRoleId(Long sysRoleId) {
        //1 查询所有的菜单 status=1
        List<SysMenu> allMenuList =
                baseMapper.selectList(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));

        //2 获得角色id列表
        List<SysRoleMenu> sysRoleMenus =
                sysRoleMenuService.list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, sysRoleId));
        ArrayList<Long> roleMenuIds = new ArrayList<>();
        for (SysRoleMenu sm: sysRoleMenus) {
            roleMenuIds.add(sm.getMenuId());
        }

        //3 根据查到的角色id,如果是被选择的就把isSelect设为true
        for (SysMenu sysMenu :allMenuList){
            sysMenu.setSelect(roleMenuIds.contains(sysMenu.getId()));
        }

        //4 构建树返回
        return MenuHelper.buildTree(allMenuList);
        //全部权限列表
//        List<SysMenu> allSysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
//
//        //根据角色id获取角色权限
//        List<SysRoleMenu> sysRoleMenuList =
//                sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, sysRoleId));
//        //转换给角色id与角色权限对应Map对象
//        List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
//
//        allSysMenuList.forEach(permission -> {
//            if (menuIdList.contains(permission.getId())) {
//                permission.setSelect(true);
//            } else {
//                permission.setSelect(false);
//            }
//        });
//
//        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
//        return sysMenuList;
    }

    //角色分配菜单
    //@Transactional
    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
//        Long roleId = assignMenuVo.getRoleId();
//        List<Long> menuIdList = assignMenuVo.getMenuIdList();
//        //将所有的关系删除
//        sysRoleMenuService.remove(
//                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId,roleId));
//
//        //添加新的关系
//        for (Long menuId: menuIdList) {
//            if (StringUtils.isEmpty(menuId)) continue;
//            SysRoleMenu sysRoleMenu = new SysRoleMenu();
//            sysRoleMenu.setMenuId(menuId);
//            sysRoleMenu.setRoleId(roleId);
//            sysRoleMenuService.save(sysRoleMenu);
//        }
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId()));

        for (Long menuId : assignMenuVo.getMenuIdList()) {
            if (StringUtils.isEmpty(menuId)) continue;
            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assignMenuVo.getRoleId());
            rolePermission.setMenuId(menuId);
            sysRoleMenuService.save(rolePermission);
        }
    }

    // 根据用户id获取到可以操作的菜单列表
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        //超级管理员admin账号id为：1
        //判断是否为管理员
        //是管理员，查询所有菜单列表
        List<SysMenu> sysMenuList = null;
        if(userId == 1){
            sysMenuList = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSortValue));
        }
        //不是管理员，根据userId查询可以操作的菜单列表
        //进行多表联合查询，把查询出来的数据列表构建成要求的路由结构findListByUserId
        else {
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        //先构建成树形结构
        List<SysMenu> tree = MenuHelper.buildTree(sysMenuList);

        //构建成要求的树形结构
        List<RouterVo> routerVoTree = this.buildRouter(tree);

        return routerVoTree;
    }

    /**
     * 根据菜单构建路由
     * @param tree
     * @return
     */
    private List<RouterVo> buildRouter(List<SysMenu> tree) {
        //创建list用于存储最终的数据
        List<RouterVo> routerVos = new ArrayList<>();
        for(SysMenu menu:tree){
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            //从下一层数据
            List<SysMenu> children = menu.getChildren();
            if(menu.getType() == 1){
                //加载隐藏路由
                for (SysMenu hiddenMenu:children ){
                    if(!StringUtils.isEmpty(hiddenMenu.getComponent())){
                        RouterVo hiddenRouter = new RouterVo();
                        hiddenRouter.setHidden(true);
                        hiddenRouter.setAlwaysShow(false);
                        hiddenRouter.setPath(getRouterPath(hiddenMenu));
                        hiddenRouter.setComponent(hiddenMenu.getComponent());
                        hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                        routerVos.add(hiddenRouter);
                    }
                }
            }else {
                if (!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    //递归
                    router.setChildren(buildRouter(children));
                }
            }
            routerVos.add(router);
        }
        //print(routerVos);
        return routerVos;
    }

    /**
     * 获取路由地址
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    // 根据用户id获取到可以操作的按钮列表
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        //如果是管理员，查询所有的按钮列表
        if(userId == 1){
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
        }
        //如果不是管理员，根据用户id查询可以操作按钮列表 多表联合查询
        else {
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        List<String> permsList = new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if(sysMenu.getType() == 2){
                permsList.add(sysMenu.getPerms());
            }
        }

        return permsList;
    }
}
