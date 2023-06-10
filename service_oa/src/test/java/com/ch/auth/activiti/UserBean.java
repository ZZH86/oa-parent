package com.ch.auth.activiti;

import org.springframework.stereotype.Component;

/**
 * @Author hui cao
 * @ClassName: UserBean
 * @Description:
 * @Date: 2023/5/4 15:22
 * @Version: v1.0
 */

@Component
public class UserBean {

    public String getUsername(int id){
        if(id == 1){
            return "lilei";
        }else if(id == 2){
            return "lijie";
        }else {
            return "caohui";
        }
    }
}
