package com.ch.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ch.common.result.Result;
import com.ch.model.process.ProcessTemplate;
import com.ch.process.service.OaProcessTemplateService;
import com.ch.process.service.OaProcessTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author hui cao
 * @ClassName: OaProcessTemplateController
 * @Description:
 * @Date: 2023/5/5 20:12
 * @Version: v1.0
 */

@Api(value = "审批模板", tags = "审批模板")
@RestController
@RequestMapping(value = "/admin/process/processTemplate")
public class OaProcessTemplateController {

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    //上传流程定义接口
    @ApiOperation(value = "上传流程定义")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException{
        String path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        String originalFilename = file.getOriginalFilename();
        //上传目录
        File tempFile = new File(path + "/processes/");
        if(!tempFile.exists()){
            tempFile.mkdir();
        }
        //创建空文件，实现文件写入
        File zipFile = new File(path + "/processes/" + originalFilename);
        //保存文件到本地
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            return Result.fail();
        }
        Map<String, Object> map = new HashMap<>();
        //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath", "processes/" + originalFilename);
        map.put("processDefinitionKey", originalFilename.substring(0, originalFilename.lastIndexOf(".")));

        return Result.ok(map);
    }

    //部署流程定义
    @ApiOperation(value = "发布")
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable long id){
        //修改模板的发布状态 1 代表发布
        oaProcessTemplateService.publish(id);
        return Result.ok();

    }

    //分页查询
    @ApiOperation(value = "分页")
    @GetMapping("{page}/{limit}")
    public Result page(@PathVariable long page,@PathVariable long limit){
        Page<ProcessTemplate> processTemplatePage = new Page<>(page,limit);

        //分页查询审批模板，被审批类型对应的名称查出来
        Page<ProcessTemplate> page1 = oaProcessTemplateService.selectProcessTemplate(processTemplatePage);
        return Result.ok(page1);
    }

    //增
    @ApiOperation(value = "增加")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate){
        return oaProcessTemplateService.save(processTemplate)?Result.ok():Result.fail();
    }

    //删
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable long id){
        return oaProcessTemplateService.removeById(id)?Result.ok():Result.fail();
    }

    //改
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result update(@RequestBody ProcessTemplate processTemplate){
        return oaProcessTemplateService.updateById(processTemplate)?Result.ok():Result.fail();
    }

    //查
    @GetMapping("get/{id}")
    public Result get(@PathVariable long id){
        return Result.ok(oaProcessTemplateService.getById(id));
    }
}
