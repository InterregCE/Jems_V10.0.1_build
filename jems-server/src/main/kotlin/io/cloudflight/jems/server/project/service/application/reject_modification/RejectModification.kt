package io.cloudflight.jems.server.project.service.application.reject_modification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanRejectModification
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ifIsValid
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class RejectModification(
    private val projectPersistence: ProjectPersistence,
    private val generalValidatorService: GeneralValidatorService,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher

): RejectModificationInteractor {

    @CanRejectModification
    @Transactional
    @ExceptionWrapper(RejectModificationException::class)
    override fun reject(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus =
        actionInfo.ifIsValid(generalValidatorService).let {
            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                applicationStateFactory.getInstance(projectSummary).rejectModification(actionInfo).also {
                    auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
                }
            }
        }
}
