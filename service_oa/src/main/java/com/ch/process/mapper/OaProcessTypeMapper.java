package com.ch.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ch.model.process.ProcessType;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author hui cao
 * @ClassName: OaProcessTypeController
 * @Description:
 * @Date: 2023/5/5 20:16
 * @Version: v1.0
 */
@Mapper
public interface OaProcessTypeMapper extends BaseMapper<ProcessType> {
}
