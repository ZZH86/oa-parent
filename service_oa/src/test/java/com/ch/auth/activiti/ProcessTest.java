package com.ch.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author hui cao
 * @ClassName: ProcessTest
 * @Description:
 * @Date: 2023/5/4 10:29
 * @Version: v1.0
 */

@SpringBootTest
public class ProcessTest {

    //注入RepositoryService
    @Autowired
    private RepositoryService repositoryService;

    //
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    //全部流程实例挂起
    @Test
    public void suspendProcessInstanceAll(){
        //获取流程定义的对象
        ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId("请假").singleResult();
        //调用流程定义对象的方法判断当前状态：挂起 激活
        boolean suspended = qingjia.isSuspended();

        //如果挂起进行激活,激活就挂起
        if(suspended){
            repositoryService.activateProcessDefinitionById(qingjia.getId(),true,null);
            System.out.println(qingjia.getId() + "激活了");
        }else {
            repositoryService.suspendProcessDefinitionById(qingjia.getId(),true,null);
        }
    }

    //处理任务
    @Test
    public void completeTask(){
        //查询负责人需要处理的任务
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan")
                .singleResult();
        //完成任务
        taskService.complete(task.getId());
    }

    //查询已经处理的任务
    @Test
    public void findCompleteTask(){
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("lisi")
                .finished().list();
        for (HistoricTaskInstance historicTaskInstance : taskInstanceList) {
            System.out.println(historicTaskInstance.getProcessInstanceId());
            System.out.println(historicTaskInstance.getAssignee());
            System.out.println(historicTaskInstance.getName());
        }
    }

    //查询个人的待办任务
    @Test
    public void findTaskList(){
        List<Task> taskList = taskService.createTaskQuery()
                .taskAssignee("zhangsan").list();
        for (Task task : taskList) {
            System.out.println(task.getProcessInstanceId());
            System.out.println(task.getAssignee());
            System.out.println(task.getName());
        }
    }

    //启动流程实例
    @Test
    public void startProcess(){
        ProcessInstance qingjia = runtimeService.startProcessInstanceByKey("请假");
        System.out.println(qingjia.getProcessDefinitionId());
        System.out.println(qingjia.getId());
        System.out.println(qingjia.getActivityId());

    }

    //单个文件部署
    @Test
    public void deployProcess(){
        //流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();

        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
}
