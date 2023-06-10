package com.ch.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ch.auth.service.SysUserService;
import com.ch.model.process.Process;
import com.ch.model.process.ProcessTemplate;
import com.ch.model.system.SysUser;
import com.ch.process.service.OaProcessService;
import com.ch.process.service.OaProcessTemplateService;
import com.ch.security.custom.LoginUserInfoHelper;
import com.ch.wechat.service.MessageService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: MessageServiceImpl
 * @Description:
 * @Date: 2023/6/1 10:49
 * @Version: v1.0
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private OaProcessService oaProcessService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private WxMpService wxMpService;

    //推送审批消息
    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) {
        //根据id查数据
        Process process = oaProcessService.getById(processId);

        //根据userId查询要推送人的数据
        SysUser sysUser = sysUserService.getById(userId);

        //查询模板信息
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());

        //获取提交审批人的信息
        SysUser submitSysUser = sysUserService.getById(process.getUserId());

        //设置发送消息 给谁发送获取对应的openid就可以
        String openId = sysUser.getOpenId();
        if(StringUtils.isEmpty(openId)){
            openId = "oErZD50xT4mrIRt6j7erSH1QOJGw";
        }

        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)   //给谁发送就是谁的openId
                .templateId("ljqksrcJwQ4s_Phsmg6KdLF_MR3XLMezSYw3Hf3K1vc")  //创建模板信息的id值
                .url("http://oach9090.gz2vip.91tunnel.com/#/show/" + processId + "/" + taskId)   //点击消息，跳转的地址
                .build();

        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }



        //设置模板参数值
        templateMessage.addData(new WxMpTemplateData("first",
                submitSysUser.getName()+"提交了"+processTemplate.getName()+"审批申请，请注意查看。",
                "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));

        //发送消息
        try {
            String sendTemplateMsg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    //审批后推送提交审批人员
    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) {
        Process process = oaProcessService.getById(processId);
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());
        SysUser sysUser = sysUserService.getById(userId);
        SysUser currentSysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        String openid = sysUser.getOpenId();
        if(StringUtils.isEmpty(openid)) {
            openid = "omwf25izKON9dktgoy0dogqvnGhk";
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("5xA6vjpYDfUSTNyjdA6yNH01rXvD-yBx-qx-BS4Khe4")//模板id
                .url("http://oach9090.gz2vip.91tunnel.com/#/show/"+processId+"/0")  //点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", "你发起的"+processTemplate.getName()+"审批申请已经被处理了，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", currentSysUser.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = null;
        try {
            msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            System.out.println(msg);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
