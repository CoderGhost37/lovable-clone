package com.kushagramathur.distributed_lovable_clone.workspace_service.mapper;

import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project.FileNode;
import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
