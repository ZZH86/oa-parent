package com.ch.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: ProcessTestDemo3
 * @Description:
 * @Date: 2023/5/4 20:15
 * @Version: v1.0
 */

@SpringBootTest
public class ProcessTestDemo3 {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    //1 部署流程定义
    @Test
    public void deployProcess02(){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban04.bpmn20.xml")
                .name("加班申请流程04")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }
    //启动流程实例
    @Test
    public void startProcess(){
        ProcessInstance qingjia = runtimeService.startProcessInstanceByKey("jiaban04");
        System.out.println(qingjia.getProcessDefinitionId());
        System.out.println(qingjia.getId());
        System.out.println(qingjia.getActivityId());

    }

    //2 查询组任务
    @Test
    public void findGroupTaskList(){
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("tom01")   //根据候选人查询
                .list();
        for (Task task : list) {
            System.out.println(task.getProcessInstanceId());
            System.out.println(task.getAssignee());
            System.out.println(task.getName());
        }
    }

    //3 拾取组任务
    @Test
    public void claimTask(){
        Task task = taskService.createTaskQuery()
                .taskCandidateUser("tom01")
                .singleResult();

        if(task != null){
            taskService.claim(task.getId(), "tom01");
            System.out.println("任务拾取完成");
        }
    }

    //4 查询个人的待办任务
    @Test
    public void findTaskList(){
        List<Task> taskList = taskService.createTaskQuery()
                .taskAssignee("tom01").list();
        for (Task task : taskList) {
            System.out.println(task.getProcessInstanceId());
            System.out.println(task.getAssignee());
            System.out.println(task.getName());
        }
    }

    //5 办理个人任务
    @Test
    public void completeTask(){
        Task task = taskService.createTaskQuery()
                .taskAssignee("tom01")   //要查询的负责人
                .singleResult();
//        Map<String,Object> variables = new HashMap<>();
//        variables.put("assignee2","zhao");
        //完成任务，参数：任务id
        taskService.complete(task.getId());   //,variables
    }

    //6 规范组任务
}
