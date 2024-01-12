package io.cloudflight.jems.server.payments.repository.account

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.accountingYears.repository.toEntity
import io.cloudflight.jems.server.payments.entity.PaymentAccountEntity
import io.cloudflight.jems.server.payments.service.account.FUND_ID
import io.cloudflight.jems.server.payments.service.account.PAYMENT_ACCOUNT_ID
import io.cloudflight.jems.server.payments.service.account.accountingYear
import io.cloudflight.jems.server.payments.service.account.expectedPaymentAccountUpdate
import io.cloudflight.jems.server.payments.service.account.paymentAccount
import io.cloudflight.jems.server.payments.service.account.paymentAccountEntity
import io.cloudflight.jems.server.payments.service.account.paymentAccountUpdate
import io.cloudflight.jems.server.payments.service.account.programmeFund
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.fund.toEntity
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PaymentAccountPersistenceProviderTest: UnitTest() {

    @MockK
    lateinit var repository: PaymentAccountRepository

    @MockK
    lateinit var accountingYearRepository: AccountingYearRepository

    @MockK
    lateinit var programmeFundRepository: ProgrammeFundRepository

    @InjectMockKs
    lateinit var persistenceProvider: PaymentAccountPersistenceProvider

    @BeforeEach
    fun setup() {
        clearMocks(repository, accountingYearRepository)
    }

    @Test
    fun deletePaymentAccountsByFunds() {
        every { repository.deleteAllByProgrammeFundIdIn(any())} returnsArgument 0

        persistenceProvider.deletePaymentAccountsByFunds(setOf(1L))
        verify(exactly = 1){ repository.deleteAllByProgrammeFundIdIn(any()) }
    }

    @Test
    fun getById() {
        every {
            repository.getById(
                PAYMENT_ACCOUNT_ID
            )
        } returns paymentAccountEntity()

        assertThat(persistenceProvider.getByPaymentAccountId(PAYMENT_ACCOUNT_ID)).isEqualTo(
            paymentAccount
        )
    }

    @Test
    fun getAllAccounts() {
        every { repository.findAll() } returns listOf(paymentAccountEntity())

        assertThat(persistenceProvider.getAllAccounts()).isEqualTo(listOf(paymentAccount))
    }

    @Test
    fun updatePaymentAccount() {
        val paymentAccountEntityTmp = paymentAccountEntity()
        every { repository.getById(PAYMENT_ACCOUNT_ID) } returns paymentAccountEntityTmp

        assertThat(persistenceProvider.updatePaymentAccount(PAYMENT_ACCOUNT_ID, paymentAccountUpdate)).isEqualTo(expectedPaymentAccountUpdate)
    }

    @Test
    fun persistPaymentAccountsByFunds() {
        every { accountingYearRepository.findAllByOrderByYear() } returns listOf(accountingYear.toEntity())
        every { programmeFundRepository.findAllById(any()) } returns listOf(programmeFund.toEntity())

        val paymentAccountSlot = slot<List<PaymentAccountEntity>>()
        every { repository.saveAll(capture(paymentAccountSlot)) } returnsArgument 0

        persistenceProvider.persistPaymentAccountsByFunds(setOf(FUND_ID))

        verify(exactly = 1){ repository.saveAll(paymentAccountSlot.captured) }
    }

}
