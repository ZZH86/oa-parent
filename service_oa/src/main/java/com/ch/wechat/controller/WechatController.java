package com.ch.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ch.auth.service.SysUserService;
import com.ch.common.jwt.JwtHelper;
import com.ch.common.result.Result;
import com.ch.model.system.SysUser;
import com.ch.vo.wechat.BindPhoneVo;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author hui cao
 * @ClassName: WechatController
 * @Description:
 * @Date: 2023/5/28 14:11
 * @Version: v1.0
 */
@Controller
@RequestMapping("/admin/wechat")
@CrossOrigin
public class WechatController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;

    //授权跳转
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl, HttpServletRequest request) throws UnsupportedEncodingException {
        //三个参数：1授权路径，在哪个路径获取微信信息 2 固定值，授权类型 3 授权成功之后，跳转路径 ’choa‘转成’#‘
        String redirectURL = wxMpService.getOAuth2Service()
                .buildAuthorizationUrl(userInfoUrl,
                        WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl.replace("guiguoa", "#")));
        System.out.println("【微信网页授权】获取code,redirectURL={}" + redirectURL);
        return "redirect:" + redirectURL;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws WxErrorException {
        //获取accessToken
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        //使用accessToken获取openId
        String openId = accessToken.getOpenId();
        System.out.println("openId" + openId);

        //获取微信用户信息
        WxOAuth2UserInfo wxMpUser = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        System.out.println("微信用户信息：" + JSON.toJSON(wxMpUser));

        //根据openId查询用户表
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId, openId);
        SysUser sysUser = sysUserService.getOne(wrapper);
        String token = "";
        if (sysUser != null){
            token = JwtHelper.createToken(sysUser.getId(),sysUser.getUsername());
        }
        if(!returnUrl.contains("?")) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }

    //微信账号绑定手机号
    @PostMapping("/bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo){
        //根据手机号查询数据库
        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, bindPhoneVo.getPhone()));
        //如果存在更新记录openid
        if(null != sysUser) {
            sysUser.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(sysUser);

            String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
            return Result.ok(token);
        } else {
            return Result.fail("手机号码不存在，绑定失败");
        }
    }
}
