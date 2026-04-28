package com.carrental.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 分页响应对象
 */
@Data
public class PageResult<T> {

    private long total;
    private long page;
    private long size;
    private long pages;
    private List<T> records;

    private PageResult() {}

    /**
     * 从 MyBatis-Plus 分页对象构建
     */
    public static <T> PageResult<T> from(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.total = page.getTotal();
        result.page = page.getCurrent();
        result.size = page.getSize();
        result.pages = page.getPages();
        result.records = page.getRecords();
        return result;
    }

    /**
     * 构建空分页结果
     */
    public static <T> PageResult<T> empty(long page, long size) {
        PageResult<T> result = new PageResult<>();
        result.total = 0;
        result.page = page;
        result.size = size;
        result.pages = 0;
        result.records = Collections.emptyList();
        return result;
    }
}
