package com.ch.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ch.auth.mapper.SysRoleMapper;
import com.ch.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Wrapper;
import java.util.List;

/**
 * @Author hui cao
 * @ClassName: testDemo1
 * @Description:
 * @Date: 2023/4/2 16:25
 * @Version: v1.0
 */

@SpringBootTest
public class testdemo1 {

    //注入
    @Autowired
    private SysRoleMapper sysRoleMapper;

    //查询所有记录
    @Test
    public void getAll(){
        //UserMapper 中的 selectList() 方法的参数为 MP 内置的条件封装器 Wrapper
        //所以不填写就是无任何条件
        List<SysRole> sysRoles = sysRoleMapper.selectList(null);
        System.out.println(sysRoles);
    }

    //添加数据
    @Test
    public void addOne(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员");
        sysRole.setRoleCode("role");
        sysRole.setDescription("角色管理员");
        int result = sysRoleMapper.insert(sysRole);
        System.out.println(result); //影响的行数
        System.out.println(sysRole.getId()); //id自动回填
    }

    //删除
    @Test
    public void deleteOneByID(){
        int i = sysRoleMapper.deleteById(10);
        System.out.println(i);
    }

    //修改操作
    @Test
    public void updateByID(){
        //根据id查询
        SysRole sysRole = sysRoleMapper.selectById(8);
        //设置修改值
        sysRole.setDescription("66666");
        //调用方法实现更改
        int i = sysRoleMapper.updateById(sysRole);
        System.out.println(i);
    }

    //删除操作
    @Test
    public void deleteById(){
        int i = sysRoleMapper.deleteById(9);
        System.out.println(i);
    }

    //条件查询
    @Test
    public void query1(){
        QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
        sysRoleQueryWrapper.eq(true, "role_name", "系统管理员");
        //调用mp实现查询操作
        List<SysRole> sysRoles = sysRoleMapper.selectList(sysRoleQueryWrapper);
        System.out.println(sysRoles);
    }

    //lambda查询
    @Test
    public void query2(){
        LambdaQueryWrapper<SysRole> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper.eq(SysRole::getRoleName,"系统管理员");
        List<SysRole> sysRoles = sysRoleMapper.selectList(objectLambdaQueryWrapper);
        System.out.println(sysRoles);
    }
}
