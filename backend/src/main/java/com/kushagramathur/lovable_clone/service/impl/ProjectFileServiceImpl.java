package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.project.FileContentResponse;
import com.kushagramathur.lovable_clone.dto.project.FileNode;
import com.kushagramathur.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {
    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {

    }
}
