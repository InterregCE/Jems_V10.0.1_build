package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments.PaymentInstallmentsValidator
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
        private const val COMMON_ERROR_REQUIRED = "common.error.field.required"
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
            paymentConfirmedDate = null,
            correctionId = null,
        )
        private val installmentSaved = PaymentPartnerInstallment(
            id = 3L,
            fundId = 1L,
            lumpSumId = 2L,
            orderNr = 8,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "comment",
            isSavePaymentInfo = true,
            isPaymentConfirmed = true,
            correction = null,
        )
        private val installmentNew = PaymentPartnerInstallmentUpdate(
            id = 0L,
            amountPaid = BigDecimal.ONE,
            paymentDate = currentDate.minusDays(1),
            comment = null,
            correctionId = null,
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
            validator.validateInstallmentDeletion(
                listOf(
                    PaymentPartnerInstallment(
                        id = 4L,
                        fundId = 65L,
                        lumpSumId = 6458L,
                        orderNr = 9,
                        amountPaid = BigDecimal.TEN,
                        paymentDate = currentDate,
                        comment = "comment",
                        correction = null,
                    )
                )
            )
        }
    }

    @Test
    fun `should throw InputValidationException at deletion of already checked`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateInstallmentDeletion(
                listOf(
                    PaymentPartnerInstallment(
                        id = 4L,
                        fundId = 65L,
                        lumpSumId = 6459L,
                        orderNr = 10,
                        amountPaid = BigDecimal.TEN,
                        paymentDate = currentDate,
                        isSavePaymentInfo = true,
                        correction = null,
                    )
                )
            )
        }
        assertEquals(PaymentInstallmentsValidator.PAYMENT_PARTNER_INSTALLMENT_DELETION_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw InputValidationException if text too long`() {
        val ex = assertThrows<AppInputValidationException> {
            validator.validateInstallments(
                listOf(installmentNew, installmentUpdate.copy(comment = "t".repeat(501))),
                emptyList(),
                emptyList(),
                1
            )
        }
        assertEquals(COMMON_INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should succeed if paymentDate is empty on unconfirmed payment`() {
        assertDoesNotThrow {
            validator.validateInstallments(
                listOf(
                    installmentNew, installmentUpdate.copy(
                        isPaymentConfirmed = false,
                        paymentDate = null
                    )
                ),
                emptyList(),
                emptyList(),
                1
            )
        }
    }

    @Test
    fun `should throw InputValidationException on confirmed payment if paymentDate is empty`() {
        val ex = assertThrows<AppInputValidationException> {
            validator.validateInstallments(
                listOf(
                    installmentNew, installmentUpdate.copy(
                        isPaymentConfirmed = true,
                        paymentDate = null
                    )
                ),
                emptyList(),
                emptyList(),
                1
            )
        }
        assertEquals(COMMON_INPUT_ERROR, ex.i18nMessage.i18nKey)
        assertEquals(COMMON_ERROR_REQUIRED, ex.formErrors["paymentDate-1-1"]?.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException if both checkboxes removed at same time`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateInstallments(
                listOf(installmentUpdate.copy(isSavePaymentInfo = false, isPaymentConfirmed = false)),
                listOf(installmentSaved),
                emptyList(),
                1
            )
        }
        assertEquals(PaymentInstallmentsValidator.PAYMENT_PARTNER_INSTALLMENT_DELETION_ERROR_KEY, ex.i18nKey)
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
                listOf(
                    PaymentPartnerInstallmentUpdate(
                        id = 3L,
                        amountPaid = BigDecimal.TEN,
                        paymentDate = currentDate,
                        comment = "comment",
                        isSavePaymentInfo = false,
                        isPaymentConfirmed = true,
                        correctionId = null,
                    )
                )
            )
        }
        assertEquals(PaymentInstallmentsValidator.PAYMENT_PARTNER_INSTALLMENT_SAVE_ERROR_KEY, ex.i18nKey)
    }
}
