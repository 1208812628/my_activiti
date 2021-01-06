package com.gwi.activiti.dto;

import lombok.Data;
import org.activiti.rest.service.api.engine.variable.RestVariable;

import java.util.List;

@Data
public class ExecuteTaskDTO {
    private String processInstanceId;
    private String taskId;
    private String owner;
    private String assignee;
    private String action;
    private List<RestVariable> variables;
}
