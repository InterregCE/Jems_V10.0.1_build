package io.cloudflight.jems.server.project.service.save_project_version

import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary

interface CreateNewProjectVersionInteractor {
    fun create(projectId: Long): ProjectVersionSummary
}
