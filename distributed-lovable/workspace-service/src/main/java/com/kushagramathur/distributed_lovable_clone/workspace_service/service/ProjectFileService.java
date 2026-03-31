package com.kushagramathur.distributed_lovable_clone.workspace_service.service;

import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project.FileContentResponse;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project.FileTreeResponse;

public interface ProjectFileService {
    FileTreeResponse getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
