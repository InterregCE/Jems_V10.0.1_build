package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.service.updatePaymentInstallments.PaymentInstallmentsValidator
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime

class PaymentInstallmentsValidatorTest : UnitTest() {

    companion object {
        private const val COMMON_INPUT_ERROR = "common.error.input.invalid"
        private val currentDate = ZonedDateTime.now().toLocalDate()

        private val installmentUpdate = PaymentPartnerInstallmentUpdate(
            id = 3L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "commentModified",
            isSavePaymentInfo = true,
            savePaymentInfoUserId = 4L,
            savePaymentDate = currentDate,
            isPaymentConfirmed = true,
            paymentConfirmedUserId = null,
            paymentConfirmedDate = null
        )
        private val installmentNew = PaymentPartnerInstallmentUpdate(
            id = 0L,
            amountPaid = BigDecimal.ONE,
            paymentDate = currentDate.minusDays(1),
            comment = null
        )
    }

    lateinit var generalValidator: GeneralValidatorService

    lateinit var validator: PaymentInstallmentsValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        generalValidator = GeneralValidatorDefaultImpl()
        validator = PaymentInstallmentsValidator(generalValidator)
    }

    @Test
    fun `should succeed deletion check on correct data`() {
        assertDoesNotThrow {
            validator.validateInstallmentDeletion(listOf( PaymentPartnerInstallment(
                id = 4L,
                fundId = 65L,
                amountPaid = BigDecimal.TEN,
                paymentDate = currentDate,
                comment = "comment"
            )))
        }
    }

    @Test
    fun `should throw InputValidationException at deletion of already checked`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateInstallmentDeletion(listOf(PaymentPartnerInstallment(
                id = 4L,
                fundId = 65L,
                amountPaid = BigDecimal.TEN,
                paymentDate = currentDate,
                isSavePaymentInfo = true
            )))
        }
        assertEquals(PaymentInstallmentsValidator.PAYMENT_PARTNER_INSTALLMENT_DELETION_ERROR_KEY, ex.i18nKey)

    }

    @Test
    fun `should throw InputValidationException if text too long`() {
        val ex = assertThrows<AppInputValidationException> {
            validator.validateInstallmentValues(
                listOf(installmentNew, installmentUpdate.copy(comment = "t".repeat(501)))
            )
        }
        assertEquals(COMMON_INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should succeed if max installments reached`() {
        assertDoesNotThrow {
            validator.validateMaxInstallments(
                listOf(installmentNew, installmentNew, installmentNew, installmentNew, installmentNew)
            )
        }
    }

    @Test
    fun `should throw I18nValidationException if max+1 installments reached`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateMaxInstallments(
                listOf(installmentNew, installmentNew, installmentNew, installmentNew, installmentNew, installmentNew)
            )
        }
        assertEquals(PaymentInstallmentsValidator.PAYMENT_PARTNER_INSTALLMENT_MAX_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException if checkbox states are wrong`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateCheckboxStates(
                listOf(PaymentPartnerInstallmentUpdate(
                    id = 3L,
                    amountPaid = BigDecimal.TEN,
                    paymentDate = currentDate,
                    comment = "comment",
                    isSavePaymentInfo = false,
                    isPaymentConfirmed = true
                ))
            )
        }
        assertEquals(PaymentInstallmentsValidator.PAYMENT_PARTNER_INSTALLMENT_SAVE_ERROR_KEY, ex.i18nKey)
    }
}
