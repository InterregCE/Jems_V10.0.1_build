package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.selectPayment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelectPaymentToEc(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
) : SelectPaymentToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(SelectPaymentToEcException::class)
    override fun selectPaymentToEcPayment(paymentId: Long, ecPaymentId: Long) {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(ecPaymentId)
        if (ecPayment.status != PaymentEcStatus.Draft)
            throw PaymentApplicationToEcNotInDraftException()

        val linkedEcPaymentId = ecPaymentLinkPersistence.getPaymentExtension(paymentId).ecPaymentId
        if (linkedEcPaymentId != null)
            throw PaymentApplicationAlreadyTakenException(ecPaymentId = linkedEcPaymentId)

        ecPaymentLinkPersistence.selectPaymentToEcPayment(paymentIds = setOf(paymentId), ecPaymentId = ecPaymentId)
    }

}
