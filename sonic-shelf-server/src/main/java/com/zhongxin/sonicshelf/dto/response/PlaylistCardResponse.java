package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PlaylistCardResponse {
    private Long id;
    private String coverImage;
    private String title;
    private Integer playCount;
    private List<String> songs;
}
