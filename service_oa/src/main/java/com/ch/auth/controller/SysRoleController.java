package com.ch.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ch.auth.service.SysRoleService;
import com.ch.common.config.exception.ChException;
import com.ch.common.result.Result;
import com.ch.model.system.SysRole;
import com.ch.vo.system.AssignRoleVo;
import com.ch.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: SysRoleController
 * @Description:
 * @Date: 2023/4/2 17:38
 * @Version: v1.0
 */

@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    //注入service
    @Autowired
    private SysRoleService sysRoleService;

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("查询所有的角色")
    @GetMapping("findAll")
    public Result findAll() {
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }

    //条件分页查询
    //page 当前页 limit 每页显示记录数
    //SysRoleQueryVo 条件对象
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable("page") Long page,
                                @PathVariable("limit") Long limit,
                                SysRoleQueryVo sysRoleQueryVo) {
        //调用service
        //创建分页对象,传递分页相关的参数
        Page<SysRole> sysRolePage = new Page<>(page, limit);
        //获得查询条件，对分页条件进行封装
        LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)) {
            //封装
            sysRoleLambdaQueryWrapper.like(SysRole::getRoleName, roleName);
        }
        //调用方法实现
        Page<SysRole> page1 = sysRoleService.page(sysRolePage, sysRoleLambdaQueryWrapper);
        return Result.ok(page1);
    }

    //添加角色
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role) {
        //调用service
        return sysRoleService.save(role) ? Result.ok() : Result.fail();
    }

    //查询角色-根据id进行查询
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("获取角色-根据id进行查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable("id") long id) {
        SysRole byId = sysRoleService.getById(id);
        return Result.ok(byId);
    }

    //修改角色-最终修改
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("修改角色-最终修改")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role) {
        //调用service
        return sysRoleService.updateById(role) ? Result.ok() : Result.fail();
    }

    //根据id删除
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable("id") long id) {
        return sysRoleService.removeById(id) ? Result.ok() : Result.fail();
    }

    //批量删除
    //前端用数组形式传递
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        return sysRoleService.removeByIds(idList) ? Result.ok() : Result.fail();
    }

    //获取所有角色列表
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("所有角色列表和用户角色列表")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String, Object> map = sysRoleService.findRoleByUserId(userId);
        return Result.ok(map);
    }

    //更改用户的角色列表
    @PreAuthorize("hasAuthority('bnt.sysUser.assignRole')")
    @ApiOperation("根据用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssignRoleVo assignRoleVo) {
        return sysRoleService.assignRoleByUSerId(assignRoleVo) ? Result.ok() : Result.fail();
    }
}
