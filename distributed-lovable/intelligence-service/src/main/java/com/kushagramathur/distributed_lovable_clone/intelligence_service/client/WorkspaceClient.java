package com.kushagramathur.distributed_lovable_clone.intelligence_service.client;

import com.kushagramathur.distributed_lovable_clone.common_lib.dto.FileTreeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "workspace-service", path = "/workspace")
public interface WorkspaceClient {

    @GetMapping("/internal/v1/projects/{projectId}/files/tree")
    FileTreeDto getFileTree(@PathVariable("projectId") Long projectId);

    @GetMapping("/internal/v1/projects/{projectId}/files/content")
    String getFileContent(@PathVariable("projectId") Long projectId, @RequestParam("filePath") String filePath);

}
