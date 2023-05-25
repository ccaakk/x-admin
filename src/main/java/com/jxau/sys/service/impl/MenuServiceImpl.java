package com.jxau.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxau.sys.entity.Menu;
import com.jxau.sys.mapper.MenuMapper;
import com.jxau.sys.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {


    @Override
    public List<Menu> getAllMenu() {
        //一级菜单
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId,0);
        List<Menu> menuList = this.list(wrapper);
        //子菜单
        setMenuChildren(menuList);
        return menuList;
    }

    @Override
    public List<Menu> getMenuListByUserId(Integer userId) {
        //一级菜单
        List<Menu> menuList = this.baseMapper.getMenuListByUserId(userId, 0);
        //子菜单
        setMenuChildrenByUserId(userId, menuList);
        return menuList;
    }

    private void setMenuChildrenByUserId(Integer userId, List<Menu> menuList) {
        if(menuList != null){
            for (Menu subMenu : menuList) {
                List<Menu> subMenuList = this.baseMapper.getMenuListByUserId(userId, subMenu.getMenuId());
                subMenu.setChildren(subMenuList);
                //递归
                setMenuChildrenByUserId(userId,subMenuList);
            }
        }
    }


    //多级菜单查询
    private void setMenuChildren(List<Menu> menuList) {
        if (menuList != null){
            for (Menu menu : menuList) {
                LambdaQueryWrapper<Menu> subWrapper = new LambdaQueryWrapper<>();
                subWrapper.eq(Menu::getParentId,menu.getMenuId());
                List<Menu> subMenuList = this.list(subWrapper);
                menu.setChildren(subMenuList);
                //递归
                setMenuChildren(subMenuList);
            }
        }
    }
}
