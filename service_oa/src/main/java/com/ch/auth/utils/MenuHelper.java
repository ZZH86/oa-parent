package com.ch.auth.utils;

import com.ch.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author hui cao
 * @ClassName: MenuHelper
 * @Description:
 * @Date: 2023/4/15 14:32
 * @Version: v1.0
 */
public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> list) {
        ArrayList<SysMenu> sysMenusTree = new ArrayList<>();
        for (SysMenu sysMenu1 : list) {
            if(sysMenu1.getParentId() == 0){
                //list.remove(sysMenu1);
                sysMenusTree.add(getChildren(sysMenu1,list));
            }
        }
        return sysMenusTree;
    }

    //递归添加所有子节点
    public static SysMenu getChildren(SysMenu sysMenu,List<SysMenu> list){
        sysMenu.setChildren(new ArrayList<SysMenu>());
        for (SysMenu menuChildren : list) {
            if(menuChildren.getParentId().longValue() == sysMenu.getId().longValue()){
                if(sysMenu.getChildren() == null){
                    sysMenu.setChildren(new ArrayList<SysMenu>());
                }
                sysMenu.getChildren().add(getChildren(menuChildren,list));
            }
        }
        return sysMenu;
    }

//    /**
//     * 使用递归方法建菜单
//     * @param sysMenuList
//     * @return
//     */
//    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
//        List<SysMenu> trees = new ArrayList<>();
//        for (SysMenu sysMenu : sysMenuList) {
//            if (sysMenu.getParentId() == 0) {
//                trees.add(findChildren(sysMenu,sysMenuList));
//            }
//        }
//        return trees;
//    }
//
//    /**
//     * 递归查找子节点
//     * @param treeNodes
//     * @return
//     */
//    public static SysMenu findChildren(SysMenu sysMenu, List<SysMenu> treeNodes) {
//        sysMenu.setChildren(new ArrayList<SysMenu>());
//
//        for (SysMenu it : treeNodes) {
//            if(sysMenu.getId().longValue() == it.getParentId().longValue()) {
//                if (sysMenu.getChildren() == null) {
//                    sysMenu.setChildren(new ArrayList<>());
//                }
//                sysMenu.getChildren().add(findChildren(it,treeNodes));
//            }
//        }
//        return sysMenu;
//    }
}
