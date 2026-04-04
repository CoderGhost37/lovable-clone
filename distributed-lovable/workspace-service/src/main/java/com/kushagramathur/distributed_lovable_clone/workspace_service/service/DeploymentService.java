package com.kushagramathur.distributed_lovable_clone.workspace_service.service;

import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.deploy.DeployResponse;

public interface DeploymentService {

    DeployResponse deploy(Long projectId);
}
