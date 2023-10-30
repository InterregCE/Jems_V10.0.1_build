package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentStatus
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
        if (isInstallmentAuthorized(saved) && saved?.paymentAuthorized == false) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_AUTHORIZE_ERROR_KEY)
        }

        if (update.hasSettlements() && saved?.paymentConfirmed == false) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_SETTLEMENTS_ERROR_KEY)
        }

        validator.throwIfAnyIsInvalid(
            *validateDetails(update, saved).toTypedArray()
        )
        validateContributionSource(update)
    }

    private fun validateDetails(update: AdvancePaymentUpdate, saved: AdvancePaymentDetail?): List<Map<String, I18nMessage>> {
        val feedback = mutableListOf<Map<String, I18nMessage>>()
        feedback.add(validator.notNullOrZero(update.projectId, "projectId"))
        feedback.add(validator.notNullOrZero(update.partnerId, "partnerId"))
        if (saved?.paymentConfirmed == true) {
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

    fun validateStatus(status: AdvancePaymentStatus, saved: AdvancePaymentDetail) {
        if (status == AdvancePaymentStatus.CONFIRMED) {
            validator.throwIfAnyIsInvalid(validator.notNull(saved.paymentDate, "paymentDate"))
        }

        if (saved.paymentAuthorized == false && status == AdvancePaymentStatus.CONFIRMED) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_SAVE_ERROR_KEY)
        }

        if (saved.paymentConfirmed == true && status == AdvancePaymentStatus.DRAFT) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_AUTHORIZE_ERROR_KEY)
        }

        if (status == AdvancePaymentStatus.AUTHORIZED && saved.hasSettlements()) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_CONFIRMATION_ERROR_KEY)
        }
    }

    fun validateContributionSource(it: AdvancePaymentUpdate) {
        if (isNotSet(it.programmeFundId) &&
            isNotSet(it.partnerContributionId) &&
            isNotSet(it.partnerContributionSpfId)
        ) {
            throw I18nValidationException(i18nKey = PAYMENT_ADVANCE_NO_SOURCE_ERROR_KEY)
        }
    }

    private fun isNotSet(id: Long?): Boolean =
        id == null || id <= 0

    private fun AdvancePaymentDetail?.hasSettlements() =
        this != null && this.paymentSettlements.isNotEmpty()

    private fun AdvancePaymentUpdate.hasSettlements() =
        this.paymentSettlements.isNotEmpty()
}

