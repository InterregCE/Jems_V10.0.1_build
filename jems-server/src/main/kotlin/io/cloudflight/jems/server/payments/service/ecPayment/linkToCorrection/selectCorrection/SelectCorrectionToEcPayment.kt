package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.selectCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.deselectPayment.PaymentApplicationToEcNotInDraftException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelectCorrectionToEcPayment(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence
) : SelectCorrectionToEcPaymentInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(SelectCorrectionToEcException::class)
    override fun selectCorrectionToEcPayment(correctionId: Long, ecPaymentId: Long) {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(ecPaymentId)
        if (ecPayment.status != PaymentEcStatus.Draft)
            throw PaymentApplicationToEcNotInDraftException()

        val availableCorrections =
            ecPaymentCorrectionLinkPersistence.getCorrectionIdsAvailableForEcPayments(ecPayment.paymentApplicationToEcSummary.programmeFund.id)

        if (!availableCorrections.contains(correctionId)) {
            throw CorrectionNotAvailableForSelectionException()
        }

        ecPaymentCorrectionLinkPersistence.selectCorrectionToEcPayment(
            correctionIds = setOf(correctionId),
            ecPaymentId = ecPaymentId
        )
    }

}
