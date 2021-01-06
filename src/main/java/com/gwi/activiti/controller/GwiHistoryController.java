package com.gwi.activiti.controller;

import com.gwi.activiti.dto.SearchTaskDTO;
import com.gwi.activiti.service.GwiHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.rest.common.api.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@Api("流程中心查询历史")
public class GwiHistoryController {
    @Autowired
    private GwiHistoryService gwiHistoryService;

    @ApiOperation("查询已办任务")
    @PostMapping("/queryComplete")
    public DataResponse queryComplete(@RequestBody SearchTaskDTO dto){
        return gwiHistoryService.queryComplete(dto);
    }
}
