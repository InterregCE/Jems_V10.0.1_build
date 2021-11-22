package io.cloudflight.jems.server.project.service.application.approve_application

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ifIsValid
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStatusDecideApproved
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStatusDecideModificationApproved
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApproveApplication(
    private val projectPersistence: ProjectPersistence,
    private val generalValidatorService: GeneralValidatorService,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectAuthorization: ProjectAuthorization
) : ApproveApplicationInteractor {

    @Transactional
    @ExceptionWrapper(ApproveApplicationException::class)
    override fun approve(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus =
        actionInfo.ifIsValid(generalValidatorService).let {
            val project = projectPersistence.getProject(projectId)

            if (project.projectStatus.status == ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED) {
                projectAuthorization.hasPermission(ProjectStatusDecideModificationApproved, projectId)
            } else {
                projectAuthorization.hasPermission(ProjectStatusDecideApproved, projectId)
            }

            val assessment = if (project.isInStep2()) project.assessmentStep2 else project.assessmentStep1

            if (assessment?.assessmentQuality == null)
                throw QualityAssessmentMissing()

            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                applicationStateFactory.getInstance(projectSummary).approve(actionInfo).also {
                    auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
                }
            }
        }
}
