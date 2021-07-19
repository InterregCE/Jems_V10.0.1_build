package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.server.project.service.model.ProjectForm
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectService {

    fun update(projectId: Long, projectData: InputProjectData): ProjectForm

}
