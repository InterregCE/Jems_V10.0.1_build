package io.cloudflight.jems.server.payments.repository.account.finance.correction.reconciliation

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.repository.account.PaymentAccountRepository
import io.cloudflight.jems.server.payments.repository.account.correction.PAYMENT_ACCOUNT_ID
import io.cloudflight.jems.server.payments.repository.account.correction.PRIORITY_AXIS_ID
import io.cloudflight.jems.server.payments.repository.account.correction.expectedAccountReconciliationUpdate
import io.cloudflight.jems.server.payments.repository.account.correction.paymentAccountReconciliation
import io.cloudflight.jems.server.payments.repository.account.correction.paymentReconciliationList
import io.cloudflight.jems.server.payments.repository.account.correction.reconciliationEntityList
import io.cloudflight.jems.server.payments.repository.account.correction.reconciliationUpdate
import io.cloudflight.jems.server.payments.repository.account.reconciliation.PaymentAccountReconciliationPersistenceProvider
import io.cloudflight.jems.server.payments.repository.account.reconciliation.PaymentAccountReconciliationRepository
import io.cloudflight.jems.server.payments.service.account.paymentAccountEntity
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProviderTest.Companion.programmePriority
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class PaymentAccountReconciliationPersistenceProviderTest : UnitTest() {

    @MockK
    lateinit var accountReconciliationRepository: PaymentAccountReconciliationRepository

    @MockK
    lateinit var paymentAccountRepository: PaymentAccountRepository

    @MockK
    lateinit var programmePriorityRepository: ProgrammePriorityRepository

    @InjectMockKs
    lateinit var persistence: PaymentAccountReconciliationPersistenceProvider

    @Test
    fun getByPaymentAccountId() {
        every { accountReconciliationRepository.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns reconciliationEntityList()

        assertThat(persistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID)).isEqualTo(paymentReconciliationList)
    }

    @Test
    fun `updateReconciliation - create new row`() {
        every {
            accountReconciliationRepository.getByPaymentAccountIdAndPriorityAxisId(
                PAYMENT_ACCOUNT_ID,
                PRIORITY_AXIS_ID,
            )
        } returns Optional.empty()

        every { paymentAccountRepository.getById(PAYMENT_ACCOUNT_ID) } returns paymentAccountEntity()
        every { programmePriorityRepository.getById(PRIORITY_AXIS_ID) } returns programmePriority
        every { accountReconciliationRepository.save(any()) } returnsArgument 0

        assertThat(persistence.updateReconciliation(PAYMENT_ACCOUNT_ID, reconciliationUpdate)).isEqualTo(
            expectedAccountReconciliationUpdate(0L)
        )
        verify(exactly = 1) { accountReconciliationRepository.save(any()) }
    }

    @Test
    fun `updateReconciliation - existing entry`() {
        every {
            accountReconciliationRepository.getByPaymentAccountIdAndPriorityAxisId(
                PAYMENT_ACCOUNT_ID,
                PRIORITY_AXIS_ID,
            )
        } returns Optional.of(paymentAccountReconciliation())

        every { paymentAccountRepository.getById(PAYMENT_ACCOUNT_ID) } returns paymentAccountEntity()
        every { programmePriorityRepository.getById(PRIORITY_AXIS_ID) } returns programmePriority

        assertThat(persistence.updateReconciliation(PAYMENT_ACCOUNT_ID, reconciliationUpdate).totalComment).isEqualTo(
            "Updated comment"
        )
    }
}
