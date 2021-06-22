package io.cloudflight.jems.server.project.service.create_project

import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CreateProjectInteractor {

    fun createProject(acronym: String, callId: Long): Project

}
