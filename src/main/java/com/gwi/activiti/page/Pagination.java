package com.gwi.activiti.page;

import lombok.Data;

import java.util.List;

@Data
public class Pagination<T> extends Paging{
    private Integer totalPage;
    private Integer total;
    private List<T> list;
}
