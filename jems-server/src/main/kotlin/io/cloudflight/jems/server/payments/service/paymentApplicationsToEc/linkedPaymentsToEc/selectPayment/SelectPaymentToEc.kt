package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.selectPayment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelectPaymentToEc(
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
) : SelectPaymentToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(SelectPaymentToEcException::class)
    override fun selectPaymentToEcPayment(paymentId: Long, ecPaymentId: Long) {
        val ecPayment = paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(ecPaymentId)
        if (ecPayment.status != PaymentEcStatus.Draft)
            throw PaymentApplicationToEcNotInDraftException()

        val paymentExtension = paymentApplicationsToEcPersistence.getPaymentExtension(paymentId)
        if (paymentExtension.ecPaymentId != null)
            throw PaymentApplicationAlreadyTakenException(paymentExtension.ecPaymentId)

        paymentApplicationsToEcPersistence.selectPaymentToEcPayment(paymentIds = setOf(paymentId), ecPaymentId = ecPaymentId)
    }

}
