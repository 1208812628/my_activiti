package com.gwi.activiti.page;

import lombok.Data;

@Data
public abstract class Paging {
    /**
     * 当前页
     */
    protected Integer pageNum = 1;
    /**
     * 每页数据条数
     */
    protected Integer pageSize = 20;
    /**
     * 排序字段
     */
    protected String orderBy;
    /**
     * 正序倒序
     */
    protected String order = "desc";
}
