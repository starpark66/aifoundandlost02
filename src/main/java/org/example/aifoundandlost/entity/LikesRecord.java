package org.example.aifoundandlost.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("likes_record")
public class LikesRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer targetType;
    private Long targetId;
    private Long uid;

    private LocalDateTime createTime;
}