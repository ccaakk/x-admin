package com.jxau.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxau.common.utils.JwtUtil;
import com.jxau.sys.entity.Menu;
import com.jxau.sys.entity.RoleMenu;
import com.jxau.sys.entity.User;
import com.jxau.sys.entity.UserRole;
import com.jxau.sys.mapper.MenuMapper;
import com.jxau.sys.mapper.UserMapper;
import com.jxau.sys.mapper.UserRoleMapper;
import com.jxau.sys.service.IMenuService;
import com.jxau.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author laocai
 * @since 2023-05-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(User user) {
        //根据用户名查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        User loginUser = this.baseMapper.selectOne(queryWrapper);
        //结果不为空并且用matches来比较加密前加密后的密码是否一致，则生成token，并将用户信息保存到redis
        if (loginUser != null && !("".equals(loginUser))
                && passwordEncoder.matches(user.getPassword(),loginUser.getPassword())){
            //使用UUID,jwt更优
//            String key ="user:" + UUID.randomUUID();

            //创建Jwt
            String token = jwtUtil.createToken(loginUser);
            //存入redis
            loginUser.setPassword(null);
//            redisTemplate.opsForValue().set(key,loginUser);
            //返回数据
            HashMap<String, Object> data = new HashMap<>();
            data.put("token",token);
            return data;
        }
        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {

        User loginUser = null;
        //根据token获取用户信息,从redis中获取
//        Object obj = redisTemplate.opsForValue().get(token);
        try {
           loginUser = jwtUtil.parseToken(token, User.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //发布序列化

        if (loginUser != null){
//            User loginUser = JSON.parseObject(JSON.toJSONString(obj), User.class);
            Map<String, Object> data = new HashMap<>();
            data.put("name",loginUser.getUsername());
            data.put("avatar",loginUser.getAvatar());

            //角色
            List<String> roleList = this.baseMapper.getRoleNameByUserId(loginUser.getId());
            data.put("roles",roleList);

            //权限列表
            List<Menu> menuList = menuService.getMenuListByUserId(loginUser.getId());
            data.put("menuList",menuList);

            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
//        redisTemplate.delete(token);
    }

    @Override
    @Transactional
    public void addUser(User user) {
        //写入用户表
        this.baseMapper.insert(user);
        //写入用户角色标
        if(null != user.getRoleIdList()){
            for (Integer roleId : user.getRoleIdList()) {
                userRoleMapper.insert(new UserRole(null,user.getId(),roleId));
            }
        }
    }

    @Override
    public User getUserById(Integer id) {
//        List<Integer> roleIdList = new ArrayList<>();
        User user = this.baseMapper.selectById(id);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,id);
        List<UserRole> userRoleList = userRoleMapper.selectList(wrapper);
//        for (UserRole role : userRoleList) {
//            roleIdList.add(role.getRoleId());
//        }
//        user.setRoleIdList(roleIdList);
        List<Integer> roleIdList = userRoleList.stream()
                                               .map(userRole -> {return userRole.getRoleId();})
                                               .collect(Collectors.toList());
        user.setRoleIdList(roleIdList);
        return user;
    }

    @Override
    @Transactional
    public void updateUser(User user) {

        //更新角色
        this.baseMapper.updateById(user);
        //删除用户角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,user.getId());
        userRoleMapper.delete(wrapper);
        //新增用户角色
        if(null != user.getRoleIdList()){
            for (Integer roleId : user.getRoleIdList()) {
                userRoleMapper.insert(new UserRole(null,user.getId(),roleId));
            }
        }
    }

    @Override
    public void deleteUserById(Integer id) {
        //删除用户
        this.baseMapper.deleteById(id);
        //删除用户的角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,id);
        userRoleMapper.delete(wrapper);
    }
}
