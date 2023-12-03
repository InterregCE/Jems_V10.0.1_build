package io.cloudflight.jems.server.project.service.auditAndControl.correction.closeAuditControlCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.project.authorization.CanCloseAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.AuditControlCorrectionFinancePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.AuditControlCorrectionMeasurePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.projectAuditControlCorrectionClosed
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CloseAuditControlCorrection(
    private val auditControlPersistence: AuditControlPersistence,
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val correctionFinancePersistence: AuditControlCorrectionFinancePersistence,
    private val correctionMeasurePersistence: AuditControlCorrectionMeasurePersistence,
    private val correctionExtensionLinkingPersistence: EcPaymentCorrectionLinkPersistence,
    private val auditPublisher: ApplicationEventPublisher,
): CloseAuditControlCorrectionInteractor {

    @CanCloseAuditControlCorrection
    @Transactional
    @ExceptionWrapper(CloseAuditControlCorrectionException::class)
    override fun closeCorrection(correctionId: Long): AuditControlStatus {
        val correction = correctionPersistence.getByCorrectionId(correctionId)
        val auditControl = auditControlPersistence.getById(correction.auditControlId)

        validateAuditControlNotClosed(auditControl)
        validateAuditControlCorrectionNotClosed(correction)
        validateReportAndFundAreAlreadySelected(correction)

        val correctionMeasure = correctionMeasurePersistence.getProgrammeMeasure(correctionId)
        if (correctionMeasure.scenario.scenarioAllowsLinkingToEcPayment()) {
            val correctionFinance = correctionFinancePersistence.getCorrectionFinancialDescription(correctionId)
            correctionExtensionLinkingPersistence.createCorrectionExtension(correctionFinance)
        }

        return correctionPersistence.closeCorrection(correctionId).also {
            auditPublisher.publishEvent(
                projectAuditControlCorrectionClosed(this, auditControl, correctionNr = it.orderNr)
            )
        }.status
    }

    private fun validateAuditControlNotClosed(auditControl: AuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlClosedException()
    }

    private fun validateAuditControlCorrectionNotClosed(correction: AuditControlCorrectionDetail) {
        if (correction.status.isClosed())
            throw AuditControlCorrectionClosedException()
    }

    private fun validateReportAndFundAreAlreadySelected(correction: AuditControlCorrectionDetail) {
        if (correction.partnerReportId == null || correction.programmeFundId == null)
            throw PartnerOrReportOrFundNotSelectedException()
    }

}
