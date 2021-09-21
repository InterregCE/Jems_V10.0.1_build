package io.cloudflight.jems.server.project.service.file.list_project_file_metadata

import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListProjectFileMetadataInteractor {

    fun list(projectId: Long, projectFileCategory: ProjectFileCategory, page: Pageable): Page<ProjectFileMetadata>

}
