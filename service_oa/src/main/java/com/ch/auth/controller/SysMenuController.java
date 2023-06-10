package com.ch.auth.controller;

import com.ch.auth.service.SysMenuService;
import com.ch.auth.service.SysRoleMenuService;
import com.ch.common.result.Result;
import com.ch.model.system.SysMenu;
import com.ch.vo.system.AssignMenuVo;
import com.ch.vo.system.AssignRoleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: SysMenuController
 * @Description:
 * @Date: 2023/4/15 13:50
 * @Version: v1.0
 */

@Api(tags = "菜单管理接口")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    //增加
    @PreAuthorize("hasAuthority('bnt.sysMenu.add')")
    @ApiOperation("添加")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu sysMenu){
        return Result.ok(sysMenuService.save(sysMenu));
    }

    //删除
    @PreAuthorize("hasAuthority('bnt.sysMenu.remove')")
    @ApiOperation("删除菜单")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable Long id){
        return sysMenuService.removeMenuById(id)?Result.ok():Result.fail();
    }

    //修改
    @PreAuthorize("hasAuthority('bnt.sysMenu.update')")
    @ApiOperation("修改菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu sysMenu){
        return sysMenuService.updateById(sysMenu)?Result.ok():Result.fail();
    }

    //查找
    @PreAuthorize("hasAuthority('bnt.sysMenu.list')")
    @ApiOperation("菜单列表")
    @GetMapping("findNodes")
    public Result findNodes(){
        List<SysMenu> list = sysMenuService.findNodes();
        return Result.ok(list);
    }

    //查询所有菜单和角色分配的菜单
    @PreAuthorize("hasAuthority('bnt.sysMenu.list')")
    @ApiOperation("查询所有菜单和角色分配的菜单")
    @GetMapping("toAssign/{sysRoleId}")
    public Result findAllRoles(@PathVariable Long sysRoleId){
        List<SysMenu> list = sysMenuService.findSysMenuByRoleId(sysRoleId);
        return Result.ok(list);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.assignAuth')")
    @ApiOperation("角色分配菜单")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssignMenuVo assignMenuVo){
        sysMenuService.doAssign(assignMenuVo);
        return Result.ok();
    }
}
