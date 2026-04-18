package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.User;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.UserService;
import org.example.aifoundandlost.util.UserContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // 白名单接口：无需登录，不调用UserContext
    @PostMapping("/login")
    public Result<?> login(String account, String password) {
        if (account == null || account.isBlank()) {
            throw new BusinessException(400, "账号不能为空");
        }
        if (password == null || password.isBlank()) {
            throw new BusinessException(400, "密码不能为空");
        }
        return Result.success(userService.login(account, password));
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        if (user == null) {
            throw new BusinessException(400, "用户信息不能为空");
        }
        userService.register(user);
        return Result.success();
    }

    @GetMapping("/get/{uid}")
    public Result<?> get(@PathVariable Long uid) {
        if (uid == null || uid <= 0) {
            throw new BusinessException(400, "用户UID不合法");
        }
        return Result.success(userService.getUserByUid(uid));
    }

    @GetMapping("/page")
    public Result<?> page(Integer current, Integer size, String keyword) {
        current = (current == null || current < 1) ? 1 : current;
        size = (size == null || size < 1) ? 10 : size;
        Page<User> page = userService.getUserPage(current, size, keyword);
        return Result.success(page);
    }

    // 登录接口：用你真实的UserContext.get()方法，加非空判断
    @PostMapping("/updateInfo")
    public Result<?> updateInfo(@RequestBody User user) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (user == null || user.getUid() == null) {
            throw new BusinessException(400, "用户UID不能为空");
        }
        // 仅本人可修改
        if (!user.getUid().equals(currentUid)) {
            throw new BusinessException(403, "无权限修改他人信息");
        }
        userService.updateUserInfo(user);
        return Result.success();
    }

    @PostMapping("/updateAvatar")
    public Result<?> updateAvatar(Long uid, String avatar) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (uid == null || uid <= 0) {
            throw new BusinessException(400, "UID不合法");
        }
        if (avatar == null || avatar.isBlank()) {
            throw new BusinessException(400, "头像不能为空");
        }
        if (!uid.equals(currentUid)) {
            throw new BusinessException(403, "无权限修改他人头像");
        }
        userService.updateAvatar(uid, avatar);
        return Result.success();
    }

    @PostMapping("/updatePwd")
    public Result<?> updatePwd(Long uid, String oldPwd, String newPwd) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (uid == null || uid <= 0) {
            throw new BusinessException(400, "UID不合法");
        }
        if (oldPwd == null || oldPwd.isBlank() || newPwd == null || newPwd.isBlank()) {
            throw new BusinessException(400, "旧密码/新密码不能为空");
        }
        if (!uid.equals(currentUid)) {
            throw new BusinessException(403, "无权限修改他人密码");
        }
        userService.updatePassword(uid, oldPwd, newPwd);
        return Result.success();
    }

    // 管理员接口：同样使用UserContext.get()
    @PostMapping("/setAdmin")
    public Result<?> setAdmin(Long uid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (uid == null || uid <= 0) {
            throw new BusinessException(400, "UID不合法");
        }
        if (!userService.isAdmin(currentUid)) {
            throw new BusinessException(403, "无管理员权限");
        }
        userService.setAdmin(uid);
        return Result.success();
    }

    @PostMapping("/cancelAdmin")
    public Result<?> cancelAdmin(Long uid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (uid == null || uid <= 0) {
            throw new BusinessException(400, "UID不合法");
        }
        if (!userService.isAdmin(currentUid)) {
            throw new BusinessException(403, "无管理员权限");
        }
        userService.cancelAdmin(uid);
        return Result.success();
    }

    @PostMapping("/ban")
    public Result<?> ban(Long uid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (uid == null || uid <= 0) {
            throw new BusinessException(400, "UID不合法");
        }
        if (!userService.isAdmin(currentUid)) {
            throw new BusinessException(403, "无管理员权限");
        }
        userService.banUser(uid);
        return Result.success();
    }

    @PostMapping("/unban")
    public Result<?> unban(Long uid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (uid == null || uid <= 0) {
            throw new BusinessException(400, "UID不合法");
        }
        if (!userService.isAdmin(currentUid)) {
            throw new BusinessException(403, "无管理员权限");
        }
        userService.unbanUser(uid);
        return Result.success();
    }

    @GetMapping("/isAdmin")
    public Result<?> isAdmin(Long uid) {
        if (uid == null || uid <= 0) {
            throw new BusinessException(400, "UID不合法");
        }
        return Result.success(userService.isAdmin(uid));
    }
}