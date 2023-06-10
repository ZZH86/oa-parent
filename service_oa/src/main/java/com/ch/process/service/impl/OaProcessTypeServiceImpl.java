package com.ch.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.model.process.ProcessTemplate;
import com.ch.model.process.ProcessType;
import com.ch.process.mapper.OaProcessTypeMapper;
import com.ch.process.service.OaProcessTemplateService;
import com.ch.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author hui cao
 * @ClassName: OaProcessTypeServiceImpl
 * @Description:
 * @Date: 2023/5/5 20:41
 * @Version: v1.0
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    //查询所有审批分类和每个分类所有的审批模板
    @Override
    public List<ProcessType> findProcessType() {
        //1 查询所有的审批分类，返回list集合
        List<ProcessType> processTypeList = baseMapper.selectList(null);

        //2 遍历返回所有的审批分类list集合
        for (ProcessType processType : processTypeList) {
            //3 得到每个审批分类，根据分类id查询对应审批模板
            Long id = processType.getId();
            List<ProcessTemplate> processTemplates = oaProcessTemplateService.list(new LambdaQueryWrapper<ProcessTemplate>().eq(ProcessTemplate::getProcessTypeId, id));
            //4 根据审批分类id查询对应的审批模板数据封装到每个审批分类对象中
            processType.setProcessTemplateList(processTemplates);
        }
        return processTypeList;
    }
}
