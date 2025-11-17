package com.zhongxin.sonicshelf.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlayHistory {
    private Long id;
    private Long userId;
    private Long musicId;
    private Integer playDuration;
    private LocalDateTime playTime;
}