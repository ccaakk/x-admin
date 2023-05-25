package com.jxau.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxau.common.vo.Result;
import com.jxau.sys.entity.Role;
import com.jxau.sys.entity.User;
import com.jxau.sys.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags = {"角色接口列表"})
@RestController
@RequestMapping(value = "/role")
@CrossOrigin
public class RoleController {

    @Autowired
    private IRoleService roleService;

    /**
     * 查询操作
     */
    @ApiOperation("角色查询(分页)")
    @GetMapping("/list")
    public Result<Map<String,Object>> getRoleList(@RequestParam(value = "roleName",required = false) String roleName,
                                                  @RequestParam(value = "pageNo") Long pageNo,
                                                  @RequestParam(value = "pageSize") Long pageSize){
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(roleName), Role::getRoleName,roleName)
                .orderByDesc(Role::getRoleId);
        Page<Role> page = new Page<>(pageNo,pageSize);
        roleService.page(page,wrapper);
        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());
        return Result.success(data);
    }

    @ApiOperation("新增角色")
    @PostMapping()
    public Result<Map<String,Object>> addRole(@RequestBody Role role){
        //将用户的密码加密
        roleService.addRole(role);
        return Result.success("新增角色成功");
    }

    @ApiOperation("修改角色")
    @PutMapping()
    public Result<?> updateRole(@RequestBody Role role){
        roleService.updateRole(role);
        return Result.success("修改角色成功");
    }

    @ApiOperation("通过ID查找角色")
    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable("id") Integer id){
        Role role = roleService.getRoleById(id);
        return Result.success(role);
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public Result<User> deleteRoleById(@PathVariable("id") Integer id){
        roleService.deleteRoleById(id);
        return Result.success("删除角色成功");
    }

    @ApiOperation("返回所有的角色")
    @GetMapping("/all")
    public Result<List<Role>> getAllRole(){
        List<Role> roleList = roleService.list();
        return Result.success(roleList);
    }
}
