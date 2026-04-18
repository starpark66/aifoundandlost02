package org.example.aifoundandlost.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("lost_item")
public class LostItem {
    @TableId(type = IdType.AUTO)
    private Long lid;

    private Long uid;
    private LocalDateTime lostTime;
    private String description;
    private String area;
    private String detailArea;
    private String photoUrl;
    private Integer status;
    private Long likes;
    private Integer hot;
    private String call;
    private String aiDescription;
    private String name;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}