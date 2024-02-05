package io.cloudflight.jems.server.payments.service.account.reOpenPaymentAccount

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.service.account.PAYMENT_ACCOUNT_ID
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.paymentAccount
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class ReOpenPaymentAccountTest: UnitTest() {

    companion object {
        val paymentAccountDraft = paymentAccount.copy(status = PaymentAccountStatus.DRAFT)
        val paymentAccountFinished = paymentAccount.copy(status = PaymentAccountStatus.FINISHED)
    }

    @MockK
    lateinit var  paymentAccountPersistence: PaymentAccountPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var service: ReOpenPaymentAccount

    @Test
    fun reOpenAccount() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccountFinished

        every { paymentAccountPersistence.reOpenPaymentAccount(PAYMENT_ACCOUNT_ID)} returns PaymentAccountStatus.DRAFT

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        assertThat(service.reOpenPaymentAccount(PAYMENT_ACCOUNT_ID))
            .isEqualTo(PaymentAccountStatus.DRAFT)

        val accountingYear = paymentAccountFinished.accountingYear
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_ACCOUNT_STATUS_CHANGED,
                description = "Account 13 Fund (11, OTHER) for accounting Year 4: 2024-01-29 - 2024-02-02 changed  status from FINISHED to DRAFT"
            )
        )
    }

    @Test
    fun `reOpenAccount - not in status Finished exception`() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccountDraft
        every { paymentAccountPersistence.reOpenPaymentAccount(PAYMENT_ACCOUNT_ID)} returns PaymentAccountStatus.DRAFT

        assertThrows<PaymentAccountNotSubmittedException> { service.reOpenPaymentAccount(PAYMENT_ACCOUNT_ID) }
    }

}
