package com.ch.auth;

import com.ch.luobin.controller.Pusher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author hui cao
 * @ClassName: ServiceAuthApplication
 * @Description:
 * @Date: 2023/4/2 16:20
 * @Version: v1.0
 */

@Slf4j
@SpringBootApplication
@ComponentScan("com.ch")
public class ServiceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class,args);
        System.out.println("------------------------------- 启动成功 -------------------------------");
        try {
            Pusher.push();
            log.info("启动后第一次测试发送成功！");
        } catch (Exception e) {
            log.error("启动后第一次发送失败！！！");
            e.printStackTrace();
        }
    }
}
