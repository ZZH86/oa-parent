package com.ch.auth.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @Author hui cao
 * @ClassName: MyTaskListener
 * @Description:
 * @Date: 2023/5/4 15:45
 * @Version: v1.0
 */
public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if(delegateTask.getName().equals("经理审批")){
            //分配任务
            delegateTask.setAssignee("jack");
        }else if(delegateTask.getName().equals("人事审批")){
            delegateTask.setAssignee("tom");
        }
    }
}
