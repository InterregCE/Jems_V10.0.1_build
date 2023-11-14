package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl.GetPartnerAndPartnerReportDataService
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAuditControlCorrection(
    private val auditControlPersistence: AuditControlPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
    private val allowedDataService: GetPartnerAndPartnerReportDataService,
): UpdateAuditControlCorrectionInteractor {

    @CanEditAuditControlCorrection
    @Transactional
    @ExceptionWrapper(UpdateAuditControlCorrectionException::class)
    override fun updateCorrection(
        correctionId: Long,
        data: AuditControlCorrectionUpdate,
    ): AuditControlCorrectionDetail {
        val correction = auditControlCorrectionPersistence.getByCorrectionId(correctionId)
        val auditControl = auditControlPersistence.getById(correction.auditControlId)
        validateAuditControlNotClosed(auditControl)
        validateAuditControlCorrectionNotClosed(correction)

        val allowedReportData = allowedDataService.getPartnerAndPartnerReportData(projectId = auditControl.projectId)
        validateReportAndFundSelectedAreValid(data, availableData = allowedReportData)

        return auditControlCorrectionPersistence.updateCorrection(correctionId, data)
    }

    private fun validateAuditControlNotClosed(auditControl: AuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlClosedException()
    }

    private fun validateAuditControlCorrectionNotClosed(correction: AuditControlCorrectionDetail) {
        if (correction.status.isClosed())
            throw AuditControlCorrectionClosedException()
    }


    private fun validateReportAndFundSelectedAreValid(input: AuditControlCorrectionUpdate, availableData: List<CorrectionAvailablePartner>) {
        val report = availableData.flatMap { it.availableReports }.first { it.id == input.partnerReportId }
        val fund = report.availableReportFunds.firstOrNull { it.id == input.programmeFundId }

        if (fund == null)
            throw CombinationOfReportAndFundIsInvalidException()
    }

}
