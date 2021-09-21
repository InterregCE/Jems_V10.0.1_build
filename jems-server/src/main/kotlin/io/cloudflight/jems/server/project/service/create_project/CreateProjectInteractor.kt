package io.cloudflight.jems.server.project.service.create_project

import io.cloudflight.jems.server.project.service.model.ProjectDetail

interface CreateProjectInteractor {

    fun createProject(acronym: String, callId: Long): ProjectDetail

}
