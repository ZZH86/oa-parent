package com.ch.process.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.model.process.ProcessTemplate;
import com.ch.process.mapper.OaProcessTemplateMapper;
import com.ch.process.service.OaProcessService;
import com.ch.process.service.OaProcessTemplateService;
import com.ch.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author hui cao
 * @ClassName: OaProcessTemplateServiceImpl
 * @Description:
 * @Date: 2023/5/5 20:39
 * @Version: v1.0
 */

@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    private OaProcessService oaProcessService;
    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    @Override
    public Page<ProcessTemplate> selectProcessTemplate(Page<ProcessTemplate> processTemplatePage) {
        //1 分页查询
        Page<ProcessTemplate> processTemplatePage1 = baseMapper.selectPage(processTemplatePage,null);

        //2 从分页数据中获得类型列表list集合
        List<ProcessTemplate> templatePage1Records = processTemplatePage1.getRecords();

        //3 遍历列表类型，得到每个审批对象的id
        for (ProcessTemplate page1Record : templatePage1Records) {
            Long processTypeId = page1Record.getProcessTypeId();
            //4 根据审批对象的id获取它对应的名称
            String name = oaProcessTypeService.getById(processTypeId).getName();
            //5 完成最终封装processTypeName
            page1Record.setProcessTypeName(name);
        }

        return processTemplatePage1;
    }

    //部署流程定义
    @Transactional
    @Override
    public void publish(long id) {
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);

        //TODO 后续完善，流程定义部署
        if(!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())){
            oaProcessService.deployByZip(processTemplate.getProcessDefinitionPath());
            System.out.println("部署成功！！！！！！！！！！！！！！！！！");
        }
    }
}
