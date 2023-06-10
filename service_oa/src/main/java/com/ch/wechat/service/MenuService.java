package com.ch.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.model.wechat.Menu;
import com.ch.vo.wechat.MenuVo;

import java.util.List;

/**
 * @Author hui cao
 * @ClassName: MenuService
 * @Description:
 * @Date: 2023/5/27 10:35
 * @Version: v1.0
 */
public interface MenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();
}
