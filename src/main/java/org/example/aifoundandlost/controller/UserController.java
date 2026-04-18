package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.User;
import org.example.aifoundandlost.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(String account, String password) {
        return Result.success(userService.login(account, password));
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    @GetMapping("/get/{uid}")
    public Result<?> get(@PathVariable Long uid) {
        return Result.success(userService.getUserByUid(uid));
    }

    @GetMapping("/page")
    public Result<?> page(Integer current, Integer size, String keyword) {
        Page<User> page = userService.getUserPage(current, size, keyword);
        return Result.success(page);
    }

    @PostMapping("/updateInfo")
    public Result<?> updateInfo(@RequestBody User user) {
        userService.updateUserInfo(user);
        return Result.success();
    }

    @PostMapping("/updateAvatar")
    public Result<?> updateAvatar(Long uid, String avatarUrl) {
        userService.updateAvatar(uid, avatarUrl);
        return Result.success();
    }

    @PostMapping("/updatePwd")
    public Result<?> updatePwd(Long uid, String oldPwd, String newPwd) {
        userService.updatePassword(uid, oldPwd, newPwd);
        return Result.success();
    }

    // 管理员接口
    @PostMapping("/setAdmin")
    public Result<?> setAdmin(Long uid) {
        userService.setAdmin(uid);
        return Result.success();
    }

    @PostMapping("/cancelAdmin")
    public Result<?> cancelAdmin(Long uid) {
        userService.cancelAdmin(uid);
        return Result.success();
    }

    @PostMapping("/ban")
    public Result<?> ban(Long uid) {
        userService.banUser(uid);
        return Result.success();
    }

    @PostMapping("/unban")
    public Result<?> unban(Long uid) {
        userService.unbanUser(uid);
        return Result.success();
    }

    @GetMapping("/isAdmin")
    public Result<?> isAdmin(Long uid) {
        return Result.success(userService.isAdmin(uid));
    }
}