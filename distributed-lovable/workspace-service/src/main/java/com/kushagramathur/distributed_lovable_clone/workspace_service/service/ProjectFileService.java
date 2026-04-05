package com.kushagramathur.distributed_lovable_clone.workspace_service.service;

import com.kushagramathur.distributed_lovable_clone.common_lib.dto.FileTreeDto;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project.FileContentResponse;

public interface ProjectFileService {
    FileTreeDto getFileTree(Long projectId);

    String getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
