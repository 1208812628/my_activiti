package com.gwi.activiti.dto;

import com.gwi.activiti.page.Paging;
import lombok.Data;

@Data
public class SearchTaskDTO extends Paging {
    private String processInstanceId;
    private String processInstanceBusinessKey;
    private String assignee;
    private String category;
    private String tenantId;
    private String activityType = "userTask";
    private String finished;
}
