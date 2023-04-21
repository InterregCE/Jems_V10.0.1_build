package io.cloudflight.jems.server.project.service.application.approve_application_with_conditions

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanApproveApplicationWithConditions
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ifIsValid
import io.cloudflight.jems.server.project.service.application.submit_application.ProjectStatusChangeEvent
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApproveApplicationWithConditions(
    private val projectPersistence: ProjectPersistence,
    private val generalValidatorService: GeneralValidatorService,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher
) : ApproveApplicationWithConditionsInteractor {

    @CanApproveApplicationWithConditions
    @Transactional
    @ExceptionWrapper(ApproveApplicationWithConditionsException::class)
    override fun approveWithConditions(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus =
        actionInfo.ifIsValid(generalValidatorService).let {
            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                applicationStateFactory.getInstance(projectSummary).approveWithConditions(actionInfo).also {
                    auditPublisher.publishEvent(ProjectStatusChangeEvent(this, projectSummary, it))
                }
            }
        }
}
