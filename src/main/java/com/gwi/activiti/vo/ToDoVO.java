package com.gwi.activiti.vo;

import lombok.Data;

@Data
public class ToDoVO {
    private String id;
    private String processInstanceId;
    private String businessKey;
    private String suspended;
    private String ended;
    private String tenantId;
    private String name;
    private String completed;
    private String activityId;
    private String activityName;
    private String assignee;
    private String assigneeName;
    private String startTime;
    private String endTime;

}
