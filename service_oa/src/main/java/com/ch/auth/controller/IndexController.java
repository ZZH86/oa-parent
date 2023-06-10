package com.ch.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ch.auth.service.SysMenuService;
import com.ch.auth.service.SysUserService;
import com.ch.common.config.exception.ChException;
import com.ch.common.jwt.JwtHelper;
import com.ch.common.result.Result;
import com.ch.common.utils.MD5;
import com.ch.model.system.SysUser;
import com.ch.vo.system.LoginVo;
import com.ch.vo.system.RouterVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author hui cao
 * @ClassName: IndexController
 * @Description:
 * @Date: 2023/4/4 21:17
 * @Version: v1.0
 */

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    //login
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        //{"code":200,"data":{"token":"admin-token"}}
//        Map<String,Object> map = new HashMap<>();
//        map.put("token","admin-token");
//        return Result.ok(map);
        //1 获取输入用户名和密码
        //2 根据用户名查询数据库
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = sysUserService.getOne(wrapper);

        //3 用户信息是否存在
        if(sysUser == null) {
            throw new ChException(201,"用户不存在");
        }

        //4 判断密码
        //数据库存密码（MD5）
        String password_db = sysUser.getPassword();
        //获取输入的密码
        String password_input = MD5.encrypt(loginVo.getPassword());
        if(!password_db.equals(password_input)) {
            throw new ChException(201,"密码错误");
        }

        //5 判断用户是否被禁用  1 可用 0 禁用
        if(sysUser.getStatus()==0) {
            throw new ChException(201,"用户已经被禁用");
        }

        //6 使用jwt根据用户id和用户名称生成token字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        //7 返回
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }

    //info
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        //1 从请求头获取用户信息（获取请求头token字符串）
        String token = request.getHeader("token");

        //2 从token字符串获取用户id 或者 用户名称
        Long userId = JwtHelper.getUserId(token);

        //3 根据用户id查询数据库，把用户信息获取出来
        SysUser sysUser = sysUserService.getById(userId);

        //4 根据用户id获取用户可以操作菜单列表
        //查询数据库动态构建路由结构，进行显示
        List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);

        //5 根据用户id获取用户可以操作按钮列表
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);

        //6 返回相应的数据
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        //返回用户可以操作菜单
        map.put("routers",routerList);
        //返回用户可以操作按钮
        map.put("buttons",permsList);
        return Result.ok(map);
    }

    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}


//@Api(tags = "后台登陆管理")
//@RestController
//@RequestMapping("/admin/system/index")
//public class IndexController {
//
//    @Autowired
//    private SysUserService sysUserService;
//
//    @Autowired
//    private SysMenuService sysMenuService;
//
//    //登录
//    @PostMapping("login")
//    public Result login(@RequestBody LoginVo loginVo){
//        //{"code":20000,"data":{"token":"admin-token"}}
////        HashMap<String, Object> map = new HashMap<>();
////        map.put("token","admin-token");
////        return Result.ok(map);
//        //1 获取输入用户名
//        String username = loginVo.getUsername();
//        //2 根据用户名查询数据库
//        SysUser sysUser = sysUserService.getOne(
//                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
//        //3 用户信息是否存在
//        if(sysUser == null){
//            throw new ChException(201,"用户不存在");
//        }
//        //4 判断密码
//        //数据库存储密码(MD5)
//        String passwordFromDatasource = sysUser.getPassword();
//        //输入的密码
//        String passwordFromInput = MD5.encrypt(loginVo.getPassword());
//        if(!passwordFromDatasource.equals(passwordFromInput)){
//            throw new ChException(201,"密码错误");
//        }
//        //5 判断用户是否被禁用 1可用
//        if(sysUser.getStatus() == 0){
//            throw new ChException(201,"用户已被禁用");
//        }
//        //6 使用jwt根据用户id和用户名称生成token字符串
//        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
//        //7 返回token
//        Map<String,Object> map = new HashMap<>();
//        map.put("token",token);
//        return Result.ok(map);
//    }
//
//    /**
//     * 获取用户信息
//     * {"code":20000,"data":{"roles":["admin"],"introduction":"I am a super administrator",
//     * "avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif","name":"Super Admin"}}
//     */
//    @GetMapping("info")
//    public Result info(@RequestHeader("token") String token) {
//        Map<String, Object> map = new HashMap<>();
////        map.put("roles","[admin]");
////        map.put("name","Super Admin");
////        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
//        //还有可以操作的菜单和按钮
//
//        //1 从请求头获取用户信息token
//        //2 从token字符串获取用户id或者用户名称
//        Long userId = JwtHelper.getUserId(token);
////        Long userId = 1L;
//
//        //3 根据用户id查询数据库，把用户信息获取出来
//        SysUser sysUser = sysUserService.getById(userId);
//
//        //4 根据用户id获取到可以操作的菜单列表 查询数据库动态构建路由结构进行显示
//        List<RouterVo> routerVos = sysMenuService.findUserMenuListByUserId(userId);
//
//        //5 根据用户id获取到可以操作的按钮列表
//        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);
//
//        //6 返回值
//        //当前权限控制使用不到，我们暂时忽略
//        map.put("roles", "[admin]");
//        map.put("name",sysUser.getName());
//        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
//        map.put("routers",routerVos);
//        map.put("buttons",permsList);
//
//        return Result.ok(map);
//    }
//    /**
//     * 退出
//     */
//    @PostMapping("logout")
//    public Result logout() {
//        return Result.ok();
//    }
//}
