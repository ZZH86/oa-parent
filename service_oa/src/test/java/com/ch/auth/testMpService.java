package com.ch.auth;

import com.ch.auth.service.SysRoleService;
import com.ch.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author hui cao
 * @ClassName: testMpService
 * @Description:
 * @Date: 2023/4/2 17:28
 * @Version: v1.0
 */

@SpringBootTest
public class testMpService {

    @Autowired
    private SysRoleService sysRoleService;

    @Test
    public void getAll(){
        List<SysRole> list = sysRoleService.list();
        System.out.println(list);
    }
}
