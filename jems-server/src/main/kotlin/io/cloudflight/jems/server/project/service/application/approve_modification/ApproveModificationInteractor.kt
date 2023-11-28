package io.cloudflight.jems.server.project.service.application.approve_modification

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectModificationCreate

interface ApproveModificationInteractor {
    fun approveModification(projectId: Long, modification: ProjectModificationCreate): ApplicationStatus
}
