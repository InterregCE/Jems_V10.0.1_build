package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.context.ApplicationEventPublisher

class ApprovedApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditPublisher: ApplicationEventPublisher,
    override val securityService: SecurityService,
    override val projectPersistence: ProjectPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence) {

    private val canBeRevertTo = setOf(
        ApplicationStatus.ELIGIBLE,
        ApplicationStatus.APPROVED_WITH_CONDITIONS,
        ApplicationStatus.CONDITIONS_SUBMITTED,
    )

    override fun returnToApplicant(): ApplicationStatus =
        returnToApplicantDefaultImpl()

    override fun revertDecision(): ApplicationStatus =
        revertCurrentStatusToPreviousStatus(validRevertStatuses = canBeRevertTo).also { reestablishedStatus ->
            when (reestablishedStatus) {
                ApplicationStatus.ELIGIBLE ->
                    projectWorkflowPersistence.clearProjectFundingDecision(projectSummary.id)
                ApplicationStatus.APPROVED_WITH_CONDITIONS, ApplicationStatus.CONDITIONS_SUBMITTED ->
                    projectWorkflowPersistence.resetProjectFundingDecisionToCurrentStatus(projectSummary.id)
                else -> Unit
            }
            // also delete optimization if project was already approved before
            projectVersionPersistence.deleteTimestampForApprovedModification(projectId = projectSummary.id)
            // delete data from institution-assignment table
            controllerInstitutionPersistence.deletePartnerDataInAssignmentsForProject(projectId = projectSummary.id)
        }

    override fun startModification(): ApplicationStatus {
        return projectWorkflowPersistence.updateProjectCurrentStatus(
            projectId = projectSummary.id,
            userId = securityService.getUserIdOrThrow(),
            status = ApplicationStatus.MODIFICATION_PRECONTRACTING
        )
    }

    override fun getPossibleStatusToRevertTo() =
        getPossibleStatusToRevertToDefaultImpl(canBeRevertTo)

    override fun setToContracted(): ApplicationStatus =
        projectWorkflowPersistence.updateProjectCurrentStatus(
            projectId = projectSummary.id,
            userId = securityService.getUserIdOrThrow(),
            status = ApplicationStatus.CONTRACTED
        )
}
