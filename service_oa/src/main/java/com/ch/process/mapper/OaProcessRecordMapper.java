package com.ch.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ch.model.process.ProcessRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author hui cao
 * @ClassName: OaProcessRecordMapper
 * @Description:
 * @Date: 2023/5/14 14:53
 * @Version: v1.0
 */
@Mapper
public interface OaProcessRecordMapper extends BaseMapper<ProcessRecord> {
}
