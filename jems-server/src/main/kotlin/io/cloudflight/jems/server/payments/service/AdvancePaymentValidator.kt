package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentUpdate
import org.springframework.stereotype.Service

@Service
class AdvancePaymentValidator(private val validator: GeneralValidatorService) {

    companion object {
        const val PAYMENT_ADVANCE_DELETION_ERROR_KEY = "payment.advance.deletion.not.possible"
        const val PAYMENT_ADVANCE_SAVE_ERROR_KEY = "payment.advance.save.invalid.fields"
        const val PAYMENT_ADVANCE_AUTHORIZE_ERROR_KEY = "payment.advance.save.unauthorize.not.possible"
    }

    fun validateDetail(update: AdvancePaymentUpdate, saved: AdvancePaymentDetail?) {
        validateCheckboxStates(update)
        if (isInstallmentAuthorized(saved) && update.paymentAuthorized == false) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_AUTHORIZE_ERROR_KEY)
        }
        validator.throwIfAnyIsInvalid(
            *validateDetails(update).toTypedArray()
        )
    }

    private fun validateDetails(update: AdvancePaymentUpdate): List<Map<String, I18nMessage>> {
        val feedback = mutableListOf<Map<String, I18nMessage>>()
        if (update.paymentConfirmed == true) {
            feedback.add(validator.notNull(update.dateOfPayment, "dateOfPayment"))
        }
        feedback.add(validator.maxLength(update.comment, 500, "comment"))
        return feedback
    }

    private fun isInstallmentAuthorized(payment: AdvancePaymentDetail?): Boolean {
        return payment != null && payment.paymentAuthorized == true && payment.paymentConfirmed == true
    }

    fun validateDeletion(it: AdvancePaymentDetail) {
        if (it.paymentAuthorized != null && it.paymentAuthorized) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_DELETION_ERROR_KEY)
        }
    }

    fun validateCheckboxStates(it: AdvancePaymentUpdate) {
        if (it.paymentAuthorized != true && it.paymentConfirmed == true) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_SAVE_ERROR_KEY)
        }
    }
}

