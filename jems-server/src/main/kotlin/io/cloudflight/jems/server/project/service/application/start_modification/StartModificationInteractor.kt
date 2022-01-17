package io.cloudflight.jems.server.project.service.application.start_modification

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface StartModificationInteractor {

    fun startModification(projectId: Long): ApplicationStatus
}
