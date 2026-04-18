package org.example.aifoundandlost.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.aifoundandlost.entity.User;

public interface UserService extends IService<User> {

    // 登录：支持 邮箱 / 手机号
    User login(String account, String password);

    // 注册
    boolean register(User user);

    // 根据uid获取用户
    User getUserByUid(Long uid);

    // 分页条件查询
    Page<User> getUserPage(Integer current, Integer size, String keyword);

    // 修改用户信息（昵称、邮箱等）
    boolean updateUserInfo(User user);

    // 修改头像
    boolean updateAvatar(Long uid, String avatarUrl);

    // 修改密码
    boolean updatePassword(Long uid, String oldPassword, String newPassword);

    // 设为管理员
    boolean setAdmin(Long uid);

    // 取消管理员
    boolean cancelAdmin(Long uid);

    // 封禁用户
    boolean banUser(Long uid);

    // 解封用户
    boolean unbanUser(Long uid);

    // 判断是否是管理员以上
    boolean isAdmin(Long uid);

    // 判断是否是超级管理员
    boolean isSuperAdmin(Long uid);
}