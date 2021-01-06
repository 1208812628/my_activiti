package com.gwi.activiti.dto;

import com.gwi.activiti.page.Paging;
import lombok.Data;

import java.util.Date;

@Data
public class SearchDefinitionDTO extends Paging {
    private String deploymentId;
    private String deploymentName;
    private String deploymentNameLike;
    private String category;
    private String tenantId;
    private Date deployDate;
}
