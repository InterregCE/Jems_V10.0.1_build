package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator.Companion.PAYMENT_ADVANCE_AUTHORIZE_ERROR_KEY
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator.Companion.PAYMENT_ADVANCE_DELETION_ERROR_KEY
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator.Companion.PAYMENT_ADVANCE_NO_SOURCE_ERROR_KEY
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator.Companion.PAYMENT_ADVANCE_SAVE_ERROR_KEY
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
        private const val version = "1.0"

        private val fund = ProgrammeFund(id = 5L, selected = true)

        private val paymentSettlement = AdvancePaymentSettlement(
            id = 1L,
            number = 1,
            amountSettled = BigDecimal(5),
            settlementDate = currentDate.minusDays(1),
            comment = "half"
        )

        private val advancePaymentDetail = AdvancePaymentDetail(
            id = paymentId,
            projectId = projectId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            projectVersion = version,
            partnerId = partnerId,
            partnerType = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentAuthorizedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentConfirmedDate = currentDate.minusDays(2),
            paymentSettlements = listOf(paymentSettlement)
        )
        private val advancePaymentUpdate = AdvancePaymentUpdate(
            id = paymentId,
            projectId = projectId,
            partnerId = partnerId,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentAuthorizedUserId = userId,
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUserId = userId,
            paymentConfirmedDate = currentDate.minusDays(2),
            paymentSettlements = listOf(paymentSettlement)
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
            validator.validateDeletion(
                advancePaymentDetail.copy(
                paymentAuthorized = false,
                paymentConfirmed = false
            ))
        }
    }

    @Test
    fun `should throw InputValidationException at deletion of already checked`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateDeletion(
                advancePaymentDetail.copy(
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
                    programmeFundId = 1L,
                    paymentConfirmed = false,
                    paymentDate = null
                ), null
            )
        }
    }

    @Test
    fun `should throw InputValidationException if no project is set`() {
        val ex = assertThrows<AppInputValidationException> {
            validator.validateDetail(
                advancePaymentUpdate.copy(
                    projectId = 0,
                    paymentConfirmed = true,
                    paymentDate = null
                ), null
            )
        }
        assertEquals(COMMON_INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should throw InputValidationException if no source of contribution is set`() {
        val ex = assertThrows<I18nValidationException> {
            validator.validateDetail(
                advancePaymentUpdate.copy(
                    paymentConfirmed = false,
                    paymentDate = null
                ), null
            )
        }
        assertEquals(PAYMENT_ADVANCE_NO_SOURCE_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw InputValidationException on confirmed payment if paymentDate is empty`() {
        val ex = assertThrows<AppInputValidationException> {
            validator.validateDetail(
                advancePaymentUpdate.copy(
                    paymentConfirmed = true,
                    paymentDate = null
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
            validator.validateCheckboxStates(
                advancePaymentUpdate.copy(
                paymentAuthorized = false,
                paymentConfirmed = true
            ))
        }
        assertEquals(PAYMENT_ADVANCE_SAVE_ERROR_KEY, ex.i18nKey)
    }
}
