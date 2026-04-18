package org.example.aifoundandlost.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long uid;

    private String nickName;
    private String email;
    private String phoneNum;
    private String password;
    private String avatar;
    private Integer role;

    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}