package com.ch.process.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.model.process.Process;
import com.ch.vo.process.ApprovalVo;
import com.ch.vo.process.ProcessFormVo;
import com.ch.vo.process.ProcessQueryVo;
import com.ch.vo.process.ProcessVo;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: OaProcessService
 * @Description:
 * @Date: 2023/5/6 21:03
 * @Version: v1.0
 */
public interface OaProcessService extends IService<Process> {
    Page<ProcessVo> selectPage(Page<ProcessVo> page1, ProcessQueryVo processQueryVo);

    void deployByZip(String processDefinitionPath);

    //启动流程实例
    void startUp(ProcessFormVo processFormVo);

    //当前任务列表
    List<Task> getCurrentTaskList(String id);

    Page<ProcessVo> findPending(Page<Process> pageParam);

    //查看审批详情信息
    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo);

    //已处理
    Page<ProcessVo> findProcessed(Page<Process> processPage);

    Page<ProcessVo> findStarted(Page<ProcessVo> processPage);
}
