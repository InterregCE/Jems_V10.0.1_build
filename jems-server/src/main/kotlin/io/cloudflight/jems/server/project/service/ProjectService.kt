package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.server.project.service.model.ProjectForm

interface ProjectService {

    fun update(projectId: Long, projectData: InputProjectData): ProjectForm

}
