package io.cloudflight.jems.server.payments.service.ecPayment.updatePaymentApplicationToEcDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePaymentApplicationToEcDetail(
    private val paymentApplicationToEcPersistence: PaymentApplicationToEcPersistence,
    private val validator: GeneralValidatorService
) : UpdatePaymentApplicationToEcDetailInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(UpdatePaymentApplicationToEcDetailException::class)
    override fun updatePaymentApplicationToEc(paymentApplicationId: Long, paymentApplicationToEcUpdate: PaymentApplicationToEcSummaryUpdate
    ): PaymentApplicationToEcDetail {

        validateLengthOfFields(paymentApplicationToEcUpdate)
        return paymentApplicationToEcPersistence.updatePaymentApplicationToEc(paymentApplicationId, paymentApplicationToEcUpdate)
    }

    private fun validateLengthOfFields(paymentApplicationToEcUpdate: PaymentApplicationToEcSummaryUpdate) {
        validator.throwIfAnyIsInvalid(
            validator.maxLength(paymentApplicationToEcUpdate.nationalReference, 50, "nationalReference"),
            validator.maxLength(paymentApplicationToEcUpdate.sfcNumber, 50, "sfcNumber"),
            validator.maxLength(paymentApplicationToEcUpdate.comment, 5000, "comment")
        )
    }

}
