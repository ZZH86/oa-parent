package com.ch.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.model.process.ProcessType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author hui cao
 * @ClassName: OaProcessTypeService
 * @Description:
 * @Date: 2023/5/5 20:24
 * @Version: v1.0
 */
public interface OaProcessTypeService extends IService<ProcessType> {
    List<ProcessType> findProcessType();
}
