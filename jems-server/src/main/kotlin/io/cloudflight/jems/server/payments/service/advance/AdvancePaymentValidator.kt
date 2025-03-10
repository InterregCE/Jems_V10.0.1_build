package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import org.springframework.stereotype.Service

@Service
class AdvancePaymentValidator(private val validator: GeneralValidatorService) {

    companion object {
        const val PAYMENT_ADVANCE_DELETION_ERROR_KEY = "payment.advance.deletion.not.possible"
        const val PAYMENT_ADVANCE_SAVE_ERROR_KEY = "payment.advance.save.invalid.fields"
        const val PAYMENT_ADVANCE_NO_SOURCE_ERROR_KEY = "payment.advance.save.without.contribution.not.possible"
        const val PAYMENT_ADVANCE_AUTHORIZE_ERROR_KEY = "payment.advance.save.unauthorize.not.possible"
        const val PAYMENT_ADVANCE_CONFIRMATION_ERROR_KEY = "payment.advance.save.confirmation.not.possible"
        const val PAYMENT_ADVANCE_SETTLEMENTS_ERROR_KEY = "payment.advance.save.settlements.not.possible"
    }

    fun validateDetail(update: AdvancePaymentUpdate, saved: AdvancePaymentDetail?) {

        validateCheckboxStates(update)

        if (saved != null && saved.projectId != update.projectId)
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_SAVE_ERROR_KEY)

        if (isInstallmentAuthorized(saved) && update.paymentAuthorized == false) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_AUTHORIZE_ERROR_KEY)
        }

        if (paymentConfirmationRemoved(update, saved) && update.hasSettlements()) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_CONFIRMATION_ERROR_KEY)
        }

        if (update.hasSettlements() && update.paymentConfirmed == false && saved?.paymentConfirmed == false) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_SETTLEMENTS_ERROR_KEY)
        }

        validator.throwIfAnyIsInvalid(
            *validateDetails(update).toTypedArray()
        )
        validateContributionSource(update)
    }


    fun paymentConfirmationRemoved(update: AdvancePaymentUpdate, saved: AdvancePaymentDetail?) =
        saved?.paymentConfirmed == true && update.paymentConfirmed == false


    private fun validateDetails(update: AdvancePaymentUpdate): List<Map<String, I18nMessage>> {
        val feedback = mutableListOf<Map<String, I18nMessage>>()
        feedback.add(validator.notNull(update.projectId, "projectId"))
        feedback.add(validator.notNull(update.partnerId, "partnerId"))
        if (update.paymentConfirmed == true) {
            feedback.add(validator.notNull(update.paymentDate, "paymentDate"))
        }
        feedback.add(validator.maxLength(update.comment, 500, "comment"))

        update.paymentSettlements.forEach { settlement ->
            feedback.add(validator.notNull(settlement.amountSettled, "amountSettled"))
            feedback.add(validator.notNull(settlement.settlementDate, "settlementDate"))
            feedback.add(validator.maxLength(settlement.comment, 500, "comment"))
        }
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

    fun validateContributionSource(it: AdvancePaymentUpdate) {
        if (isNotSet(it.programmeFundId)
            && isNotSet(it.partnerContributionId)
            && isNotSet(it.partnerContributionSpfId)) {
                throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_NO_SOURCE_ERROR_KEY)
        }
    }
    private fun isNotSet(id: Long?): Boolean =
        id == null || id <= 0

    private fun AdvancePaymentUpdate.hasSettlements() =
        this.paymentSettlements.isNotEmpty()
}

