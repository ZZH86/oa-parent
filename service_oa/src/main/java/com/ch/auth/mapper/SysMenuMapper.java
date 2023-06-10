package com.ch.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ch.model.system.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author hui cao
 * @ClassName: SysMenuMapper
 * @Description:
 * @Date: 2023/4/15 13:41
 * @Version: v1.0
 */
@Repository
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);
}
