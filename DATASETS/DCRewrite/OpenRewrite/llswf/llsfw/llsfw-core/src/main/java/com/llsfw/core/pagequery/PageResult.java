/**
 * PageResult.java
 * Created at 2013-12-02
 * Created by wangkang
 * Copyright (C) llsfw.
 */
package com.llsfw.core.pagequery;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * ClassName: PageResult
 * </p>
 * <p>
 * Description: 分页类
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2013年12月2日
 * </p>
 */
public class PageResult<E> extends ArrayList<E> { // NOSONAR

    /**
     * 序列化标志
     */
    private static final long serialVersionUID = 1L;

    /**
     * 当前页,默认1
     */
    private int curPage = 1;

    /**
     * 每页数量,默认0
     */
    private int pageSize = 0;

    /**
     * 总页数
     */
    private int totalPages = 0;

    /**
     * 总条数
     */
    private int totalRecords = 0;

    /**
     * 只读属性,是否已经存在分页总条数,默认为false,当调用setTotalRecords方法的时候,会变更为true
     */
    private boolean hasTotalRecords = false;

    public int getCurPage() {
        return this.curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalRecords() {
        return this.totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
        this.hasTotalRecords = true;
    }

    public boolean isHasTotalRecords() {
        return hasTotalRecords;
    }

    /**
     * 强制类型转换为List
     * 
     * @return
     */
    public List<E> toList() {
        return (List<E>) this;
    }
}
