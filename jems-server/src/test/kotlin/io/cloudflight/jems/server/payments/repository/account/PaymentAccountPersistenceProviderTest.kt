package io.cloudflight.jems.server.payments.repository.account

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.accountingYears.repository.toEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
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
import io.mockk.mockk
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

    @MockK
    lateinit var  fileRepository: JemsSystemFileService

    @MockK
    lateinit var reportFileRepository: JemsFileMetadataRepository

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
            repository.getReferenceById(
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

        assertThat(persistenceProvider.getAllAccounts()).containsExactly(paymentAccount)
    }

    @Test
    fun findByFundAndYear() {
        every { repository.findByProgrammeFundIdAndAccountingYearId(24L, 54L) } returns paymentAccountEntity()
        assertThat(persistenceProvider.findByFundAndYear(24L, 54L)).isEqualTo(paymentAccount)
    }

    @Test
    fun updatePaymentAccount() {
        val paymentAccountEntityTmp = paymentAccountEntity()
        every { repository.getReferenceById(PAYMENT_ACCOUNT_ID) } returns paymentAccountEntityTmp

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

    @Test
    fun finalizePaymentAccount() {
        every { repository.getReferenceById(PAYMENT_ACCOUNT_ID) } returns paymentAccountEntity()

        assertThat(persistenceProvider.finalizePaymentAccount(PAYMENT_ACCOUNT_ID)).isEqualTo(PaymentAccountStatus.FINISHED)
    }

    @Test
    fun reOpenPaymentAccount() {
        every { repository.getReferenceById(PAYMENT_ACCOUNT_ID) } returns paymentAccountEntity()

        assertThat(persistenceProvider.reOpenPaymentAccount(PAYMENT_ACCOUNT_ID)).isEqualTo(PaymentAccountStatus.DRAFT)
    }

    @Test
    fun deletePaymentAccountAttachment() {
        val file = mockk<JemsFileMetadataEntity>()
        every { fileRepository.delete(file) } answers { }
        every { reportFileRepository.findByTypeAndId(JemsFileType.PaymentAccountAttachment, 16L) } returns file
        persistenceProvider.deletePaymentAccountAttachment(16L)
        verify(exactly = 1) { fileRepository.delete(file) }
    }
}
