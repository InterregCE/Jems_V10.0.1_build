package io.cloudflight.jems.server.project.service.application.reject_modification

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectModificationCreate

interface RejectModificationInteractor {
    fun reject(projectId: Long, modification: ProjectModificationCreate): ApplicationStatus
}
