package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class PaymentApplicationsToEcPersistenceProviderTest : UnitTest() {
    @MockK
    private lateinit var paymentApplicationsToEcRepository: PaymentApplicationsToEcRepository

    @MockK
    lateinit var programmeFundRepository: ProgrammeFundRepository

    @MockK
    lateinit var accountingYearRepository: AccountingYearRepository

    @MockK
    lateinit var reportFileRepository: JemsFileMetadataRepository
    @MockK
    lateinit var fileRepository: JemsSystemFileService

    @InjectMockKs
    private lateinit var persistenceProvider: PaymentApplicationToEcPersistenceProvider

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

        private val paymentApplicationToEcCreate = PaymentApplicationToEcSummaryUpdate(
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
            programmeFundId = 11L,
            accountingYearId = 4L,
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

        private val expectedUpdatedPaymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = expectedProgrammeFund,
            accountingYear = expectedAccountingYear,
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

        private val expectedPaymentApplicationToEcUpdate = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatus.Draft,
            paymentApplicationToEcSummary = expectedUpdatedPaymentApplicationsToEcSummary
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

    @BeforeEach
    fun resetMocks() {
        clearMocks(paymentApplicationsToEcRepository, programmeFundRepository, accountingYearRepository)
    }

    @Test
    fun createPaymentApplicationToEc() {
        every { programmeFundRepository.getById(programmeFundId) } returns programmeFundEntity
        every { accountingYearRepository.getById(accountingYearId) } returns accountingYearEntity
        every { paymentApplicationsToEcRepository.save(any()) } returnsArgument 0

        assertThat(persistenceProvider.createPaymentApplicationToEc(paymentApplicationToEcCreate)).isEqualTo(
            expectedPaymentApplicationToEcCreate
        )
        verify(exactly = 1) { paymentApplicationsToEcRepository.save(any()) }
    }

    @Test
    fun findAll() {
        every { paymentApplicationsToEcRepository.findAll(Pageable.unpaged()) } returns PageImpl(
            listOf(
                paymentApplicationToEcEntity
            )
        )

        assertThat(persistenceProvider.findAll(Pageable.unpaged())).isEqualTo(PageImpl(listOf(paymentApplicationToEc)))
    }

    @Test
    fun getPaymentApplicationToEcDetail() {
        every { paymentApplicationsToEcRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationsToEcEntity()
        assertThat(persistenceProvider.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)).isEqualTo(
            expectedPaymentApplicationToEc
        )
    }

    @Test
    fun updatePaymentApplicationToEc() {
        every { programmeFundRepository.getById(expectedProgrammeFundId) } returns expectedProgrammeFundEntity
        every { accountingYearRepository.getById(expectedAccountingYearId) } returns expectedAccountingYearEntity
        every { paymentApplicationsToEcRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationsToEcEntity()

        assertThat(persistenceProvider.updatePaymentApplicationToEc(paymentApplicationToEcUpdate)).isEqualTo(
            expectedPaymentApplicationToEcUpdate
        )
    }

    @Test
    fun updatePaymentApplicationToEcSummaryOtherSection() {
        every { paymentApplicationsToEcRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationsToEcEntity()

        assertThat(persistenceProvider.updatePaymentToEcSummaryOtherSection(paymentApplicationToEcUpdate)).isEqualTo(
            expectedPaymentApplicationToEc
        )
    }

    @Test
    fun deleteById() {
        every { paymentApplicationsToEcRepository.deleteById(paymentApplicationsToEcId) } returns Unit
        persistenceProvider.deleteById(paymentApplicationsToEcId)

        verify(exactly = 1) { paymentApplicationsToEcRepository.deleteById(paymentApplicationsToEcId) }
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
    fun finalizePaymentToEc() {
        every { paymentApplicationsToEcRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationsToEcEntity()
        assertThat(persistenceProvider.finalizePaymentApplicationToEc(paymentApplicationsToEcId)).isEqualTo(expectedPaymentApplicationToEcFinalized)
    }
}
