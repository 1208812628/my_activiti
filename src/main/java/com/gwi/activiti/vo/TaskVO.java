package com.gwi.activiti.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TaskVO {
    private String id;
    private String name;
    private String assignee;
    private String owner;
    private Date crateTime;
    private String category;
    private String tenantId;
    private String executionId;
    private String execution;
    private String processInstanceId;
    private String taskDefinitionKey;
    private Integer suspensionState;
    private Integer revision;
}
