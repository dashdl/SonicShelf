package com.zhongxin.sonicshelf.util;

public enum FavoriteType {
    MUSIC("music"),
    ALBUM("album"),
    PLAYLIST("playlist");

    private String value;

    FavoriteType(String value) {
        this.value = value;
    }

    public static String toChinese(String targetType) {

        return switch (targetType) {
            case "music" -> "音乐";
            case "album" -> "专辑";
            case "playlist" -> "歌单";
            default -> targetType;
        };
    }

    public String getValue() {
        return value;
    }

    // 根据值获取枚举实例
    public static FavoriteType fromValue(String value) {
        for (FavoriteType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    // 验证类型是否有效
    public static boolean isValidType(String type) {
        for (FavoriteType favoriteType : values()) {
            if (favoriteType.value.equals(type)) {
                return true;
            }
        }
        return false;
    }
}
