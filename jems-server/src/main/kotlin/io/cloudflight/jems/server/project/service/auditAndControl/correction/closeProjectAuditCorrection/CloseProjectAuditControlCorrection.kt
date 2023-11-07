package io.cloudflight.jems.server.project.service.auditAndControl.correction.closeProjectAuditCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanCloseProjectAuditAndControlCorrection
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.ProjectCorrectionIdentificationPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.projectAuditControlCorrectionClosed
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CloseProjectAuditControlCorrection(
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val correctionIdentificationPersistence: ProjectCorrectionIdentificationPersistence,
    private val auditControlPersistence: AuditControlPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistence: ProjectPersistence,
): CloseProjectAuditControlCorrectionInteractor {

    @CanCloseProjectAuditAndControlCorrection
    @Transactional
    @ExceptionWrapper(CloseProjectAuditControlCorrectionException::class)
    override fun closeProjectAuditCorrection(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): CorrectionStatus {
        val auditControl = auditControlPersistence.getByIdAndProjectId(auditControlId, projectId)
        val correctionIdentification =
            correctionIdentificationPersistence.getCorrectionIdentification(correctionId)

        validateAuditControlStatus(auditControl)
        validateCorrectionStatus(correctionIdentification.correction)
        validatePartnerAndReportAreSelected(correctionIdentification)

        val projectSummary = projectPersistence.getProjectSummary(projectId)

        return correctionPersistence.closeCorrection(correctionId).also {
            auditPublisher.publishEvent(
                projectAuditControlCorrectionClosed(
                    context = this,
                    projectSummary = projectSummary,
                    auditControl = auditControl,
                    correction = it
                )
            )
        }.status
    }

    private fun validateAuditControlStatus(auditControl: ProjectAuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlNotOngoingException()
    }

    private fun validateCorrectionStatus(correction: ProjectAuditControlCorrection) {
        if (correction.status.isClosed())
            throw ProjectCorrectionIsInStatusClosedException()
    }

    private fun validatePartnerAndReportAreSelected(correctionIdentification: ProjectCorrectionIdentification) {
        if (listOf(correctionIdentification.partnerId, correctionIdentification.partnerReportId, correctionIdentification.programmeFundId).any { it == null })
            throw PartnerOrReportOrFundNotSelectedException()

    }

}
