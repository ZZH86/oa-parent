package com.ch.process.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ch.auth.service.SysUserService;
import com.ch.common.result.Result;
import com.ch.model.process.Process;
import com.ch.model.process.ProcessTemplate;
import com.ch.model.process.ProcessType;
import com.ch.process.service.OaProcessService;
import com.ch.process.service.OaProcessTemplateService;
import com.ch.process.service.OaProcessTypeService;
import com.ch.vo.process.ApprovalVo;
import com.ch.vo.process.ProcessFormVo;
import com.ch.vo.process.ProcessQueryVo;
import com.ch.vo.process.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: OaProcess
 * @Description:
 * @Date: 2023/5/6 21:12
 * @Version: v1.0
 */
@Api(tags = "审批流管理")
@RestController
@RequestMapping(value = "/admin/process")
@CrossOrigin   //跨域
public class OaProcessController {

    @Autowired
    private OaProcessService oaProcessService;

    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private SysUserService sysUserService;

    //列表
    @ApiOperation("获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable long page,
                       @PathVariable long limit,
                       ProcessQueryVo processQueryVo){
        Page<ProcessVo> page1 = new Page<>(page, limit);
        Page<ProcessVo> pageModel = oaProcessService.selectPage(page1,processQueryVo);
        return Result.ok(pageModel);
    }

    //查询所有审批分类和每个分类所有的审批模板
    @ApiOperation(value = "获取全部审批分类及模板")
    @GetMapping("findProcessType")
    public Result findProcessType(){
        List<ProcessType> list = oaProcessTypeService.findProcessType();
        return Result.ok(list);
    }

    //获得审批模板的信息
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable long processTemplateId){
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }

    //启动流程实例
    @ApiOperation(value = "启动流程")
    @PostMapping("/startUp")
    public Result startUp(@RequestBody ProcessFormVo processFormVo){
        oaProcessService.startUp(processFormVo);
        return Result.ok();
    }

    @ApiOperation(value = "待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(@PathVariable Long page,@PathVariable Long limit){
        Page<Process> pageParam = new Page<>(page,limit);
        Page<ProcessVo> pageModel = oaProcessService.findPending(pageParam);
        return Result.ok(pageModel);
    }

    //查看审批详情信息
    @GetMapping("show/{id}")
    public Result show(@PathVariable Long id){
        Map<String,Object> map = oaProcessService.show(id);
        return Result.ok(map);
    }

    //审批
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo){
        oaProcessService.approve(approvalVo);
        return Result.ok();
    }

    //已处理
    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(@PathVariable Long page,@PathVariable Long limit){
        Page<Process> processPage  =new Page<>(page,limit);
        Page<ProcessVo> processVoPage = oaProcessService.findProcessed(processPage);
        return Result.ok(processVoPage);
    }

    //已发起
    @ApiOperation(value = "已发起")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(@PathVariable Long page,@PathVariable Long limit){
        Page<ProcessVo> processPage  =new Page<>(page,limit);
        Page<ProcessVo> processVoPage = oaProcessService.findStarted(processPage);
        return Result.ok(processVoPage);
    }

    //获得用户信息
    @GetMapping("getCurrentUser")
    public Result getCurrentUser(){
        Map<String,Object> map = sysUserService.getCurrentUser();
        return Result.ok(map);
    }
}
