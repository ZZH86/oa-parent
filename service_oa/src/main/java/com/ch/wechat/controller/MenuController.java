package com.ch.wechat.controller;

import com.ch.common.result.Result;
import com.ch.model.wechat.Menu;
import com.ch.vo.wechat.MenuVo;
import com.ch.wechat.service.MenuService;
import com.ch.wechat.service.impl.MenuServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author hui cao
 * @ClassName: MenuController
 * @Description:
 * @Date: 2023/5/27 10:35
 * @Version: v1.0
 */
@Api(tags = "wechat管理")
@RestController
@RequestMapping(value = "/admin/wechat/menu")
@CrossOrigin   //跨域
public class MenuController {

    @Autowired
    private MenuService menuService;

    //@PreAuthorize("hasAuthority('bnt.menu.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return Result.ok(menu);
    }

    //@PreAuthorize("hasAuthority('bnt.menu.add')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody Menu menu) {
        menuService.save(menu);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.menu.update')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody Menu menu) {
        menuService.updateById(menu);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.menu.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        menuService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("获取全部菜单")
    @GetMapping("findMenuInfo")
    public Result findMenuInfo(){
        List<MenuVo> menuVoList = menuService.findMenuInfo();
        return Result.ok(menuVoList);
    }

    //同步菜单
    @GetMapping("syncMenu")
    public Result createMenu(){
        menuService.syncMenu();
        return Result.ok();
    }

    //删除同步菜单
    @DeleteMapping("removeMenu")
    public Result removeMenu(){
        menuService.removeMenu();
        return Result.ok();
    }
}
