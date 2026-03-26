package com.kushagramathur.lovable_clone.service;

import com.kushagramathur.lovable_clone.dto.deploy.DeployResponse;

public interface DeploymentService {

    DeployResponse deploy(Long projectId);
}
