package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.reOpenFinalizedEcPaymentApplication

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcReOpened
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc.EcPaymentApplicationSameFundAccountingYearExistsException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReOpenFinalizedEcPaymentApplication(
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
): ReOpenFinalizedEcPaymentApplicationInteractor {

    @Transactional
    @CanUpdatePaymentApplicationsToEc
    @ExceptionWrapper(ReOpenFinalizedEcPaymentApplicationException::class)
    override fun reOpen(ecPaymentApplicationId: Long): PaymentApplicationToEcDetail {

        val paymentApplicationToReOpen = paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(ecPaymentApplicationId)
        validatePaymentApplicationIsFinished(paymentApplicationToReOpen.status)
        validateFundAccountingYearPair(paymentApplicationToReOpen.paymentApplicationToEcSummary)

        return paymentApplicationsToEcPersistence.updatePaymentApplicationToEcStatus(ecPaymentApplicationId, PaymentEcStatus.Draft).also {
            auditPublisher.publishEvent(paymentApplicationToEcReOpened(context = this, it))
        }
    }


    private fun validatePaymentApplicationIsFinished(paymentApplicationStatus: PaymentEcStatus) {
        if (paymentApplicationStatus != PaymentEcStatus.Finished)
            throw EcPaymentApplicationNotFinishedException()
    }

    fun validateFundAccountingYearPair(paymentApplicationToEcSummary: PaymentApplicationToEcSummary) {
        val existingEcPaymentApplication = paymentApplicationsToEcPersistence.existsDraftByFundAndAccountingYear(
                paymentApplicationToEcSummary.programmeFund.id,
                paymentApplicationToEcSummary.accountingYear.id
            )
        if (existingEcPaymentApplication) {
            throw EcPaymentApplicationSameFundAccountingYearExistsException()
        }
    }
}
