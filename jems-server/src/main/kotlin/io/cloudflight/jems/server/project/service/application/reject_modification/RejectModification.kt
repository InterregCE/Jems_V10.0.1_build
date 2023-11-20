package io.cloudflight.jems.server.project.service.application.reject_modification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.notification.handler.ProjectStatusChangeEvent
import io.cloudflight.jems.server.project.authorization.CanRejectModification
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ifIsValid
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectModificationCreate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class RejectModification(
    private val projectPersistence: ProjectPersistence,
    private val generalValidatorService: GeneralValidatorService,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
) : RejectModificationInteractor {

    @CanRejectModification
    @Transactional
    @ExceptionWrapper(RejectModificationException::class)
    override fun reject(projectId: Long, modification: ProjectModificationCreate): ApplicationStatus =
        modification.actionInfo.ifIsValid(generalValidatorService).let {
            validateCorrections(projectId, modification.correctionIds)
            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                applicationStateFactory.getInstance(projectSummary).rejectModification(modification.actionInfo).also {
                    auditPublisher.publishEvent(ProjectStatusChangeEvent(this, projectSummary, it))
                    auditControlCorrectionPersistence.updateModificationByCorrectionIds(
                        projectId = projectId,
                        correctionIds = modification.correctionIds,
                        statuses = listOf(ApplicationStatus.MODIFICATION_REJECTED)
                    )
                }
            }
        }

    private fun validateCorrections(projectId: Long, correctionIds: Set<Long>) {
        val projectCorrectionIds = auditControlCorrectionPersistence.getAllIdsByProjectId(projectId)
        val invalidCorrectionIds = correctionIds.minus(projectCorrectionIds)
        if (invalidCorrectionIds.isNotEmpty()) {
            throw CorrectionsNotValidException(invalidCorrectionIds)
        }
    }
}
