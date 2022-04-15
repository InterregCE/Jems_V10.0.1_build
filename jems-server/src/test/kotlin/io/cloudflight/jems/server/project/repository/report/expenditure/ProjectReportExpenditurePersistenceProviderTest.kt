package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectReportExpenditurePersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 380L
        private const val PROCUREMENT_ID = 18L
        private const val INVESTMENT_ID = 28L

        private const val EXPENDITURE_TO_UPDATE = 40L
        private const val EXPENDITURE_TO_DELETE = 41L
        private const val EXPENDITURE_TO_STAY = 42L
        private const val EXPENDITURE_TO_ADD_1 = -1L
        private const val EXPENDITURE_TO_ADD_2 = -2L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        val dummyAttachment = ReportProjectFileEntity(
            id = 970L,
            projectId = 4L,
            partnerId = PARTNER_ID,
            path = "",
            minioBucket = "minioBucket",
            minioLocation = "",
            name = "some_file.txt",
            type = mockk(),
            size = 1475,
            user = mockk(),
            uploaded = ZonedDateTime.now(),
        )

        private fun dummyExpenditure(id: Long, report: ProjectPartnerReportEntity) = PartnerReportExpenditureCostEntity(
            id = id,
            partnerReport = report,
            costCategory = BudgetCategory.InfrastructureCosts,
            investmentId = INVESTMENT_ID,
            procurementId = PROCUREMENT_ID,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            declaredAmount = BigDecimal.TEN,
            currencyCode = "HUF",
            currencyConversionRate = BigDecimal.valueOf(368),
            declaredAmountAfterSubmission = BigDecimal.valueOf(3680),
            translatedValues = mutableSetOf(),
            attachment = dummyAttachment,
        ).apply {
            translatedValues.add(
                PartnerReportExpenditureCostTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    comment = "comment EN",
                    description = "desc EN",
                )
            )
        }

        private fun dummyExpectedExpenditure(id: Long) = ProjectPartnerReportExpenditureCost(
            id = id,
            costCategory = BudgetCategory.InfrastructureCosts,
            investmentId = INVESTMENT_ID,
            contractId = PROCUREMENT_ID,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            declaredAmount = BigDecimal.TEN,
            currencyCode = "HUF",
            currencyConversionRate = BigDecimal.valueOf(368),
            declaredAmountAfterSubmission = BigDecimal.valueOf(3680),
            attachment = ProjectReportFileMetadata(dummyAttachment.id, dummyAttachment.name, dummyAttachment.uploaded),
        )

        private fun dummyExpectedExpenditureNew(id: Long) = ProjectPartnerReportExpenditureCost(
            id = id,
            costCategory = BudgetCategory.EquipmentCosts,
            investmentId = INVESTMENT_ID + 10,
            contractId = PROCUREMENT_ID + 10,
            internalReferenceNumber = "irn NEW",
            invoiceNumber = "invoice NEW",
            invoiceDate = YESTERDAY.minusDays(1),
            dateOfPayment = TOMORROW.plusDays(1),
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN NEW")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")),
            totalValueInvoice = BigDecimal.ZERO,
            vat = BigDecimal.TEN,
            declaredAmount = BigDecimal.ONE,
            currencyCode = "HUF",
            currencyConversionRate = BigDecimal.valueOf(368),
            declaredAmountAfterSubmission = BigDecimal.valueOf(3680),
            attachment = ProjectReportFileMetadata(dummyAttachment.id, dummyAttachment.name, dummyAttachment.uploaded),
            )
    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var reportExpenditureRepository: ProjectPartnerReportExpenditureRepository

    @MockK
    lateinit var minioStorage: MinioStorage

    @MockK
    lateinit var reportFileRepository: ProjectReportFileRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportExpenditurePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportExpenditureRepository)
    }

    @Test
    fun getPartnerReportExpenditureCosts() {
        val report = mockk<ProjectPartnerReportEntity>()
        val expenditure = dummyExpenditure(id = 14L, report)
        every { reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            reportId = 44L,
            partnerId = PARTNER_ID,
        ) } returns mutableListOf(expenditure)

        assertThat(persistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 44L))
            .containsExactly(dummyExpectedExpenditure(id = 14L))
    }

    @Test
    fun existsByExpenditureId() {
        every { reportExpenditureRepository.existsByPartnerReportPartnerIdAndPartnerReportIdAndId(
            PARTNER_ID, reportId = 18L, 45L) } returns false
        assertThat(persistence.existsByExpenditureId(PARTNER_ID, reportId = 18L, 45L)).isFalse
    }

    @Test
    fun updatePartnerReportExpenditureCosts() {
        val report = mockk<ProjectPartnerReportEntity>()
        val entityToStay = dummyExpenditure(EXPENDITURE_TO_STAY, report)
        val entityToDelete = dummyExpenditure(EXPENDITURE_TO_DELETE, report)
        val entityToUpdate = dummyExpenditure(EXPENDITURE_TO_UPDATE, report)
        every { reportRepository.findByIdAndPartnerId(id = 58L, PARTNER_ID) } returns report
        every { reportExpenditureRepository.findExistingExpenditureIdsFor(report) } returns
            setOf(EXPENDITURE_TO_UPDATE, EXPENDITURE_TO_DELETE, EXPENDITURE_TO_STAY)

        every { reportExpenditureRepository.findByPartnerReportOrderByIdDesc(report) } returns
            mutableListOf(entityToStay, entityToDelete, entityToUpdate)

        every { minioStorage.deleteFile(dummyAttachment.minioBucket, dummyAttachment.minioLocation) } answers { }
        every { reportFileRepository.delete(dummyAttachment) } answers { }
        val slotDeleted = slot<Iterable<PartnerReportExpenditureCostEntity>>()
        every { reportExpenditureRepository.deleteAll(capture(slotDeleted)) } answers { }

        val slotSavedEntities = mutableListOf<PartnerReportExpenditureCostEntity>()
        every { reportExpenditureRepository.save(capture(slotSavedEntities)) } returnsArgument 0

        persistence.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId = 58L, listOf(
            dummyExpectedExpenditure(id = EXPENDITURE_TO_STAY),
            dummyExpectedExpenditure(id = EXPENDITURE_TO_UPDATE),
            dummyExpectedExpenditureNew(id = EXPENDITURE_TO_ADD_1),
            dummyExpectedExpenditureNew(id = EXPENDITURE_TO_ADD_2),
        ))

        assertThat(slotDeleted.captured).containsExactly(entityToDelete)
        assertThat(slotSavedEntities.map { it.id }).containsExactly(
            // order is important, because not-yet-existing elements will get ID based on insertion order
            EXPENDITURE_TO_ADD_1, EXPENDITURE_TO_ADD_2
        )

        slotSavedEntities.forEach {
            assertThat(it.costCategory).isEqualTo(BudgetCategory.EquipmentCosts)
            assertThat(it.investmentId).isEqualTo(INVESTMENT_ID + 10)
            assertThat(it.procurementId).isEqualTo(PROCUREMENT_ID + 10)
            assertThat(it.internalReferenceNumber).isEqualTo("irn NEW")
            assertThat(it.invoiceNumber).isEqualTo("invoice NEW")
            assertThat(it.invoiceDate).isEqualTo(YESTERDAY.minusDays(1))
            assertThat(it.dateOfPayment).isEqualTo(TOMORROW.plusDays(1))
            assertThat(it.translatedValues.first().comment).isEqualTo("comment EN NEW")
            assertThat(it.translatedValues.first().description).isEqualTo("desc EN NEW")
            assertThat(it.totalValueInvoice).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(it.vat).isEqualByComparingTo(BigDecimal.TEN)
            assertThat(it.declaredAmount).isEqualByComparingTo(BigDecimal.ONE)
        }
    }

}
