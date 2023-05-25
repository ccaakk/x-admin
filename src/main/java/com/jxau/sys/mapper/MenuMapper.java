package com.jxau.sys.mapper;

import com.jxau.sys.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author laocai
 * @since 2023-05-20
 */
public interface MenuMapper extends BaseMapper<Menu> {

    public List<Menu> getMenuListByUserId(@Param("userId") Integer userId, @Param("pid") Integer pid);
}
