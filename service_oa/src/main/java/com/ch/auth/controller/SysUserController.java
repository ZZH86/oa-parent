package com.ch.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ch.auth.service.SysUserService;
import com.ch.common.result.Result;
import com.ch.common.utils.MD5;
import com.ch.model.system.SysUser;
import com.ch.vo.system.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Author hui cao
 * @ClassName: SysUserController
 * @Description:
 * @Date: 2023/4/11 13:13
 * @Version: v1.0
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("admin/system/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService service;

    //更改用户状态
    @PreAuthorize("hasAuthority('bnt.sysUser.update')")
    @ApiOperation("更改用户状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,@PathVariable Integer status){
        boolean success = service.updateStatus(id,status);
        return success?Result.ok():Result.fail();
    }

    //用户条件分页查询
    @PreAuthorize("hasAuthority('bnt.sysUser.list')")
    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysUserQueryVo sysUserQueryVo){
        Page<SysUser> sysUserPage = new Page<>(page,limit);
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        String keyword = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        if(!StringUtils.isEmpty(keyword)){
            sysUserLambdaQueryWrapper.like(SysUser::getUsername,keyword);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            sysUserLambdaQueryWrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            sysUserLambdaQueryWrapper.le(SysUser::getCreateTime,createTimeEnd);
        }
        IPage<SysUser> page1 = service.page(sysUserPage, sysUserLambdaQueryWrapper);
        return Result.ok(page1);
    }

    //添加
    @PreAuthorize("hasAuthority('bnt.sysUser.add')")
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser sysUser){
        //对密码进行加密 MD5
        sysUser.setPassword(MD5.encrypt(sysUser.getPassword()));
        boolean save = service.save(sysUser);
        if(save){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //删除
    @PreAuthorize("hasAuthority('bnt.sysUser.remove')")
    @ApiOperation("删除用户")
    @DeleteMapping("remove/{id}")
    public Result delete(@PathVariable Long id){
        boolean b = service.removeById(id);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //查找
    @PreAuthorize("hasAuthority('bnt.sysUser.list')")
    @ApiOperation("查找用户")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        SysUser byId = service.getById(id);
        return Result.ok(byId);
    }

    //修改
    @PreAuthorize("hasAuthority('bnt.sysUser.update')")
    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result update(@RequestBody SysUser sysUser){
        boolean b = service.updateById(sysUser);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }


}
