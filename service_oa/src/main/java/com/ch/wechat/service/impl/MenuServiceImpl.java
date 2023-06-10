package com.ch.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.model.wechat.Menu;
import com.ch.vo.wechat.MenuVo;
import com.ch.wechat.mapper.MenuMapper;
import com.ch.wechat.service.MenuService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author hui cao
 * @ClassName: MenuServiceImpl
 * @Description:
 * @Date: 2023/5/27 10:36
 * @Version: v1.0
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private WxMpService wxMpService;

    @Override
    public List<MenuVo> findMenuInfo() {
        ArrayList<MenuVo> list = new ArrayList<>();
        //1 查询所有菜单的集合
        List<Menu> menuList = menuMapper.selectList(null);

        //2 查询所有一级菜单 id=0
        List<Menu> oneMenuList = new ArrayList<>();
        for (Menu menu : menuList) {
            if (menu.getParentId() == 0){
                oneMenuList.add(menu);
            }
        }

        //3 遍历一级菜单的二级菜单
        for (Menu onemenu : oneMenuList) {
            //一级菜单menu -》 menuVo
            MenuVo menuVo = new MenuVo();
            BeanUtils.copyProperties(onemenu,menuVo);
            List<Menu> twoMenuList = menuList.stream().filter(menu -> menu.getParentId().intValue()==onemenu.getId().intValue())
                    .collect(Collectors.toList());
            ArrayList<MenuVo> twoMenuVoList = new ArrayList<>();
            for (Menu menu : twoMenuList) {
                MenuVo menuVo1 = new MenuVo();
                BeanUtils.copyProperties(menu,menuVo1);
                twoMenuVoList.add(menuVo1);
            }
            menuVo.setChildren(twoMenuVoList);
            list.add(menuVo);
        }
        return list;
    }

    //同步菜单
    @Override
    public void syncMenu() {
        //1 菜单数据查询出来，封装成微信要求的格式
        List<MenuVo> menuVoList = this.findMenuInfo();
        //2 调用工具里面的方法实现
        JSONArray buttonList = new JSONArray();
        for (MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name",oneMenuVo.getName());
            if(CollectionUtils.isEmpty(oneMenuVo.getChildren())){
                one.put("type",oneMenuVo.getType());
                one.put("url","http://oach9090.gz2vip.91tunnel.com/#" + oneMenuVo.getUrl());
            }else {
                JSONArray subButton = new JSONArray();
                for (MenuVo twoMenuVo:oneMenuVo.getChildren()){
                    JSONObject view = new JSONObject();
                    view.put("type",twoMenuVo.getType());
                    if(twoMenuVo.getType().equals("view")){
                        view.put("name",twoMenuVo.getName());
                        view.put("url","http://oach9090.gz2vip.91tunnel.com#" + twoMenuVo.getUrl());
                    }else {
                        view.put("name",twoMenuVo.getName());
                        view.put("key",twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button",subButton);
            }
            buttonList.add(one);
        }
        //菜单
        JSONObject button = new JSONObject();
        button.put("button",buttonList);
        //调用工具实现菜单推送
        try {
            wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }

    //删除菜单
    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

}
