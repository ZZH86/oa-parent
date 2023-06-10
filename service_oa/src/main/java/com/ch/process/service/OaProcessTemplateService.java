package com.ch.process.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.model.process.ProcessTemplate;
import com.ch.model.process.ProcessType;
import org.springframework.stereotype.Service;

/**
 * @Author hui cao
 * @ClassName: OaProcessTemplateService
 * @Description:
 * @Date: 2023/5/5 20:25
 * @Version: v1.0
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {
    Page<ProcessTemplate> selectProcessTemplate(Page<ProcessTemplate> processTemplatePage);

    //部署流程定义
    void publish(long id);
}
