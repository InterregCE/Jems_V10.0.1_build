package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.service.AdvancePaymentValidator.Companion.PAYMENT_ADVANCE_AUTHORIZE_ERROR_KEY
import io.cloudflight.jems.server.payments.service.AdvancePaymentValidator.Companion.PAYMENT_ADVANCE_DELETION_ERROR_KEY
import io.cloudflight.jems.server.payments.service.AdvancePaymentValidator.Companion.PAYMENT_ADVANCE_SAVE_ERROR_KEY
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentUpdate
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime

class AdvancePaymentValidatorTest : UnitTest() {

    companion object {
        private const val COMMON_INPUT_ERROR = "common.error.input.invalid"
        private val currentDate = ZonedDateTime.now().toLocalDate()
        private const val paymentId = 1L
        private const val projectId = 2L
        private const val partnerId = 3L
        private const val userId = 4L

        private val fund = ProgrammeFund(id = 5L, selected = true)

        private val advancePaymentDetail = AdvancePaymentDetail(
            id = paymentId,
            projectId = projectId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            partnerId = partnerId,
            partnerType = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentAuthorizedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentConfirmedDate = currentDate.minusDays(2)
        )
        private val advancePaymentUpdate = AdvancePaymentUpdate(
            id = paymentId,
            projectId = projectId,
            partnerId = partnerId,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentAuthorizedUserId = userId,
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUserId = userId,
            paymentConfirmedDate = currentDate.minusDays(2)
        )
    }

    lateinit var generalValidator: GeneralValidatorService

    lateinit var validator: AdvancePaymentValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        generalValidator = GeneralValidatorDefaultImpl()
        validator = AdvancePaymentValidator(generalValidator)
    }

    @Test
    fun `should succeed deletion check on correct data`() {
        assertDoesNotThrow {
            validator.validateDeletion(advancePaymentDetail.copy(
                paymentAuthorized = false,
                paymentConfirmed = false
            ))
        }
    }

    @Test
    fun `should throw InputValidationException at deletion of already checked`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateDeletion(advancePaymentDetail.copy(
                paymentAuthorized = true,
                paymentConfirmed = false
            ))
        }
        assertEquals(PAYMENT_ADVANCE_DELETION_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw InputValidationException if text too long`() {
        val ex = assertThrows<AppInputValidationException> {
            validator.validateDetail(
                update = advancePaymentUpdate.copy(comment = "t".repeat(501)),
                saved = null
            )
        }
        assertEquals(COMMON_INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should succeed if paymentDate is empty on unconfirmed payment`() {
        assertDoesNotThrow {
            validator.validateDetail(
                advancePaymentUpdate.copy(
                    paymentConfirmed = false,
                    dateOfPayment = null
                ), null
            )
        }
    }

    @Test
    fun `should throw InputValidationException on confirmed payment if paymentDate is empty`() {
        val ex = assertThrows<AppInputValidationException> {
            validator.validateDetail(
                advancePaymentUpdate.copy(
                    paymentConfirmed = true,
                    dateOfPayment = null
                ), null
            )
        }
        assertEquals(COMMON_INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException if both checkboxes removed at same time`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateDetail(
                advancePaymentUpdate.copy(paymentAuthorized = false, paymentConfirmed = false),
                advancePaymentDetail
            )
        }
        assertEquals(PAYMENT_ADVANCE_AUTHORIZE_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException if checkbox states are wrong`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateCheckboxStates(advancePaymentUpdate.copy(
                paymentAuthorized = false,
                paymentConfirmed = true
            ))
        }
        assertEquals(PAYMENT_ADVANCE_SAVE_ERROR_KEY, ex.i18nKey)
    }
}
