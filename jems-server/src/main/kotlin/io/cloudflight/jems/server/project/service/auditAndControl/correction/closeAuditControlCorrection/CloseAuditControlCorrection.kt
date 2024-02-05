package io.cloudflight.jems.server.project.service.auditAndControl.correction.closeAuditControlCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.service.account.corrections.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.project.authorization.CanCloseAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.AuditControlCorrectionFinancePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.AuditControlCorrectionMeasurePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.projectAuditControlCorrectionClosed
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class CloseAuditControlCorrection(
    private val auditControlPersistence: AuditControlPersistence,
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val correctionFinancePersistence: AuditControlCorrectionFinancePersistence,
    private val correctionMeasurePersistence: AuditControlCorrectionMeasurePersistence,
    private val ecPaymentCorrectionExtensionLinkingPersistence: EcPaymentCorrectionLinkPersistence,
    private val paymentAccountCorrectionExtensionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence,
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

        createCorrectionExtension(correctionId)

        return correctionPersistence.closeCorrection(correctionId).also {
            auditPublisher.publishEvent(
                projectAuditControlCorrectionClosed(this, auditControl, correctionNr = it.orderNr)
            )
        }.status
    }

    private fun createCorrectionExtension(correctionId: Long) {
        val correctionMeasure = correctionMeasurePersistence.getProgrammeMeasure(correctionId)
        val correctionFinance = correctionFinancePersistence.getCorrectionFinancialDescription(correctionId)
        if (correctionMeasure.scenario.allowsLinkingToEcPayment()) {
            ecPaymentCorrectionExtensionLinkingPersistence.createCorrectionExtension(
                correctionFinance,
                totalEligibleWithoutArt94or95 = correctionFinance.calculateTotalEligibleWithoutArt94or95(),
                unionContribution = BigDecimal.ZERO
            )
        } else if (correctionMeasure.scenario.allowsLinkingToPaymentAccount()) {
            paymentAccountCorrectionExtensionLinkingPersistence.createCorrectionExtension(correctionFinance)
        }
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
        val invalidPartnerReportAndLumpSum = correction.partnerReportId == null && correction.lumpSumOrderNr == null
        if (invalidPartnerReportAndLumpSum || correction.programmeFundId == null)
            throw PartnerOrReportOrFundNotSelectedException()
    }

    private fun ProjectCorrectionFinancialDescription.calculateTotalEligibleWithoutArt94or95() : BigDecimal =
        this.fundAmount + this.publicContribution + this.autoPublicContribution + this.privateContribution

}
