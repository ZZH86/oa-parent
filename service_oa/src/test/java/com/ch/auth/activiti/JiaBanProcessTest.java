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
 * @ClassName: JiaBanProcessTest
 * @Description:
 * @Date: 2023/5/4 14:31
 * @Version: v1.0
 */

@SpringBootTest
public class JiaBanProcessTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;


    ///////////////////////////////
    // 监听器实现
    ///////////////////////////////
    @Test
    public void deployProcess02(){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban02.bpmn20.xml")
                .name("加班申请流程02")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());

    }

    @Test
    public void startProcessInstance02(){
        ProcessInstance jiaban = runtimeService.startProcessInstanceByKey("jiaban02");
        System.out.println(jiaban.getProcessDefinitionId());
        System.out.println(jiaban.getProcessInstanceId());

    }


    ///////////////////////////////
    // uel-method 实现
    ///////////////////////////////
    @Test
    public void deployProcess01(){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban1.bpmn20.xml")
                .name("加班申请流程01")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());

    }

    @Test
    public void startProcessInstance01(){
        ProcessInstance jiaban = runtimeService.startProcessInstanceByKey("jiaban");
        System.out.println(jiaban.getProcessDefinitionId());
        System.out.println(jiaban.getProcessInstanceId());

    }

    ///////////////////////////////
    // uel-value 实现
    ///////////////////////////////
    //部署流程定义
    @Test
    public void deployProcess(){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程")
                .deploy();

        System.out.println(deployment.getName());
    }

    //启动流程实例
    @Test
    public void startProcessInstance(){
        Map<String,Object> map = new HashMap<>();
        //设置任务人
        map.put("assignee1","lucy");
        map.put("assignee2","mary");
        ProcessInstance jiaban = runtimeService.startProcessInstanceByKey("jiaban", map);
        System.out.println(jiaban.getProcessDefinitionId());
        System.out.println(jiaban.getProcessInstanceId());
    }

    //查询个人的待办任务
    @Test
    public void findTaskList(){
        List<Task> taskList = taskService.createTaskQuery()
                .taskAssignee("jack").list();
        for (Task task : taskList) {
            System.out.println(task.getProcessInstanceId());
            System.out.println(task.getAssignee());
            System.out.println(task.getName());
        }
    }
}
