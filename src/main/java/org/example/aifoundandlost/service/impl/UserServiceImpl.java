package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aifoundandlost.entity.User;
import org.example.aifoundandlost.mapper.UserMapper;
import org.example.aifoundandlost.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    // ======================== 登录 ========================
    @Override
    public User login(String account, String password) {
        if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
            throw new RuntimeException("账号和密码不能为空");
        }

        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        if (PHONE_PATTERN.matcher(account).matches()) {
            wrapper.eq(User::getPhoneNum, account);
        } else if (EMAIL_PATTERN.matcher(account).matches()) {
            wrapper.eq(User::getEmail, account);
        } else {
            throw new RuntimeException("账号格式错误，请输入手机号或邮箱");
        }

        User user = baseMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getRole() == 4) {
            throw new RuntimeException("该账号已被封禁，请联系管理员");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 更新最后登录时间
        lambdaUpdate()
                .eq(User::getUid, user.getUid())
                .set(User::getLastLoginTime, LocalDateTime.now())
                .update();

        return user;
    }

    // ======================== 注册 ========================
    @Override
    public boolean register(User user) {
        boolean hasPhone = StringUtils.hasText(user.getPhoneNum());
        boolean hasEmail = StringUtils.hasText(user.getEmail());

        if (!hasPhone && !hasEmail) {
            throw new RuntimeException("手机号和邮箱至少填写一项");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new RuntimeException("密码不能为空");
        }

        // 手机号重复校验
        if (hasPhone) {
            Long cntPhone = lambdaQuery().eq(User::getPhoneNum, user.getPhoneNum()).count();
            if (cntPhone > 0) throw new RuntimeException("该手机号已被注册");
        }

        // 邮箱重复校验
        if (hasEmail) {
            Long cntEmail = lambdaQuery().eq(User::getEmail, user.getEmail()).count();
            if (cntEmail > 0) throw new RuntimeException("该邮箱已被注册");
        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 默认值
        if (!StringUtils.hasText(user.getNickName())) {
            user.setNickName("用户" + (hasPhone ? user.getPhoneNum() : user.getEmail()));
        }
        if (!StringUtils.hasText(user.getAvatar())) {
            user.setAvatar("default/avatar.png");
        }

        // 角色默认普通用户
        if (user.getRole() == null) {
            user.setRole(1);
        }

        user.setCreateTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());

        return save(user);
    }

    // ======================== 查询 ========================
    @Override
    public User getUserByUid(Long uid) {
        if (uid == null) throw new RuntimeException("用户ID不能为空");
        return getById(uid);
    }

    @Override
    public Page<User> getUserPage(Integer current, Integer size, String keyword) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getNickName, keyword)
                    .or().like(User::getPhoneNum, keyword)
                    .or().like(User::getEmail, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    // ======================== 修改信息 ========================
    @Override
    public boolean updateUserInfo(User user) {
        if (user.getUid() == null) throw new RuntimeException("用户ID不能为空");
        User dbUser = getById(user.getUid());
        if (dbUser == null) throw new RuntimeException("用户不存在");
        return updateById(user);
    }

    @Override
    public boolean updateAvatar(Long uid, String avatarUrl) {
        if (uid == null || !StringUtils.hasText(avatarUrl)) throw new RuntimeException("参数不合法");
        return lambdaUpdate().eq(User::getUid, uid).set(User::getAvatar, avatarUrl).update();
    }

    @Override
    public boolean updatePassword(Long uid, String oldPassword, String newPassword) {
        User user = getById(uid);
        if (user == null) throw new RuntimeException("用户不存在");
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        return lambdaUpdate()
                .eq(User::getUid, uid)
                .set(User::getPassword, passwordEncoder.encode(newPassword))
                .update();
    }

    // ======================== 权限管理 ========================
    @Override
    public boolean setAdmin(Long uid) {
        User user = getById(uid);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getRole() == 3) throw new RuntimeException("超级管理员无需设置");
        return lambdaUpdate().eq(User::getUid, uid).set(User::getRole, 2).update();
    }

    @Override
    public boolean cancelAdmin(Long uid) {
        User user = getById(uid);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getRole() == 3) throw new RuntimeException("无法取消超级管理员");
        return lambdaUpdate().eq(User::getUid, uid).set(User::getRole, 1).update();
    }

    @Override
    public boolean banUser(Long uid) {
        User user = getById(uid);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getRole() == 3) throw new RuntimeException("无法封禁超级管理员");
        return lambdaUpdate().eq(User::getUid, uid).set(User::getRole, 4).update();
    }

    @Override
    public boolean unbanUser(Long uid) {
        User user = getById(uid);
        if (user == null) throw new RuntimeException("用户不存在");
        return lambdaUpdate().eq(User::getUid, uid).set(User::getRole, 1).update();
    }

    // ======================== 角色判断 ========================
    @Override
    public boolean isAdmin(Long uid) {
        User user = getById(uid);
        return user != null && (user.getRole() == 2 || user.getRole() == 3);
    }

    @Override
    public boolean isSuperAdmin(Long uid) {
        User user = getById(uid);
        return user != null && user.getRole() == 3;
    }
}