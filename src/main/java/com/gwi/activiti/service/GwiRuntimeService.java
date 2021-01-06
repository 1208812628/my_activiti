package com.gwi.activiti.service;

import com.gwi.activiti.dto.CreateProcessDTO;
import com.gwi.activiti.dto.ExecuteTaskDTO;
import com.gwi.activiti.dto.SearchTaskDTO;
import com.gwi.activiti.vo.TaskVO;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.TaskQueryProperty;
import org.activiti.engine.query.QueryProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.rest.common.api.DataResponse;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.activiti.rest.service.api.runtime.task.TaskActionRequest;
import org.activiti.rest.service.api.runtime.task.TaskPaginateList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GwiRuntimeService {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RestResponseFactory restResponseFactory;

    @Autowired
    private HistoryService historyService;

    private static HashMap<String, QueryProperty> taskProperties = new HashMap<String, QueryProperty>();

    static {
        taskProperties.put("id", TaskQueryProperty.TASK_ID);
        taskProperties.put("name", TaskQueryProperty.NAME);
        taskProperties.put("description", TaskQueryProperty.DESCRIPTION);
        taskProperties.put("dueDate", TaskQueryProperty.DUE_DATE);
        taskProperties.put("createTime", TaskQueryProperty.CREATE_TIME);
        taskProperties.put("priority", TaskQueryProperty.PRIORITY);
        taskProperties.put("executionId", TaskQueryProperty.EXECUTION_ID);
        taskProperties.put("processInstanceId", TaskQueryProperty.PROCESS_INSTANCE_ID);
        taskProperties.put("tenantId", TaskQueryProperty.TENANT_ID);
    }

    /**
     * 启动流程
     * @param dto
     * @return
     */
    public ProcessInstanceResponse createProcess(CreateProcessDTO dto){
        if(dto.getProcessDefinitionId()==null||"".equals(dto.getProcessDefinitionId())){
            throw new ActivitiIllegalArgumentException("processDefinitionId is required!");
        }
        Map<String, Object> startVariables = null;
        if (dto.getVariables() != null) {
            startVariables = new HashMap<String, Object>();
            for (RestVariable variable : dto.getVariables()) {
                if (variable.getName() == null) {
                    throw new ActivitiIllegalArgumentException("Variable name is required.");
                }
                startVariables.put(variable.getName(), restResponseFactory.getVariableValue(variable));
            }
        }

        try{
            // 启动流程
            ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
            processInstanceBuilder.processDefinitionId(dto.getProcessDefinitionId());
            if(startVariables!=null){
                processInstanceBuilder.variables(startVariables);
            }
            if(dto.getBusinessKey()!=null && !"".equals(dto.getBusinessKey())){
                processInstanceBuilder.businessKey(dto.getBusinessKey());
            }
            if(dto.getName()!=null && !"".equals(dto.getName())){
                processInstanceBuilder.name(dto.getName());
            }
            ProcessInstance processInstance = processInstanceBuilder.start();
            if (dto.isReturnVariables()) {
                Map<String, Object> runtimeVariableMap = null;
                List<HistoricVariableInstance> historicVariableList = null;
                if (processInstance.isEnded()) {
                    historicVariableList = historyService.createHistoricVariableInstanceQuery()
                            .processInstanceId(processInstance.getId()).list();
                } else {
                    runtimeVariableMap = runtimeService.getVariables(processInstance.getId());
                }
                return restResponseFactory.createProcessInstanceResponse(processInstance, true, runtimeVariableMap,
                        historicVariableList);
            }
            return restResponseFactory.createProcessInstanceResponse(processInstance);
        }
        catch(ActivitiObjectNotFoundException e){
            throw new ActivitiIllegalArgumentException(e.getMessage(),e);
        }
    }

    /**
     * 查询要处理的任务
     * @param dto
     * @return
     */
    public List<TaskVO> queryHandleTasks(SearchTaskDTO dto){
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (dto.getAssignee() != null&&!"".equals(dto.getAssignee())) {
            taskQuery.taskAssignee(dto.getAssignee());
        }
        if (dto.getProcessInstanceId() != null&&!"".equals(dto.getProcessInstanceId())) {
            taskQuery.processInstanceId(dto.getProcessInstanceId());
        }
        if (dto.getProcessInstanceBusinessKey() != null&&!"".equals(dto.getProcessInstanceBusinessKey())) {
            taskQuery.processInstanceBusinessKey(dto.getProcessInstanceBusinessKey());
        }
        List<Task> tasks = taskQuery.list();
        List<TaskVO> taskVOS = new ArrayList<TaskVO>();
        for(Task vo:tasks){
            TaskVO taskVO = new TaskVO();
            BeanUtils.copyProperties(vo,taskVO);
            taskVOS.add(taskVO);
        }
        return taskVOS;
    }

    /**
     * 查询待办任务
     * @return
     */
    public DataResponse queryWait(SearchTaskDTO dto){
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (dto.getAssignee() != null&&!"".equals(dto.getAssignee())) {
            taskQuery.taskAssignee(dto.getAssignee());
        }
        if (dto.getProcessInstanceId() != null&&!"".equals(dto.getProcessInstanceId())) {
            taskQuery.processInstanceId(dto.getProcessInstanceId());
        }
        if (dto.getProcessInstanceBusinessKey() != null&&!"".equals(dto.getProcessInstanceBusinessKey())) {
            taskQuery.processInstanceBusinessKey(dto.getProcessInstanceBusinessKey());
        }
        if (dto.getCategory() != null&&!"".equals(dto.getCategory())) {
            taskQuery.taskCategory(dto.getCategory());
        }
        if (dto.getTenantId() != null&&!"".equals(dto.getTenantId())) {
            taskQuery.taskTenantId(dto.getTenantId());
        }
        Map<String,String> map = new HashMap<String,String>();
        int pageNum = dto.getPageNum();
        int pageSize = dto.getPageSize();
        map.put("start",String.valueOf((pageNum-1)*pageSize));
        map.put("size",String.valueOf(pageSize));
        map.put("order",dto.getOrder());
        return  new TaskPaginateList(restResponseFactory).paginateList(map, taskQuery,
                "id", taskProperties);
    }

    /**
     * 根据任务号操作任务
     */
    public List<TaskVO> executeTask(ExecuteTaskDTO dto){
        if(dto.getTaskId()==null||"".equals(dto.getTaskId())){
            throw new ActivitiIllegalArgumentException("taskId is required");
        }
        if(dto.getAction()==null||"".equals(dto.getAction())){
            dto.setAction("complete");
        }
        if(TaskActionRequest.ACTION_COMPLETE.equals(dto.getAction())){
            completeTask(dto);
        }
        else{
            throw new ActivitiIllegalArgumentException("Invalid action: "+ dto.getAction());
        }
        if(dto.getProcessInstanceId()!=null&&!"".equals(dto.getProcessInstanceId())){
            SearchTaskDTO searchTaskDTO = new SearchTaskDTO();
            searchTaskDTO.setProcessInstanceId(dto.getProcessInstanceId());
            return queryHandleTasks(searchTaskDTO);
        }
        else{
            return new ArrayList<TaskVO>();
        }
    }

    /**
     * 完成任务
     */
    public void completeTask(ExecuteTaskDTO dto){
        Map<String, Object> variablesToSet = null;

        if (dto.getVariables() != null) {
            variablesToSet = new HashMap<String, Object>();
            for (RestVariable var : dto.getVariables()) {
                if (var.getName() == null) {
                    throw new ActivitiIllegalArgumentException("Variable name is required");
                }

                Object actualVariableValue = restResponseFactory.getVariableValue(var);
                variablesToSet.put(var.getName(), actualVariableValue);
            }
        }
        taskService.complete(dto.getTaskId(), variablesToSet);
    }

    /**
     * 获取流程实例详情
     * @param processInstanceId
     * @return
     */
    public ProcessInstanceResponse getProcessInstance(String processInstanceId){
        ProcessInstance processInstance =runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if(processInstance==null){
            throw new ActivitiIllegalArgumentException(
                    "can not find a process by processInstanceId: " + processInstanceId
            );
        }
        Map<String, Object> runtimeVariableMap = null;
        List<HistoricVariableInstance> historicVariableList = null;
        if (processInstance.isEnded()) {
            historicVariableList = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.getId()).list();
        } else {
            runtimeVariableMap = runtimeService.getVariables(processInstance.getId());
        }
        return restResponseFactory.createProcessInstanceResponse(processInstance, true, runtimeVariableMap,
                historicVariableList);
    }

    /**
     * 根据任务号查询流程变量
     * @param dto
     * @return
     */
    public List<RestVariable> queryRestVariablesByTask(SearchTaskDTO dto){
        return null;
    }
}
