package io.cloudflight.jems.server.payments.service.account.finalizePaymentAccount

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.service.account.PAYMENT_ACCOUNT_ID
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.sumUpProperColumns
import io.cloudflight.jems.server.payments.service.account.paymentAccount
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class FinalizePaymentAccountTest : UnitTest() {

    companion object {
        val paymentAccountDraft = paymentAccount.copy(status = PaymentAccountStatus.DRAFT)
        val paymentAccountFinished = paymentAccount.copy(status = PaymentAccountStatus.FINISHED)
    }

    @MockK
    lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @MockK
    lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var paymentAccountCorrectionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence

    @InjectMockKs
    lateinit var service: FinalizePaymentAccount

    @Test
    fun finalizeAccount() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccountDraft
        every {
            ecPaymentPersistence.getDraftIdsByFundAndAccountingYear(
                paymentAccountDraft.fund.id,
                paymentAccountDraft.accountingYear.id
            )
        } returns emptySet()
        every { paymentAccountPersistence.finalizePaymentAccount(PAYMENT_ACCOUNT_ID) } returns PaymentAccountStatus.FINISHED
        val currentOverview = emptyMap<Long?, PaymentAccountAmountSummaryLineTmp>()
        every { correctionLinkingPersistence.calculateOverviewForDraftPaymentAccount(PAYMENT_ACCOUNT_ID) } returns currentOverview
        every {
            correctionLinkingPersistence.saveTotalsWhenFinishingPaymentAccount(
                PAYMENT_ACCOUNT_ID,
                currentOverview.sumUpProperColumns()
            )
        } just runs
        every {
            paymentAccountCorrectionLinkingPersistence.getCorrectionExtensionIdsByPaymentAccountId(
                PAYMENT_ACCOUNT_ID
            )
        } returns listOf(1L, 2L)
        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        assertThat(service.finalizePaymentAccount(PAYMENT_ACCOUNT_ID))
            .isEqualTo(PaymentAccountStatus.FINISHED)

        val accountingYear = paymentAccount.accountingYear
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_ACCOUNT_STATUS_CHANGED,
                description = "Account 13 Fund (11, OTHER) for accounting Year 4: 2024-01-29 - 2024-02-02 changed  status from DRAFT to FINISHED" +
                    " and following items were included: Correction ID 1, Correction ID 2"
            )
        )
    }

    @Test
    fun `finalizeAccount - not in Draft exception`() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccountFinished
        every {
            ecPaymentPersistence.getDraftIdsByFundAndAccountingYear(
                paymentAccountFinished.fund.id,
                paymentAccountFinished.accountingYear.id
            )
        } returns
            emptySet()
        every { paymentAccountPersistence.finalizePaymentAccount(PAYMENT_ACCOUNT_ID) } returns PaymentAccountStatus.DRAFT

        assertThrows<PaymentAccountNotInDraftException> { service.finalizePaymentAccount(PAYMENT_ACCOUNT_ID) }
    }

    @Test
    fun `finalizeAccount - ec payments still in draft exception`() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccountDraft
        every {
            ecPaymentPersistence.getDraftIdsByFundAndAccountingYear(
                paymentAccountDraft.fund.id,
                paymentAccountDraft.accountingYear.id
            )
        } returns setOf(1L)
        every { paymentAccountPersistence.finalizePaymentAccount(PAYMENT_ACCOUNT_ID) } returns PaymentAccountStatus.FINISHED

        assertThrows<EcPaymentsForAccountingYearStillInDraftException> {
            service.finalizePaymentAccount(
                PAYMENT_ACCOUNT_ID
            )
        }
    }

}
