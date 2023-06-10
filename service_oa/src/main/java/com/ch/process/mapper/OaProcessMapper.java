package com.ch.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ch.model.process.Process;
import com.ch.vo.process.ProcessQueryVo;
import com.ch.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author hui cao
 * @ClassName: OaProcessMapper
 * @Description:
 * @Date: 2023/5/6 21:01
 * @Version: v1.0
 */
@Mapper
public interface OaProcessMapper extends BaseMapper<Process> {

    //审批列表管理
    Page<ProcessVo> selectPage(Page<ProcessVo> page, @Param("vo") ProcessQueryVo processQueryVo);
}