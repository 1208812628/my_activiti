package com.gwi.activiti.dto;

import lombok.Data;
import org.activiti.rest.service.api.engine.variable.RestVariable;

import java.util.List;

@Data
public class CreateProcessDTO {
    private String processDefinitionId;
    private String name;
    private String businessKey;
    private List<RestVariable> variables;
    private String tenantId;
    private boolean returnVariables=false;
}
