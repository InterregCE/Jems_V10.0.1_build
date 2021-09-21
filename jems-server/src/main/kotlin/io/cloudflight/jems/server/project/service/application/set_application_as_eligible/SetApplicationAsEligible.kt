package io.cloudflight.jems.server.project.service.application.set_application_as_eligible

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanSetApplicationAsEligible
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
class SetApplicationAsEligible(
    private val projectPersistence: ProjectPersistence,
    private val generalValidatorService: GeneralValidatorService,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher
) : SetApplicationAsEligibleInteractor {

    @CanSetApplicationAsEligible
    @Transactional
    @ExceptionWrapper(SetApplicationAsEligibleException::class)
    override fun setAsEligible(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus =
        actionInfo.ifIsValid(generalValidatorService).let {
            val project = projectPersistence.getProject(projectId)
            val assessment = if (project.isInStep2()) project.assessmentStep2 else project.assessmentStep1

            if (assessment?.assessmentEligibility == null)
                throw EligibilityAssessmentMissing()

            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                applicationStateFactory.getInstance(projectSummary).setAsEligible(actionInfo).also {
                    auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
                }
            }
        }
}
