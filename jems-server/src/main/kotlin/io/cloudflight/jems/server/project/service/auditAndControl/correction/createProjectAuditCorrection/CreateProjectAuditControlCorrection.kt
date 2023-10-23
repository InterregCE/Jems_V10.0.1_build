package io.cloudflight.jems.server.project.service.auditAndControl.correction.createProjectAuditCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectAuditAndControl
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.projectAuditControlCorrectionCreated
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProjectAuditControlCorrection(
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val auditControlPersistence: AuditControlPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistence: ProjectPersistence
): CreateProjectAuditControlCorrectionInteractor {

    @CanEditProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(CrateProjectAuditControlCorrectionException::class)
    override fun createProjectAuditCorrection(
        projectId: Long,
        auditControlId: Long,
        linkedToInvoice: Boolean
    ): ProjectAuditControlCorrection {
        val auditControl = auditControlPersistence.getByIdAndProjectId(auditControlId, projectId)
        validateAuditControlStatus(auditControl)

        val lastUsedOrderNr = correctionPersistence.getLastUsedOrderNr(auditControlId) ?: 0
        validateCorrectionsNumber(lastUsedOrderNr)

        val projectSummary = projectPersistence.getProjectSummary(projectId)

        return correctionPersistence.saveCorrection(
            ProjectAuditControlCorrection(
                id = 0,
                auditControlId = auditControlId,
                orderNr = lastUsedOrderNr + 1,
                status = CorrectionStatus.Ongoing,
                linkedToInvoice = linkedToInvoice,
            )
        ).also {
            auditPublisher.publishEvent(
                projectAuditControlCorrectionCreated(
                    context = this,
                    projectSummary = projectSummary,
                    auditControl = auditControl,
                    correction = it
                )
            )
        }
    }

    private fun validateAuditControlStatus(auditControl: ProjectAuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlIsInStatusClosedException()
    }

    private fun validateCorrectionsNumber(lastCorrectionNumber: Int) {
        if (lastCorrectionNumber >= 100) {
            throw MaximumNumberOfCorrectionsException()
        }
    }

}
