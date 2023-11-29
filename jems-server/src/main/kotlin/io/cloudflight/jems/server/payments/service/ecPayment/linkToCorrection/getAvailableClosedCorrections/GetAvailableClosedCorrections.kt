package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.getAvailableClosedCorrections

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.constructCorrectionFilter
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableClosedCorrections(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val correctionPersistence: AuditControlCorrectionPersistence
) : GetAvailableClosedCorrectionsInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableClosedCorrectionsException::class)
    override fun getClosedCorrectionList(pageable: Pageable, ecApplicationId: Long): Page<PaymentToEcCorrectionLinking> {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(ecApplicationId)
        val fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id

        val filter = if (ecPayment.status.isFinished())
            constructCorrectionFilter(ecPaymentIds = setOf(ecPayment.id))
        else
            constructCorrectionFilter(ecPaymentIds = setOf(null, ecPayment.id), fundId = fundId)

        return correctionPersistence.getCorrectionsLinkedToPaymentToEc(pageable, filter)
    }

}
