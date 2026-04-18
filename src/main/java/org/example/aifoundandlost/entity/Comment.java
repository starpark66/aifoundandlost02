package org.example.aifoundandlost.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long cid;

    private Integer targetType;
    private Long uid;
    private Long targetId;
    private Long parentId;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;

    //仅用于内存组装，不保存到数据库
    @TableField(exist = false)
    private List<Comment> children;
}