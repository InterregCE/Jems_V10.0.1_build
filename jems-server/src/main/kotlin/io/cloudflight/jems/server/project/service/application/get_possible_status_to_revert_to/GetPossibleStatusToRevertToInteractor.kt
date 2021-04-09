package io.cloudflight.jems.server.project.service.application.get_possible_status_to_revert_to

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface GetPossibleStatusToRevertToInteractor {
    fun get(projectId: Long): ApplicationStatus?
}
