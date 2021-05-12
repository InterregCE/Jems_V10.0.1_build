package io.cloudflight.jems.server.project.service.get_project

import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetProjectInteractor {

    fun getProjectCallSettings(projectId: Long): ProjectCallSettings

    fun getProject(projectId: Long, version: Int?): Project

    fun getAllProjects(pageable: Pageable): Page<ProjectSummary>

    fun getMyProjects(pageable: Pageable): Page<ProjectSummary>


}
