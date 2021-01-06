package com.gwi.activiti.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gwi.activiti.dto.CreateProcessDTO;
import com.gwi.activiti.dto.ExecuteTaskDTO;
import com.gwi.activiti.dto.SearchTaskDTO;
import com.gwi.activiti.service.GwiRuntimeService;
import com.gwi.activiti.vo.TaskVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.task.Task;
import org.activiti.rest.common.api.DataResponse;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/run")
@Api("流程中心操作")
public class GwiRuntimeController {
    @Autowired
    private GwiRuntimeService gwiRuntimeService;

    @ApiOperation("启动流程")
    @PostMapping("/createProcess")
    public ProcessInstanceResponse createProcess(@RequestBody CreateProcessDTO dto){
        return gwiRuntimeService.createProcess(dto);
    }

    @ApiOperation("查询处理中的任务")
    @PostMapping("/queryHandleTasks")
    public List<TaskVO> queryHandleTasks(@RequestBody SearchTaskDTO dto){
        return gwiRuntimeService.queryHandleTasks(dto);
    }

    @ApiOperation("查询待办任务")
    @PostMapping("/queryWait")
    public DataResponse queryWait(@RequestBody SearchTaskDTO dto){
        return gwiRuntimeService.queryWait(dto);
    }

    @ApiOperation("执行任务")
    @PostMapping("/executeTask")
    public List<TaskVO> executeTask(@RequestBody ExecuteTaskDTO dto){
        return gwiRuntimeService.executeTask(dto);
    }

    @ApiOperation(value = "获取流程实例", tags = { "Process Instances" }, nickname = "getProcessInstance")
    @RequestMapping(value = "/process-instances/{processInstanceId}", method = RequestMethod.GET, produces = "application/json")
    public ProcessInstanceResponse getProcessInstance(
            @ApiParam(name = "processInstanceId", value = "The id of the process instance to get.") @PathVariable("processInstanceId") String processInstanceId) {
        return gwiRuntimeService.getProcessInstance(processInstanceId);
    }

    @ApiOperation("查询任务节点的流变量程")
    @GetMapping("/queryRestVariableByTask")
    public List<RestVariable> queryRestVariableByTask(@RequestBody SearchTaskDTO dto){
        return gwiRuntimeService.queryRestVariablesByTask(dto);
    }
}
