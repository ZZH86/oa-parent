package com.ch.wechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ch.model.wechat.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author hui cao
 * @ClassName: MenuMapper
 * @Description:
 * @Date: 2023/5/27 10:35
 * @Version: v1.0
 */
@Repository
public interface MenuMapper extends BaseMapper<Menu> {
}
