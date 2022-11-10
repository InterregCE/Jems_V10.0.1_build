package io.cloudflight.jems.server.payments.service.updatePaymentInstallments

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallmentUpdate
import org.springframework.stereotype.Service

@Service
class PaymentInstallmentsValidator(private val validator: GeneralValidatorService) {

    companion object {
        const val PAYMENT_PARTNER_INSTALLMENT_MAX = 5
        const val PAYMENT_PARTNER_INSTALLMENT_DELETION_ERROR_KEY = "payment.partner.installment.deletion.not.possible"
        const val PAYMENT_PARTNER_INSTALLMENT_SAVE_ERROR_KEY = "payment.partner.installment.save.invalid.fields"
        const val PAYMENT_PARTNER_INSTALLMENT_MAX_ERROR_KEY = "payment.partner.installment.save.not.possible"
    }

    fun validateInstallments(
        installments: Collection<PaymentPartnerInstallmentUpdate>,
        savedInstallments: Collection<PaymentPartnerInstallment>,
        deleteInstallments: Collection<PaymentPartnerInstallment>
    ) {
        validateInstallmentDeletion(deleteInstallments)
        validateMaxInstallments(installments)
        validateCheckboxStates(installments)
        validator.throwIfAnyIsInvalid(
            *validateInstallmentValues(installments, savedInstallments).toTypedArray()
        )
    }

    fun validateInstallmentValues(
        installments: Collection<PaymentPartnerInstallmentUpdate>,
        savedInstallments: Collection<PaymentPartnerInstallment>
    ): List<Map<String, I18nMessage>> {
        val feedback = mutableListOf<Map<String, I18nMessage>>()
        installments.forEach { installment ->
            val savedInstallment = savedInstallments.find { installment.id == it.id }
            if (isInstallmentAuthorized(savedInstallment) && installment.isSavePaymentInfo == false) {
                throw I18nValidationException(i18nKey = PAYMENT_PARTNER_INSTALLMENT_DELETION_ERROR_KEY)
            }
            if (installment.isPaymentConfirmed == true) {
                feedback.add(validator.notNull(installment.paymentDate, "paymentDate"))
            }
            feedback.add(validator.maxLength(installment.comment, 500, "comment"))
        }
        return feedback
    }

    private fun isInstallmentAuthorized(installment: PaymentPartnerInstallment?): Boolean {
        return installment != null && installment.isSavePaymentInfo == true && installment.isPaymentConfirmed == true
    }

    fun validateInstallmentDeletion(
        deleteInstallments: Collection<PaymentPartnerInstallment>
    ) {
        if (deleteInstallments.any { it.isSavePaymentInfo != null && it.isSavePaymentInfo }) {
            throw I18nValidationException(i18nKey = PAYMENT_PARTNER_INSTALLMENT_DELETION_ERROR_KEY)
        }
    }

    fun validateMaxInstallments(
        saveInstallments: Collection<PaymentPartnerInstallmentUpdate>
    ) {
        if (saveInstallments.count() > PAYMENT_PARTNER_INSTALLMENT_MAX) {
            throw I18nValidationException(i18nKey = PAYMENT_PARTNER_INSTALLMENT_MAX_ERROR_KEY)
        }
    }

    // invalid on isPaymentConfirmed before isSavePaymentInfo
    fun validateCheckboxStates(
        saveInstallments: Collection<PaymentPartnerInstallmentUpdate>
    ) {
        if (saveInstallments.any { it.isSavePaymentInfo != true && it.isPaymentConfirmed == true } ) {
            throw I18nValidationException(i18nKey = PAYMENT_PARTNER_INSTALLMENT_SAVE_ERROR_KEY)
        }
    }
}

