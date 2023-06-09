package com.jxau.sys.mapper;

import com.jxau.sys.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author laocai
 * @since 2023-05-20
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    public List<Integer> getMenuIdListByRoleId(Integer roleId);
}
