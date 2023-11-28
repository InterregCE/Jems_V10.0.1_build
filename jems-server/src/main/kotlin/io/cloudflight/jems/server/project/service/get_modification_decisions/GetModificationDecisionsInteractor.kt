package io.cloudflight.jems.server.project.service.get_modification_decisions

import io.cloudflight.jems.server.project.service.model.ProjectModificationDecision

interface GetModificationDecisionsInteractor {
    fun getModificationDecisions(projectId: Long): List<ProjectModificationDecision>
}
