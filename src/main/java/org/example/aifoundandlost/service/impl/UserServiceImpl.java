package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.aifoundandlost.entity.User;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.mapper.UserMapper;
import org.example.aifoundandlost.service.UserService;
import org.example.aifoundandlost.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 角色定义（严格按你要求）
    private static final int ROLE_UNREGISTERED = 0;
    private static final int ROLE_NORMAL = 1;
    private static final int ROLE_ADMIN = 2;
    private static final int ROLE_SUPER_ADMIN = 3;
    private static final int ROLE_BANNED = 4;

    // 正则校验规则（标准手机号+邮箱）
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    // ====================== 1. 登录（邮箱/手机号 + 格式校验） ======================
    @Override
    public User login(String account, String password) {
        if (!StringUtils.hasText(account)) {
            throw new BusinessException(400, "账号不能为空");
        }
        if (!StringUtils.hasText(password)) {
            throw new BusinessException(400, "密码不能为空");
        }

        // 登录账号格式校验：必须是合法手机号或邮箱
        boolean isPhone = account.matches(PHONE_REGEX);
        boolean isEmail = account.matches(EMAIL_REGEX);
        if (!isPhone && !isEmail) {
            throw new BusinessException(400, "账号格式错误，请输入合法手机号或邮箱");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhoneNum, account)
                .or()
                .eq(User::getEmail, account);

        User user = getOne(wrapper);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 封禁/未注册用户禁止登录
        if (user.getRole() == ROLE_BANNED) {
            throw new BusinessException(403, "该用户已被封禁，无法登录");
        }
        if (user.getRole() == ROLE_UNREGISTERED) {
            throw new BusinessException(403, "该用户未完成注册");
        }

        if (!PasswordUtil.match(password, user.getPassword())) {
            throw new BusinessException(400, "密码错误");
        }

        log.info("用户登录成功，uid:{}", user.getUid());
        return user;
    }

    // ====================== 2. 注册（手机号/邮箱格式正则校验） ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(User user) {
        boolean hasPhone = StringUtils.hasText(user.getPhoneNum());
        boolean hasEmail = StringUtils.hasText(user.getEmail());
        if (!hasPhone && !hasEmail) {
            throw new BusinessException(400, "手机号和邮箱不能同时为空");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new BusinessException(400, "密码不能为空");
        }
        if (user.getPassword().length() < 6) {
            throw new BusinessException(400, "密码长度不能少于6位");
        }

        // 手机号格式校验
        if (hasPhone && !user.getPhoneNum().matches(PHONE_REGEX)) {
            throw new BusinessException(400, "手机号格式不合法");
        }
        // 邮箱格式校验
        if (hasEmail && !user.getEmail().matches(EMAIL_REGEX)) {
            throw new BusinessException(400, "邮箱格式不合法");
        }

        // 校验手机号/邮箱是否重复
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (hasPhone) wrapper.eq(User::getPhoneNum, user.getPhoneNum());
        if (hasEmail) wrapper.or().eq(User::getEmail, user.getEmail());
        if (count(wrapper) > 0) {
            throw new BusinessException(400, "该手机号/邮箱已被注册");
        }

        // 密码加密 + 默认普通用户
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        user.setRole(ROLE_NORMAL);

        boolean saveSuccess = save(user);
        if (!saveSuccess) {
            throw new BusinessException(500, "用户注册失败");
        }

        log.info("用户注册成功，自动生成uid:{}", user.getUid());
        return true;
    }

    // ====================== 3. 根据uid获取用户 ======================
    @Override
    public User getUserByUid(Long uid) {
        if (uid == null || uid <= 0) {
            throw new BusinessException(400, "用户UID不合法");
        }

        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUid, uid));
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    // ====================== 4. 分页条件查询 ======================
    @Override
    public Page<User> getUserPage(Integer current, Integer size, String keyword) {
        if (current == null || current < 1) {
            throw new BusinessException(400, "页码不合法");
        }
        if (size == null || size < 1 || size > 100) {
            throw new BusinessException(400, "每页条数需在1-100之间");
        }

        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getNickName, keyword)
                    .or()
                    .like(User::getPhoneNum, keyword)
                    .or()
                    .like(User::getEmail, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);

        page(page, wrapper);
        return page;
    }

    // ====================== 5. 修改用户信息（手机号/邮箱格式校验） ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(User user) {
        if (user.getUid() == null || user.getUid() <= 0) {
            throw new BusinessException(400, "用户UID不合法");
        }
        User exist = getUserByUid(user.getUid());

        // 修改手机号：格式校验 + 重复校验
        if (StringUtils.hasText(user.getPhoneNum())) {
            if (!user.getPhoneNum().matches(PHONE_REGEX)) {
                throw new BusinessException(400, "手机号格式不合法");
            }
            LambdaQueryWrapper<User> phoneWp = new LambdaQueryWrapper<>();
            phoneWp.eq(User::getPhoneNum, user.getPhoneNum()).ne(User::getUid, user.getUid());
            if (count(phoneWp) > 0) throw new BusinessException(400, "手机号已被使用");
        }
        // 修改邮箱：格式校验 + 重复校验
        if (StringUtils.hasText(user.getEmail())) {
            if (!user.getEmail().matches(EMAIL_REGEX)) {
                throw new BusinessException(400, "邮箱格式不合法");
            }
            LambdaQueryWrapper<User> emailWp = new LambdaQueryWrapper<>();
            emailWp.eq(User::getEmail, user.getEmail()).ne(User::getUid, user.getUid());
            if (count(emailWp) > 0) throw new BusinessException(400, "邮箱已被使用");
        }

        User update = new User();
        update.setUid(user.getUid());
        update.setNickName(user.getNickName());
        update.setPhoneNum(user.getPhoneNum());
        update.setEmail(user.getEmail());

        return updateById(update);
    }

    // ====================== 6. 修改头像 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAvatar(Long uid, String avatarUrl) {
        if (uid == null || uid <= 0) throw new BusinessException(400, "UID不合法");
        if (!StringUtils.hasText(avatarUrl)) throw new BusinessException(400, "头像地址不能为空");
        getUserByUid(uid);

        User update = new User();
        update.setUid(uid);
        update.setAvatar(avatarUrl);
        return updateById(update);
    }

    // ====================== 7. 修改密码 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long uid, String oldPassword, String newPassword) {
        if (uid == null || uid <= 0) throw new BusinessException(400, "UID不合法");
        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new BusinessException(400, "旧密码/新密码不能为空");
        }
        if (newPassword.length() < 6) throw new BusinessException(400, "新密码长度不能少于6位");

        User user = getUserByUid(uid);
        if (!PasswordUtil.match(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "旧密码错误");
        }

        User update = new User();
        update.setUid(uid);
        update.setPassword(PasswordUtil.encode(newPassword));
        return updateById(update);
    }

    // ====================== 8. 设为管理员 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setAdmin(Long uid) {
        User user = getUserByUid(uid);
        if (user.getRole() == ROLE_SUPER_ADMIN) throw new BusinessException(400, "超管无需设置");
        if (user.getRole() == ROLE_ADMIN) throw new BusinessException(400, "已是管理员");
        if (user.getRole() == ROLE_BANNED) throw new BusinessException(400, "封禁用户无法设为管理员");

        User update = new User();
        update.setUid(uid);
        update.setRole(ROLE_ADMIN);
        return updateById(update);
    }

    // ====================== 9. 取消管理员 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAdmin(Long uid) {
        User user = getUserByUid(uid);
        if (user.getRole() == ROLE_SUPER_ADMIN) throw new BusinessException(403, "无法取消超管权限");
        if (user.getRole() != ROLE_ADMIN) throw new BusinessException(400, "该用户不是管理员");

        User update = new User();
        update.setUid(uid);
        update.setRole(ROLE_NORMAL);
        return updateById(update);
    }

    // ====================== 10. 封禁用户 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean banUser(Long uid) {
        User user = getUserByUid(uid);
        if (user.getRole() == ROLE_SUPER_ADMIN) throw new BusinessException(403, "超管无法被封禁");
        if (user.getRole() == ROLE_BANNED) throw new BusinessException(400, "用户已封禁");

        User update = new User();
        update.setUid(uid);
        update.setRole(ROLE_BANNED);
        return updateById(update);
    }

    // ====================== 11. 解封用户 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbanUser(Long uid) {
        User user = getUserByUid(uid);
        if (user.getRole() != ROLE_BANNED) throw new BusinessException(400, "用户未被封禁");

        User update = new User();
        update.setUid(uid);
        update.setRole(ROLE_NORMAL);
        return updateById(update);
    }

    // ====================== 12. 是否是管理员以上 ======================
    @Override
    public boolean isAdmin(Long uid) {
        User user = getUserByUid(uid);
        return user.getRole() >= ROLE_ADMIN;
    }

    // ====================== 13. 是否是超级管理员 ======================
    @Override
    public boolean isSuperAdmin(Long uid) {
        User user = getUserByUid(uid);
        return user.getRole() == ROLE_SUPER_ADMIN;
    }
}