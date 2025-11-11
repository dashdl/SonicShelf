package com.zhongxin.sonicshelf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomArraySelector {
    private RandomArraySelector() {
    }

    /**
     * 从数组中随机抽取指定数量的元素（不重复）
     */
    public static <T> List<T> selectWithoutDuplicate(T[] array, int count) {
        if (array == null || array.length == 0 || count <= 0) {
            return Collections.emptyList();
        }

        if (count >= array.length) {
            return Arrays.asList(array);
        }

        List<T> list = new ArrayList<>(Arrays.asList(array));
        Collections.shuffle(list);
        return new ArrayList<>(list.subList(0, count));
    }
}
