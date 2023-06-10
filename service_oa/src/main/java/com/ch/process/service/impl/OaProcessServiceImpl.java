package com.ch.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.auth.service.SysUserService;
import com.ch.model.process.Process;
import com.ch.model.process.ProcessRecord;
import com.ch.model.process.ProcessTemplate;
import com.ch.model.system.SysUser;
import com.ch.process.mapper.OaProcessMapper;
import com.ch.process.service.OaProcessRecordService;
import com.ch.process.service.OaProcessService;
import com.ch.process.service.OaProcessTemplateService;
import com.ch.security.custom.LoginUserInfoHelper;
import com.ch.vo.process.ApprovalVo;
import com.ch.vo.process.ProcessFormVo;
import com.ch.vo.process.ProcessQueryVo;
import com.ch.vo.process.ProcessVo;
import com.ch.wechat.service.MessageService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @Author hui cao
 * @ClassName: OaProcessImpl
 * @Description:
 * @Date: 2023/5/6 21:10
 * @Version: v1.0
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private OaProcessRecordService oaProcessRecordService;

    @Autowired
    private MessageService messageService;
    @Override
    public Page<ProcessVo> selectPage(Page<ProcessVo> page1, ProcessQueryVo processQueryVo) {
        Page<ProcessVo> processPage = baseMapper.selectPage(page1,processQueryVo);
        return processPage;
    }

    @Override
    public void deployByZip(String deployPath) {
        InputStream inputStream = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //部署
        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("流程定义key" + deploy.getKey());
    }

    //启动流程实例
    @Override
    public void startUp(ProcessFormVo processFormVo) {
        //1 根据当前的用户id获取用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());

        //2 根据审批模板id把模板信息进行查询
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(processFormVo.getProcessTemplateId());
        //3 保存提交的审批信息到业务表oa_process
        Process process = new Process();
        //复制processFormVo属性到process
        BeanUtils.copyProperties(processFormVo, process);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        process.setStatus(1);
        baseMapper.insert(process);

        //绑定业务id
        String businessKey = String.valueOf(process.getId());
        //流程参数
        Map<String, Object> variables = new HashMap<>();
        //将表单数据放入流程实例中
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formData = jsonObject.getJSONObject("formData");
        //遍历fromData,封装成map集合
        Map<String, Object> map = new HashMap<>();
        //循环转换
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        variables.put("data", map);

        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processTemplate.getProcessDefinitionKey(), businessKey, variables);
        //业务表关联当前流程实例id
        String processInstanceId = processInstance.getId();
        process.setProcessInstanceId(processInstanceId);

        //计算下一个审批人，可能有多个（并行审批）
        List<Task> taskList = this.getCurrentTaskList(processInstanceId);
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assigneeList = new ArrayList<>();
            for(Task task : taskList) {
                SysUser user = sysUserService.getByUserName(task.getAssignee());
                assigneeList.add(user.getName());

                //推送消息给下一个审批人，后续完善
                messageService.pushPendingMessage(process.getId(), user.getId(), task.getId());
            }
            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
            process.setStatus(1);
        }
        System.out.println("666流程实例id" + process.getProcessInstanceId());
        oaProcessRecordService.record(process.getId(),1,"发起申请");
        baseMapper.updateById(process);
    }

    /**
     * 获取当前任务列表
     * @param processInstanceId
     * @return
     */
    public List<Task> getCurrentTaskList(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return tasks;
    }

    //查询待处理的任务列表
    @Override
    public Page<ProcessVo> findPending(Page<Process> pageParam) {
//        //1 封装查询条件，根据当前当前登陆的用户名称
//        TaskQuery query = taskService.createTaskQuery()
//                .taskAssignee(LoginUserInfoHelper.getUsername())
//                .orderByTaskCreateTime()
//                .desc();
//
//        //2 调用方法分页条件查询，返回list集合，待办任务集合 参数：开始位置，每页显示记录数
//        int begin = (int)((pageParam.getCurrent() - 1)*pageParam.getSize());
//        int size = (int)pageParam.getSize();
//        List<Task> tasks = query.listPage(begin, size);
//        long count = query.count();
//
//        //3 封装返回list集合数据到list<ProcessVo>
//        List<ProcessVo> processVoList = new ArrayList<>();
//        for (Task task : tasks) {
//            String processInstanceId = task.getProcessInstanceId();
//            //根据流程实例获取实例对象
//            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//                    .processInstanceId(processInstanceId)
//                    .singleResult();
//            //从流程实例对象获取业务key
//            String businessKey = processInstance.getBusinessKey();
//            if(businessKey == null){
//                continue;
//            }
//            //根据业务key获取process对象
//            Process process = baseMapper.selectById(businessKey);
//            if(process == null){
//                continue;
//            }
//            //process对象复制到processVo
//            ProcessVo processVo = new ProcessVo();
//            BeanUtils.copyProperties(process,processVo);
//            processVo.setTaskId(task.getId());
//
//            processVoList.add(processVo);
//        }
//
//        //4 封装返回page对象
//        Page<ProcessVo> processVoPage = new Page<>(pageParam.getCurrent(),pageParam.getSize(),count);
//        processVoPage.setRecords(processVoList);
//        return processVoPage;

        // 根据当前人的ID查询
        TaskQuery query = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername()).orderByTaskCreateTime().desc();
        List<Task> list = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long totalCount = query.count();

        List<ProcessVo> processList = new ArrayList<>();
        // 根据流程的业务ID查询实体并关联
        for (Task item : list) {
            String processInstanceId = item.getProcessInstanceId();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (processInstance == null) {
                continue;
            }
            // 业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }
            Process process = this.getById(Long.parseLong(businessKey));
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(item.getId());
            processList.add(processVo);
        }
        Page<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processList);
        return page;
    }

    //查看审批详情信息
    @Override
    public Map<String, Object> show(Long id) {
        //1 根据流程id获得流程信息  oa_process
        Process process = baseMapper.selectById(id);
        //2 根据流程id得到流程的审批过程信息 oa_process_record
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId,id);
        List<ProcessRecord> list = oaProcessRecordService.list(wrapper);

        //3 根据模板id查询模板信息
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());

        //4 判断当前用户是否可以审批
        //可以看到信息但不一定能审批，不能重复审批
        boolean isApprove = false;
        List<Task> currentTaskList = this.getCurrentTaskList(process.getProcessInstanceId());
        for (Task task : currentTaskList) {
            //判断任务审批人是否是当前用户
            if(task.getAssignee().equals(LoginUserInfoHelper.getUsername())){
                isApprove = true;
            }
        }

        //5 查询数据封装到map集合中
        Map<String,Object> map  =new HashMap<>();

        map.put("process", process);
        map.put("processRecordList", list);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);
        return map;
    }

    //审批
    @Override
    public void approve(ApprovalVo approvalVo) {
        //1 从approveVo获取任务id,根据任务id获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String,Object> entry:variables.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        //2 判断审批状态值
        //2.1 状态值=1 审批通过
        if(approvalVo.getStatus() == 1){
            taskService.complete(taskId);
        }else {
            //2.2 状态值=-1 驳回，流程直接结束
            this.endTask(taskId);
        }
        //3 记录一下审批相关的过程信息
        String description = approvalVo.getStatus() == 1 ? "已通过":"驳回";
        oaProcessRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(),description);

        //4 查询下一个审批人，更新流程表记录
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        //查询代办任务
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if(!CollectionUtils.isEmpty(taskList)){
            List<String> assignList = new ArrayList<>();
            for (Task task:taskList){
                String assignee = task.getAssignee();
                SysUser sysUser = sysUserService.getByUserName(assignee);
                assignList.add(sysUser.getName());
                //TODO 公众号消息推送
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());
            }
            //更新process流程信息
            process.setDescription("等待" + StringUtils.join(assignList.toArray(), ",") + "审批");
            process.setStatus(1);
        }else {
            if(approvalVo.getStatus() == 1){
                process.setDescription("审批通过");
                process.setStatus(2);
            }else {
                process.setDescription("审批驳回");
                process.setStatus(-1);
            }
        }
        //推送消息给申请人
        messageService.pushProcessedMessage(process.getId(), process.getUserId(), approvalVo.getStatus());
        baseMapper.updateById(process);

    }

    //已处理
    @Override
    public Page<ProcessVo> findProcessed(Page<Process> processPage) {
        //封装查询条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished().orderByHistoricTaskInstanceStartTime().desc();

        //调用方法条件分页查询，返回list   listPage(开始位置 ，每页显示记录数)
        List<HistoricTaskInstance> list = query.listPage((int) ((processPage.getCurrent() - 1) * processPage.getSize()), (int) processPage.getSize());
        long totalCount = query.count();

        //封装成processVo
        List<ProcessVo> processVoList = new ArrayList<>();
        for (HistoricTaskInstance historicTaskInstance : list) {
            //获取流程实例id
            String processInstanceId = historicTaskInstance.getProcessInstanceId();
            //根据流程实例id查询获取process信息
            Process process = baseMapper.selectOne(new LambdaQueryWrapper<Process>().eq(Process::getProcessInstanceId, processInstanceId));
            //process -> processVo
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVoList.add(processVo);
        }

        //封装分页查询所有数据，返回
        Page<ProcessVo> processVoPage = new Page<>(processPage.getCurrent(),processPage.getSize(),totalCount);
        processVoPage.setRecords(processVoList);

        return processVoPage;
    }

    //已发起
    @Override
    public Page<ProcessVo> findStarted(Page<ProcessVo> processPage) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        Page<ProcessVo> processVoPage = baseMapper.selectPage(processPage, processQueryVo);
        return processVoPage;
    }

    //结束流程
    private void endTask(String taskId) {
        //  当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());
    }
}
