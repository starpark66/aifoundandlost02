package org.example.aifoundandlost.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("found_item")
public class FoundItem {
    @TableId(type = IdType.AUTO)
    private Long fid;

    private Long uid;
    private String name;
    private String area;
    private String detailArea;
    private String description;
    private String photoUrl;
    private Integer status;
    private Long likes;
    private Integer hot;
    private String call;
    private LocalDateTime foundTime;
    private String aiDescription;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}