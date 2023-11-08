package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.updateCorrectionIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.closeProjectAuditCorrection.PartnerOrReportOrFundNotSelectedException
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
        validateAuditControlStatus(auditControl)
        validateCorrectionStatus(correction)
        validateMandatoryFields(correctionIdentificationUpdate)
        validatePartnerReportCertified(correctionIdentificationUpdate.partnerReportId!!)

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

    private fun validateMandatoryFields(newData: ProjectCorrectionIdentificationUpdate) {
        if (listOf(newData.partnerId, newData.partnerReportId, newData.programmeFundId).any { it == null }) {
            throw PartnerOrReportOrFundNotSelectedException()
        }
    }

    private fun validatePartnerReportCertified(partnerReportId: Long) {
        val partnerReport = partnerReportPersistence.getPartnerReportByIdUnsecured(partnerReportId)

        if (partnerReport.controlEnd == null) {
            throw PartnerReportNotValidException()
        }
    }
}
