package com.gwi.activiti.service;

import com.gwi.activiti.dto.SearchTaskDTO;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.impl.HistoricActivityInstanceQueryProperty;
import org.activiti.engine.query.QueryProperty;
import org.activiti.rest.common.api.DataResponse;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.history.HistoricActivityInstancePaginateList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GwiHistoryService {
    @Autowired
    private HistoryService historyService;

    @Autowired
    private RestResponseFactory restResponseFactory;

    private static Map<String, QueryProperty> historicActivityInstances = new HashMap<String, QueryProperty>();

    static {
        historicActivityInstances.put("activityId", HistoricActivityInstanceQueryProperty.ACTIVITY_ID);
        historicActivityInstances.put("activityName", HistoricActivityInstanceQueryProperty.ACTIVITY_NAME);
        historicActivityInstances.put("activityType", HistoricActivityInstanceQueryProperty.ACTIVITY_TYPE);
        historicActivityInstances.put("duration", HistoricActivityInstanceQueryProperty.DURATION);
        historicActivityInstances.put("endTime", HistoricActivityInstanceQueryProperty.END);
        historicActivityInstances.put("executionId", HistoricActivityInstanceQueryProperty.EXECUTION_ID);
        historicActivityInstances.put("activityInstanceId",
                HistoricActivityInstanceQueryProperty.HISTORIC_ACTIVITY_INSTANCE_ID);
        historicActivityInstances.put("processDefinitionId",
                HistoricActivityInstanceQueryProperty.PROCESS_DEFINITION_ID);
        historicActivityInstances.put("processInstanceId", HistoricActivityInstanceQueryProperty.PROCESS_INSTANCE_ID);
        historicActivityInstances.put("startTime", HistoricActivityInstanceQueryProperty.START);
        historicActivityInstances.put("tenantId", HistoricActivityInstanceQueryProperty.TENANT_ID);
    }

    public DataResponse queryComplete(SearchTaskDTO dto){
        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery();
        if(dto.getAssignee()!=null&&!"".equals(dto.getAssignee())){
            query.taskAssignee(dto.getAssignee());
        }
        if(dto.getTenantId()!=null&&!"".equals(dto.getTenantId())){
            query.activityTenantId(dto.getTenantId());
        }
        if(dto.getActivityType()!=null&&!"".equals(dto.getActivityType())){
            query.activityType(dto.getActivityType());
        }
        if(dto.getFinished()!=null&&!"".equals(dto.getFinished())){
            if(Boolean.parseBoolean(dto.getFinished())){
                query.finished();
            }
            else{
                query.unfinished();
            }
        }
        Integer pageNum = dto.getPageNum();
        Integer pageSize = dto.getPageSize();
        String order = dto.getOrder();
        Map<String, String> allRequestParams = new HashMap<String, String>();
        allRequestParams.put("start", String.valueOf((pageNum - 1) * pageSize));
        allRequestParams.put("size", String.valueOf(pageSize));
        allRequestParams.put("order", order);
        return new HistoricActivityInstancePaginateList(restResponseFactory).paginateList(allRequestParams,query,
                "startTime", historicActivityInstances);
    }
}
