package com.gwi.activiti.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DeploymentVO {
    private String deploymentId;
    private String deploymentName;
    private String category;
    private String tenantId;
    private Date deployDate;
}
