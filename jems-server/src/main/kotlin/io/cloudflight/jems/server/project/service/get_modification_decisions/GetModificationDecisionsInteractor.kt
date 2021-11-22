package io.cloudflight.jems.server.project.service.get_modification_decisions

import io.cloudflight.jems.server.project.service.model.ProjectStatus

interface GetModificationDecisionsInteractor {
    fun getModificationDecisions(projectId: Long): List<ProjectStatus>
}
