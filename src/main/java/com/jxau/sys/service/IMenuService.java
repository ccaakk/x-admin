package com.jxau.sys.service;

import com.jxau.sys.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author laocai
 * @since 2023-05-20
 */
public interface IMenuService extends IService<Menu> {


    List<Menu> getAllMenu();

    List<Menu> getMenuListByUserId(Integer userId);
}
