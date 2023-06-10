package com.ch.process.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ch.common.result.Result;
import com.ch.model.process.ProcessType;
import com.ch.process.service.OaProcessTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author hui cao
 * @ClassName: OaProcessTypeController
 * @Description:
 * @Date: 2023/5/5 20:13
 * @Version: v1.0
 */

@Api(value = "审批类型", tags = "审批类型")
@RestController
@RequestMapping(value = "/admin/process/processType")
public class OaProcessTypeController {

    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    //获取全部审批类型
    @ApiOperation(value = "获取全部审批类型")
    @GetMapping ("findAll")
    public Result findAll(){
        return Result.ok(oaProcessTypeService.list());
    }

    //分页查询
    @ApiOperation(value = "分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable long page, @PathVariable long limit){
        Page<ProcessType> processTypePage = new Page<>(page,limit);
        Page<ProcessType> page1 = oaProcessTypeService.page(processTypePage);

        return Result.ok(page1);
    }

    //增加
    @ApiOperation(value = "增加")
    @PostMapping ("save")
    public Result save(@RequestBody ProcessType processType){
        return oaProcessTypeService.save(processType)?Result.ok():Result.fail();
    }

    //删除
    @ApiOperation(value = "删除")
    @DeleteMapping ("remove/{id}")
    public Result remove(@PathVariable long id){
        return oaProcessTypeService.removeById(id)?Result.ok():Result.fail();
    }

    //修改
    @ApiOperation(value = "修改")
    @PutMapping ("update")
    public Result remove(@RequestBody ProcessType processType){
        return oaProcessTypeService.updateById(processType)?Result.ok():Result.fail();
    }

    //查
    @ApiOperation(value = "查找")
    @GetMapping ("get/{id}")
    public Result getById(@PathVariable long id){
        return Result.ok(oaProcessTypeService.getById(id));
    }
}
