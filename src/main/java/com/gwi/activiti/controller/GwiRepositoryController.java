package com.gwi.activiti.controller;

import com.gwi.activiti.dto.SearchDefinitionDTO;
import com.gwi.activiti.dto.SearchDeploymentDTO;
import com.gwi.activiti.service.GwiRepositoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.rest.common.api.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/repository")
@Api("流程中心部署")
public class GwiRepositoryController {
    @Autowired
    private GwiRepositoryService gwiRepositoryService;

    @ApiOperation("部署流程文件")
    @PostMapping(value = "/deploy", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object deploy(@RequestPart(value = "file", required = true) MultipartFile file,
                         @RequestParam(value = "tenantId", required = false) String tenantId,
                         @RequestParam(value = "category", required = false) String category) {
        return gwiRepositoryService.deploy(file,tenantId,category);
    }

    @ApiOperation("查询流程部署列表")
    @PostMapping(value = "/getDeploymentList")
    public DataResponse getDeploymentList(@RequestBody SearchDeploymentDTO dto){
        return gwiRepositoryService.getDeploymentList(dto);
    }

    @ApiOperation("查询流程定义列表")
    @PostMapping(value = "/getDefinitionList")
    public DataResponse getDefinitionList(@RequestBody SearchDefinitionDTO dto){
        return gwiRepositoryService.getDefinitionList(dto);
    }
}
