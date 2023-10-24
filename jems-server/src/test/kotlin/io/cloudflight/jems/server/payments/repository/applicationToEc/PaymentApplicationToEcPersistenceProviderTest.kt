package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.model.ec.*
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

class PaymentApplicationToEcPersistenceProviderTest : UnitTest() {

    companion object {
        private const val paymentApplicationsToEcId = 1L
        private const val accountingYearId = 3L
        private const val expectedAccountingYearId = 4L
        private const val programmeFundId = 10L
        private const val expectedProgrammeFundId = 11L
        private val submissionDate = LocalDate.now()

        private val programmeFundEntity = ProgrammeFundEntity(programmeFundId, true)
        private val expectedProgrammeFundEntity = ProgrammeFundEntity(expectedProgrammeFundId, true)
        private val programmeFund = ProgrammeFund(programmeFundId, true)
        private val expectedProgrammeFund = ProgrammeFund(expectedProgrammeFundId, true)

        private val accountingYearEntity =
            AccountingYearEntity(accountingYearId, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val expectedAccountingYearEntity =
            AccountingYearEntity(expectedAccountingYearId, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val accountingYear =
            AccountingYear(accountingYearId, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val expectedAccountingYear =
            AccountingYear(expectedAccountingYearId, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))

        private val paymentApplicationToEcCreate = PaymentApplicationToEcCreate(
            id = null,
            programmeFundId = programmeFund.id,
            accountingYearId = accountingYearId,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationToEcUpdate = PaymentApplicationToEcSummaryUpdate(
            id = paymentApplicationsToEcId,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = programmeFund,
            accountingYear = accountingYear,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val expectedPaymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = programmeFund,
            accountingYear = accountingYear,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )


        private val expectedPaymentApplicationsToEcCreateSummary = PaymentApplicationToEcSummary(
            programmeFund = programmeFund,
            accountingYear = accountingYear,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val expectedPaymentApplicationToEcCreate = PaymentApplicationToEcDetail(
            id = 0,
            status = PaymentEcStatus.Draft,
            paymentApplicationToEcSummary = expectedPaymentApplicationsToEcCreateSummary
        )


        private val expectedPaymentApplicationToEc = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatus.Draft,
            paymentApplicationToEcSummary = paymentApplicationsToEcSummary
        )

        private val expectedPaymentApplicationToEcFinalized = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatus.Finished,
            paymentApplicationToEcSummary = expectedPaymentApplicationsToEcSummary
        )

        private fun paymentApplicationsToEcEntity() = PaymentApplicationToEcEntity(
            id = paymentApplicationsToEcId,
            programmeFund = programmeFundEntity,
            accountingYear = accountingYearEntity,
            status = PaymentEcStatus.Draft,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationToEcEntity = PaymentApplicationToEcEntity(
            id = paymentApplicationsToEcId,
            programmeFund = programmeFundEntity,
            accountingYear = accountingYearEntity,
            status = PaymentEcStatus.Draft,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationToEc = PaymentApplicationToEc(
            id = paymentApplicationsToEcId,
            programmeFund = programmeFund,
            accountingYear = accountingYear,
            status = PaymentEcStatus.Draft
        )

    }

    @MockK
    private lateinit var ecPaymentRepository: PaymentApplicationsToEcRepository

    @MockK
    private lateinit var programmeFundRepository: ProgrammeFundRepository

    @MockK
    private lateinit var accountingYearRepository: AccountingYearRepository

    @MockK
    private lateinit var fileRepository: JemsSystemFileService

    @MockK
    private lateinit var reportFileRepository: JemsFileMetadataRepository

    @InjectMockKs
    private lateinit var persistenceProvider: PaymentApplicationToEcPersistenceProvider

    @BeforeEach
    fun resetMocks() {
        clearMocks(ecPaymentRepository, programmeFundRepository, accountingYearRepository)
    }

    @Test
    fun createPaymentApplicationToEc() {
        every { programmeFundRepository.getById(programmeFundId) } returns programmeFundEntity
        every { accountingYearRepository.getById(accountingYearId) } returns accountingYearEntity
        every { ecPaymentRepository.save(any()) } returnsArgument 0

        assertThat(persistenceProvider.createPaymentApplicationToEc(paymentApplicationToEcCreate)).isEqualTo(
            expectedPaymentApplicationToEcCreate
        )
        verify(exactly = 1) { ecPaymentRepository.save(any()) }
    }

    @Test
    fun updatePaymentApplicationToEc() {

        every { ecPaymentRepository.getById(paymentApplicationsToEcId) } returns PaymentApplicationToEcEntity(
            id = paymentApplicationsToEcId,
            programmeFund = expectedProgrammeFundEntity,
            accountingYear = expectedAccountingYearEntity,
            status = PaymentEcStatus.Draft,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        assertThat(persistenceProvider.updatePaymentApplicationToEc(paymentApplicationsToEcId, paymentApplicationToEcUpdate)).isEqualTo(
            PaymentApplicationToEcDetail(
                id = paymentApplicationsToEcId, status = PaymentEcStatus.Draft, isAvailableToReOpen = false,
                paymentApplicationToEcSummary =  PaymentApplicationToEcSummary(
                    programmeFund = expectedProgrammeFund,
                    accountingYear = expectedAccountingYear,
                    nationalReference = "National Reference",
                    technicalAssistanceEur = BigDecimal.valueOf(105.32),
                    submissionToSfcDate = submissionDate,
                    sfcNumber = "SFC number",
                    comment = "Comment"
                )
            )
        )
    }

    @Test
    fun updatePaymentApplicationToEcSummaryOtherSection() {
        every { ecPaymentRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationsToEcEntity()

        assertThat(persistenceProvider.updatePaymentToEcSummaryOtherSection(paymentApplicationToEcUpdate)).isEqualTo(
            expectedPaymentApplicationToEc
        )
    }

    @Test
    fun getPaymentApplicationToEcDetail() {
        every { ecPaymentRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationsToEcEntity()
        assertThat(persistenceProvider.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)).isEqualTo(
            expectedPaymentApplicationToEc
        )
    }

    @Test
    fun findAll() {
        every { ecPaymentRepository.findAll(Pageable.unpaged()) } returns PageImpl(
            listOf(
                paymentApplicationToEcEntity
            )
        )

        assertThat(persistenceProvider.findAll(Pageable.unpaged())).isEqualTo(PageImpl(listOf(paymentApplicationToEc)))
    }

    @Test
    fun updatePaymentApplicationToEcStatus() {
        every { ecPaymentRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationsToEcEntity()
        assertThat(
            persistenceProvider.updatePaymentApplicationToEcStatus(
                paymentApplicationsToEcId,
                PaymentEcStatus.Finished
            )
        ).isEqualTo(expectedPaymentApplicationToEcFinalized)
    }

    @Test
    fun deleteById() {
        every { ecPaymentRepository.deleteById(paymentApplicationsToEcId) } returns Unit
        persistenceProvider.deleteById(paymentApplicationsToEcId)

        verify(exactly = 1) { ecPaymentRepository.deleteById(paymentApplicationsToEcId) }
    }

    @Test
    fun deletePaymentToEcAttachment() {
        val file = mockk<JemsFileMetadataEntity>()
        every { fileRepository.delete(file) } answers { }
        every { reportFileRepository.findByTypeAndId(JemsFileType.PaymentToEcAttachment, 16L) } returns file
        persistenceProvider.deletePaymentToEcAttachment(16L)
        verify(exactly = 1) { fileRepository.delete(file) }
    }

    @Test
    fun `deletePaymentToEcAttachment - not existing`() {
        every { reportFileRepository.findByTypeAndId(JemsFileType.PaymentToEcAttachment, -1L) } returns null
        assertThrows<ResourceNotFoundException> { persistenceProvider.deletePaymentToEcAttachment(-1L) }
        verify(exactly = 0) { fileRepository.delete(any()) }
    }

    @Test
    fun existsDraftByFundAndAccountingYear() {
        val fundId = 385L
        val yearId = 745L
        every { ecPaymentRepository
            .existsByProgrammeFundIdAndAccountingYearIdAndStatus(fundId, yearId, PaymentEcStatus.Draft)
        } returns true
        assertThat(persistenceProvider.existsDraftByFundAndAccountingYear(programmeFundId = fundId, yearId)).isTrue()
    }

    @Test
    fun getAvailableAccountingYearsForFund() {
        val entityA = AccountingYearEntity(
            id = 17L, year = 2025,
            startDate = LocalDate.of(2023, 10, 23),
            endDate = LocalDate.of(2023, 10, 25),
        )
        val entityB = AccountingYearEntity(
            id = 18L, year = 2026,
            startDate = LocalDate.of(2024, 10, 23),
            endDate = LocalDate.of(2024, 10, 25),
        )
        every { ecPaymentRepository.getAvailableAccountingYearForFund(15L) } returns
                listOf(Pair(entityA, 4), Pair(entityB, 0))

        assertThat(persistenceProvider.getAvailableAccountingYearsForFund(15L))
            .containsExactly(
                AccountingYearAvailability(
                    id = 17L, year = 2025,
                    startDate = LocalDate.of(2023, 10, 23),
                    endDate = LocalDate.of(2023, 10, 25),
                    available = false,
                ),
                AccountingYearAvailability(
                    id = 18L, year = 2026,
                    startDate = LocalDate.of(2024, 10, 23),
                    endDate = LocalDate.of(2024, 10, 25),
                    available = true,
                ),
            )
    }

}
