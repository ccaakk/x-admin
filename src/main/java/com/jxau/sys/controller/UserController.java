package com.jxau.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxau.common.vo.Result;
import com.jxau.sys.entity.User;
import com.jxau.sys.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author laocai
 * @since 2023-05-20
 */
@Api(tags = {"用户接口列表"})
@RestController
@RequestMapping(value = "/user")
@CrossOrigin
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ApiOperation("查询所有")
    @GetMapping("/all")
    public Result<List<User>> getALl(){
        List<User> users = userService.list();
        return  Result.success(users,"查询成功");
    }

    /**
     * 登录功能
     *
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody User user){
        Map<String,Object> data = userService.login(user);
        if (data != null){
            return Result.success(data);
        }
        return Result.fail(20002,"用户名或密码错误");
    }

    @ApiOperation("用户登录信息查询")
    @GetMapping("/info")
    public Result<Map<String,Object>> getUserInfo(@RequestParam("token") String token){
        Map<String,Object> data = userService.getUserInfo(token);
        if (data != null){
            return Result.success(data);
        }
        return  Result.fail(20003,"登录信息无效,请重新登录");
    }

    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public Result<Map<String,Object>> logout(@RequestHeader("X-Token") String token){
        userService.logout(token);
        return Result.success();
    }

    /**
     * 查询操作
     */
    @ApiOperation("用户查询(分页)")
    @GetMapping("/list")
    public Result<Map<String,Object>> getUserList(@RequestParam(value = "username",required = false) String username,
                                              @RequestParam(value = "phone",required = false) String phone,
                                              @RequestParam(value = "pageNo") Long pageNo,
                                              @RequestParam(value = "pageSize") Long pageSize){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(username), User::getUsername,username)
                .like(StringUtils.hasLength(phone), User::getPhone,phone)
                .orderByDesc(User::getId);
        Page<User> page = new Page<>(pageNo,pageSize);
        userService.page(page,wrapper);
        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());
        return Result.success(data);
    }

    @ApiOperation("新增用户")
    @PostMapping()
    public Result<Map<String,Object>> addUser(@RequestBody User user){
        //将用户的密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.addUser(user);
        return Result.success("新增用户成功");
    }

    @ApiOperation("修改用户")
    @PutMapping()
    public Result<?> updateUser(@RequestBody User user){
        user.setPassword(null);
        userService.updateUser(user);
        return Result.success("修改用户成功");
    }

    @ApiOperation("通过ID查找用户")
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Integer id){
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<User> deleteUserById(@PathVariable("id") Integer id){
        userService.deleteUserById(id);
        return Result.success("删除角色成功");
    }
}
