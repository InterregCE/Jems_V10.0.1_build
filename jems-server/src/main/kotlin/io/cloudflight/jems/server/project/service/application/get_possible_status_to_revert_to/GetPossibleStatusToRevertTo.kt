package io.cloudflight.jems.server.project.service.application.get_possible_status_to_revert_to

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRevertDecision
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPossibleStatusToRevertTo(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
) : GetPossibleStatusToRevertToInteractor {

    @CanRevertDecision
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPossibleStatusToRevertToException::class)
    override fun get(projectId: Long): ApplicationStatus? =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).getPossibleStatusToRevertTo()
        }
}
