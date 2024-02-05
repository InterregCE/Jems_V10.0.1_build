package io.cloudflight.jems.server.payments.service.account.corrections.selectCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.corrections.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class SelectCorrectionToPaymentAccountTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 541L
        private const val PAYMENT_ACCOUNT_ID = 20L
        private const val FUND_ID = 21L

        val paymentAccount = mockk<PaymentAccount>()

        private val paymentToEcExtensionModel = PaymentAccountCorrectionExtension(
            correctionId = CORRECTION_ID,
            paymentAccountId = null,
            paymentAccountStatus = PaymentAccountStatus.DRAFT,
            auditControlStatus = AuditControlStatus.Ongoing,
            comment = "Comment",
            fundAmount = BigDecimal.valueOf(25.80),
            publicContribution = BigDecimal.valueOf(35.00),
            correctedPublicContribution = BigDecimal.valueOf(36.20),
            autoPublicContribution = BigDecimal.valueOf(15.00),
            correctedAutoPublicContribution = BigDecimal.valueOf(16.00),
            privateContribution = BigDecimal.valueOf(45.00),
            correctedPrivateContribution = BigDecimal.valueOf(46.20),
            correctedFundAmount = BigDecimal.valueOf(75.00),
        )

    }

    @MockK
    lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @MockK
    lateinit var correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence

    @InjectMockKs
    lateinit var service: SelectCorrectionToPaymentAccount

    @Test
    fun selectCorrection() {
        every { paymentAccount.status } returns PaymentAccountStatus.DRAFT
        every { paymentAccount.fund.id } returns FUND_ID
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount
        every { correctionLinkingPersistence.getCorrectionExtension(CORRECTION_ID) } returns paymentToEcExtensionModel
        every { correctionLinkingPersistence.getCorrectionIdsAvailableForPaymentAccounts(FUND_ID) } returns setOf(
            CORRECTION_ID
        )

        every {
            correctionLinkingPersistence.selectCorrectionToPaymentAccount(
                correctionIds = setOf(
                    CORRECTION_ID
                ), paymentAccountId = PAYMENT_ACCOUNT_ID
            )
        } returns Unit

        service.selectCorrection(correctionId = CORRECTION_ID, paymentAccountId = PAYMENT_ACCOUNT_ID)
        verify(exactly = 1) {
            correctionLinkingPersistence.selectCorrectionToPaymentAccount(
                correctionIds = setOf(
                    CORRECTION_ID
                ), paymentAccountId = PAYMENT_ACCOUNT_ID
            )
        }
    }

    @Test
    fun `selectCorrection - ec payment closed exception`() {
        every { paymentAccount.status } returns PaymentAccountStatus.FINISHED
        every { paymentAccount.fund.id } returns FUND_ID

        assertThrows<PaymentAccountNotInDraftException> {
            service.selectCorrection(
                correctionId = CORRECTION_ID,
                paymentAccountId = PAYMENT_ACCOUNT_ID
            )
        }
    }

    @Test
    fun `selectCorrection - correction not available to be selected exception`() {
        every { paymentAccount.status } returns PaymentAccountStatus.DRAFT
        every { paymentAccount.fund.id } returns FUND_ID
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount
        every { correctionLinkingPersistence.getCorrectionExtension(CORRECTION_ID).paymentAccountId  } returns null
        every { correctionLinkingPersistence.getCorrectionIdsAvailableForPaymentAccounts(FUND_ID) } returns setOf(
            CORRECTION_ID + 1L
        )

        every {
            correctionLinkingPersistence.selectCorrectionToPaymentAccount(
                correctionIds = setOf(
                    CORRECTION_ID
                ), paymentAccountId = PAYMENT_ACCOUNT_ID
            )
        } returns Unit

        assertThrows<CorrectionNotAvailableForSelectionException> {
            service.selectCorrection(
                correctionId = CORRECTION_ID,
                paymentAccountId = PAYMENT_ACCOUNT_ID
            )
        }
    }

}
