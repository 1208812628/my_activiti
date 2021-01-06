package com.gwi.activiti.service;

import com.gwi.activiti.dto.SearchDefinitionDTO;
import com.gwi.activiti.dto.SearchDeploymentDTO;
import com.gwi.activiti.vo.DeploymentVO;
import org.activiti.engine.impl.DeploymentQueryProperty;
import org.activiti.engine.query.QueryProperty;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.rest.common.api.DataResponse;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.repository.DeploymentsPaginateList;
import org.activiti.rest.service.api.repository.ProcessDefinitionsPaginateList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service
public class GwiRepositoryService {
    private static Map<String, QueryProperty> deploymentsProperties = new HashMap<String, QueryProperty>();

    static {
        deploymentsProperties.put("id", DeploymentQueryProperty.DEPLOYMENT_ID);
        deploymentsProperties.put("name", DeploymentQueryProperty.DEPLOYMENT_NAME);
        deploymentsProperties.put("deployTime", DeploymentQueryProperty.DEPLOY_TIME);
        deploymentsProperties.put("tenantId", DeploymentQueryProperty.DEPLOYMENT_TENANT_ID);
    }

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RestResponseFactory restResponseFactory;

    /**
     *部署流程文件
     * @param file
     * @param tenantId
     * @param category
     * @return
     */
    public Deployment deploy(MultipartFile file, String tenantId, String category) {
        try {
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
            String fileName = file.getOriginalFilename();
            if (StringUtils.isEmpty(fileName) || !(fileName.endsWith(".bpmn20.xml") || fileName.endsWith(".bpmn")
                    || fileName.toLowerCase().endsWith(".bar") || fileName.toLowerCase().endsWith(".zip"))) {
                fileName = file.getName();
            }

            if (fileName.endsWith(".bpmn20.xml") || fileName.endsWith(".bpmn")) {
                deploymentBuilder.addInputStream(fileName, file.getInputStream());
            } else if (fileName.toLowerCase().endsWith(".bar") || fileName.toLowerCase().endsWith(".zip")) {
                deploymentBuilder.addZipInputStream(new ZipInputStream(file.getInputStream()));
            } else {
                throw new ActivitiIllegalArgumentException("File must be of type .bpmn20.xml, .bpmn, .bar or .zip");
            }
            deploymentBuilder.name(fileName);
            if (tenantId != null) {
                deploymentBuilder.tenantId(tenantId);
            }
            if (category != null) {
                deploymentBuilder.category(category);
            }
            Deployment deployment = deploymentBuilder.deploy();
            DeploymentVO vo = new DeploymentVO();
            vo.setDeploymentId(deployment.getId());
            vo.setDeploymentName(deployment.getName());
            vo.setCategory(deployment.getCategory());
            vo.setTenantId(deployment.getTenantId());
            vo.setDeployDate(deployment.getDeploymentTime());
            return deployment;
        } catch (Exception e) {
            if (e instanceof ActivitiException) {
                throw (ActivitiException) e;
            }
            throw new ActivitiException(e.getMessage(), e);
        }
    }

    /**
     * 获取流程部署列表
     * @param dto
     * @return
     */
    public DataResponse getDeploymentList(SearchDeploymentDTO dto){
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        if(dto.getDeploymentId()!=null&&!"".equals(dto.getDeploymentId())){
            deploymentQuery.deploymentId(dto.getDeploymentId());
        }
        if(dto.getDeploymentName()!=null&&!"".equals(dto.getDeploymentName())){
            deploymentQuery.deploymentName(dto.getDeploymentName());
        }
        if(dto.getDeploymentNameLike()!=null&&!"".equals(dto.getDeploymentNameLike())){
            deploymentQuery.deploymentNameLike(dto.getDeploymentNameLike());
        }
        if(dto.getCategory()!=null&&!"".equals(dto.getCategory())){
            deploymentQuery.deploymentCategory(dto.getCategory());
        }
        if(dto.getTenantId()!=null&&!"".equals(dto.getTenantId())){
            deploymentQuery.deploymentTenantId(dto.getTenantId());
        }
        Map<String,String> map = new HashMap<String,String>();
        int pageNum = dto.getPageNum();
        int pageSize = dto.getPageSize();
        map.put("start",String.valueOf((pageNum-1)*pageSize));
        map.put("size",String.valueOf(pageSize));
        map.put("order",dto.getOrder());
        return new DeploymentsPaginateList(restResponseFactory)
                .paginateList(map,deploymentQuery,"id",deploymentsProperties);
    }

    /**
     * 获取流程定义列表
     * @param dto
     * @return
     */
    public DataResponse getDefinitionList(SearchDefinitionDTO dto){
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if(dto.getDeploymentId()!=null&&!"".equals(dto.getDeploymentId())){
            processDefinitionQuery.deploymentId(dto.getDeploymentId());
        }
        if(dto.getDeploymentName()!=null&&!"".equals(dto.getDeploymentName())){
            processDefinitionQuery.processDefinitionName(dto.getDeploymentName());
        }
        if(dto.getDeploymentNameLike()!=null&&!"".equals(dto.getDeploymentNameLike())){
            processDefinitionQuery.processDefinitionNameLike(dto.getDeploymentNameLike());
        }
        if(dto.getCategory()!=null&&!"".equals(dto.getCategory())){
            processDefinitionQuery.processDefinitionCategory(dto.getCategory());
        }
        if(dto.getTenantId()!=null&&!"".equals(dto.getTenantId())){
            processDefinitionQuery.processDefinitionTenantId(dto.getTenantId());
        }
        Map<String,String> map = new HashMap<String,String>();
        int pageNum = dto.getPageNum();
        int pageSize = dto.getPageSize();
        map.put("start",String.valueOf((pageNum-1)*pageSize));
        map.put("size",String.valueOf(pageSize));
        map.put("order",dto.getOrder());
        return new ProcessDefinitionsPaginateList(restResponseFactory)
                .paginateList(map,processDefinitionQuery,"id",deploymentsProperties);
    }



}
