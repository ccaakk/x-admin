package com.jxau.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxau.sys.entity.Role;
import com.jxau.sys.entity.RoleMenu;
import com.jxau.sys.mapper.RoleMapper;
import com.jxau.sys.mapper.RoleMenuMapper;
import com.jxau.sys.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author laocai
 * @since 2023-05-20
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    @Transactional
    public void addRole(Role role) {
        //写入角色表
        this.baseMapper.insert(role);
        //写入角色菜单关系表
        if(null != role.getMenuIdList()){
            for (Integer menuId : role.getMenuIdList()) {
                roleMenuMapper.insert(new RoleMenu(null,role.getRoleId(),menuId));
            }
        }
    }

    @Override
    public Role getRoleById(Integer id) {
        Role role = this.baseMapper.selectById(id);
        List<Integer> menuIdList = roleMenuMapper.getMenuIdListByRoleId(id);
        role.setMenuIdList(menuIdList);
        return role;
    }

    @Override
    public void updateRole(Role role) {
        //修改角色标
        this.updateById(role);
        //删除现有权限
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId,role.getRoleId());
        roleMenuMapper.delete(wrapper);
        //新增权限
        if(null != role.getMenuIdList()){
            for (Integer menuId : role.getMenuIdList()) {
                roleMenuMapper.insert(new RoleMenu(null,role.getRoleId(),menuId));
            }
        }
    }

    @Override
    public void deleteRoleById(Integer id) {
        //删除角色
        this.baseMapper.deleteById(id);
        //删除角色权限
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId,id);
        roleMenuMapper.delete(wrapper);
    }
}
