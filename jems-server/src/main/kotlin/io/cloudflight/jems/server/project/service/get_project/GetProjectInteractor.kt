package io.cloudflight.jems.server.project.service.get_project

import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectForm
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetProjectInteractor {

    fun getProjectCallSettings(projectId: Long): ProjectCallSettings

    fun getProjectDetail(projectId: Long, version: String? = null): ProjectDetail

    fun getProjectForm(projectId: Long, version: String? = null): ProjectForm

    fun getAllProjects(pageable: Pageable): Page<ProjectSummary>

    fun getMyProjects(pageable: Pageable): Page<ProjectSummary>


}
