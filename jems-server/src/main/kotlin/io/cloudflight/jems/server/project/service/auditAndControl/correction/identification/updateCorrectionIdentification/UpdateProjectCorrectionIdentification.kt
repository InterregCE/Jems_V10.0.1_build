package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.updateCorrectionIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.closeProjectAuditCorrection.PartnerOrReportNotSelectedException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.ProjectCorrectionIdentificationPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentificationUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.correction.updateProjectAuditCorrection.CorrectionIsInStatusClosedException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.updateProjectAuditCorrection.PartnerReportNotValidException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.updateProjectAuditCorrection.UpdateProjectAuditControlCorrectionException
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectCorrectionIdentification(
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val correctionIdentificationPersistence: ProjectCorrectionIdentificationPersistence,
    private val auditControlPersistence: AuditControlPersistence,
    private val partnerReportPersistence: ProjectPartnerReportPersistence
): UpdateProjectCorrectionIdentificationInteractor {

    @CanEditProjectCorrection
    @Transactional
    @ExceptionWrapper(UpdateProjectAuditControlCorrectionException::class)
    override fun updateProjectAuditCorrection(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long,
        correctionIdentificationUpdate: ProjectCorrectionIdentificationUpdate
    ): ProjectCorrectionIdentification {
        val auditControl = auditControlPersistence.getByIdAndProjectId(auditControlId, projectId)
        val correction = correctionPersistence.getByCorrectionId(correctionId)
        validatePartnerAndReport(correctionIdentificationUpdate)
        validateAuditControlStatus(auditControl)
        validateCorrectionStatus(correction)

        return correctionIdentificationPersistence.updateCorrectionIdentification(
            correctionId, correctionIdentificationUpdate
        )
    }

    private fun validateAuditControlStatus(auditControl: ProjectAuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlNotOngoingException()
    }

    private fun validateCorrectionStatus(correction: ProjectAuditControlCorrection) {
        if (correction.status.isClosed())
            throw CorrectionIsInStatusClosedException()
    }

    private fun validatePartnerAndReport(newData: ProjectCorrectionIdentificationUpdate) {
        if (newData.partnerReportId == null || newData.partnerId == null) {
            throw PartnerOrReportNotSelectedException()
        }

        val partnerReport = partnerReportPersistence.getPartnerReportByIdUnsecured(newData.partnerReportId)

        if (partnerReport.status.isNotCertified()) {
            throw PartnerReportNotValidException()
        }

    }
}
