package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.minio.JemsProjectFileRepository
import io.cloudflight.jems.server.programme.entity.costoption.*
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportInvestmentTranslEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.financialOverview.costCategory.ReportProjectPartnerExpenditureCostCategoryRepository
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
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
            description = "dummy description of attachment",
        )

        private fun dummyExpenditure(
            id: Long,
            report: ProjectPartnerReportEntity,
            lumpSum: PartnerReportLumpSumEntity?,
            unitCost: PartnerReportUnitCostEntity?,
            investment: PartnerReportInvestmentEntity?,
        ) = PartnerReportExpenditureCostEntity(
            id = id,
            partnerReport = report,
            reportLumpSum = lumpSum,
            reportUnitCost = unitCost,
            costCategory = ReportBudgetCategory.InfrastructureCosts,
            reportInvestment = investment,
            procurementId = PROCUREMENT_ID,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.TEN,
            currencyCode = "HUF",
            currencyConversionRate = BigDecimal.valueOf(368),
            declaredAmountAfterSubmission = BigDecimal.valueOf(3680),
            translatedValues = mutableSetOf(),
            attachment = dummyAttachment,
            partOfSample = false,
            certifiedAmount = BigDecimal.valueOf(3680),
            deductedAmount = BigDecimal.ZERO,
            typologyOfErrorId = null,
            verificationComment = null
        ).apply {
            translatedValues.add(
                PartnerReportExpenditureCostTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    comment = "comment EN",
                    description = "desc EN",
                )
            )
        }

        private fun dummyExpectedExpenditure(id: Long, lumpSumId: Long?, unitCostId: Long?, investmentId: Long?) = ProjectPartnerReportExpenditureCost(
            id = id,
            lumpSumId = lumpSumId,
            unitCostId = unitCostId,
            costCategory = ReportBudgetCategory.InfrastructureCosts,
            investmentId = investmentId,
            contractId = PROCUREMENT_ID + 10,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.TEN,
            currencyCode = "HUF",
            currencyConversionRate = BigDecimal.valueOf(368),
            declaredAmountAfterSubmission = BigDecimal.valueOf(3680),
            attachment = JemsFileMetadata(dummyAttachment.id, dummyAttachment.name, dummyAttachment.uploaded),
        )

        private fun dummyExpectedExpenditureNew(id: Long, lumpSumId: Long?, unitCostId: Long?, investmentId: Long?) = ProjectPartnerReportExpenditureCost(
            id = id,
            lumpSumId = lumpSumId,
            unitCostId = unitCostId,
            costCategory = ReportBudgetCategory.EquipmentCosts,
            investmentId = investmentId,
            contractId = PROCUREMENT_ID + 10,
            internalReferenceNumber = "irn NEW",
            invoiceNumber = "invoice NEW",
            invoiceDate = YESTERDAY.minusDays(1),
            dateOfPayment = TOMORROW.plusDays(1),
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN NEW")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")),
            totalValueInvoice = BigDecimal.ZERO,
            vat = BigDecimal.TEN,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.ONE,
            currencyCode = "HUF",
            currencyConversionRate = BigDecimal.valueOf(368),
            declaredAmountAfterSubmission = BigDecimal.valueOf(3680),
            attachment = null,
        )

        private fun dummyLumpSumEntity(reportEntity: ProjectPartnerReportEntity) = PartnerReportLumpSumEntity(
            id = 4L,
            reportEntity = reportEntity,
            programmeLumpSum = ProgrammeLumpSumEntity(
                id = 400L,
                translatedValues = mutableSetOf(ProgrammeLumpSumTranslEntity(ProgrammeLumpSumTranslId(400L, SystemLanguage.EN), "name EN", "desc EN")),
                cost = BigDecimal.TEN,
                splittingAllowed = true,
                phase = ProgrammeLumpSumPhase.Implementation,
                categories = mutableSetOf(),
                isFastTrack = false
            ),
            orderNr = 10,
            period = 2,
            total = BigDecimal.ONE,
            current = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            previouslyPaid = BigDecimal.valueOf(1111, 1),
        )

        private fun dummyUnitCostEntity(reportEntity: ProjectPartnerReportEntity) = PartnerReportUnitCostEntity(
            id = 4L,
            reportEntity = reportEntity,
            programmeUnitCost = ProgrammeUnitCostEntity(
                id = 400L,
                projectId = null,
                translatedValues = mutableSetOf(ProgrammeUnitCostTranslEntity(ProgrammeUnitCostTranslId(400L, SystemLanguage.EN), "name EN", "desc EN")),
                isOneCostCategory = false,
                costPerUnit = BigDecimal.ONE,
                costPerUnitForeignCurrency = BigDecimal.TEN,
                foreignCurrencyCode = "RON",
                categories = mutableSetOf(
                    ProgrammeUnitCostBudgetCategoryEntity(
                        id = 1L,
                        programmeUnitCostId = 5L,
                        category = BudgetCategory.StaffCosts
                    ),
                    ProgrammeUnitCostBudgetCategoryEntity(
                        id = 2L,
                        programmeUnitCostId = 5L,
                        category = BudgetCategory.EquipmentCosts
                    )),
            ),
            numberOfUnits = BigDecimal.ONE,
            total = BigDecimal.ONE,
            current = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
        )

        private fun dummyInvestmentEntity(reportEntity: ProjectPartnerReportEntity) = PartnerReportInvestmentEntity(
            id = 7L,
            reportEntity = reportEntity,
            investmentId = 18L,
            investmentNumber = 2,
            workPackageNumber = 1,
            translatedValues = mutableSetOf(),
            total = BigDecimal.ONE,
            current = BigDecimal.ONE,
            previouslyReported = BigDecimal.ZERO,
        ).apply {
            translatedValues.add(
                PartnerReportInvestmentTranslEntity(TranslationId(this, SystemLanguage.EN), "inv title EN")
            )
        }

        private val dummyLumpSum = ProjectPartnerReportLumpSum(
            id = 4L,
            lumpSumProgrammeId = 400L,
            fastTrack = false,
            period = 2,
            cost = BigDecimal.ONE,
            name = setOf(InputTranslation(SystemLanguage.EN, "name EN"))
        )

        private val dummyUnitCost = ProjectPartnerReportUnitCost(
            id = 4L,
            unitCostProgrammeId = 400L,
            projectDefined = false,
            total = BigDecimal.ONE,
            numberOfUnits = BigDecimal.ONE,
            name = setOf(InputTranslation(SystemLanguage.EN, "name EN")),
            costPerUnit = BigDecimal.ONE,
            costPerUnitForeignCurrency = BigDecimal.TEN,
            foreignCurrencyCode = "RON",
            category = ReportBudgetCategory.Multiple
        )

        private val dummyInvestment = ProjectPartnerReportInvestment(
            id = 7L,
            investmentId = 18L,
            workPackageNumber = 1,
            investmentNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "inv title EN")),
            total = BigDecimal.ONE,
        )

        private val dummyBudgetOptions = ProjectPartnerBudgetOptions(
            officeAndAdministrationOnStaffCostsFlatRate = 20,
            officeAndAdministrationOnDirectCostsFlatRate = 22,
            travelAndAccommodationOnStaffCostsFlatRate = 8,
            staffCostsFlatRate = 1,
            partnerId = PARTNER_ID
        )
    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var reportExpenditureRepository: ProjectPartnerReportExpenditureRepository

    @MockK
    lateinit var reportLumpSumRepository: ProjectPartnerReportLumpSumRepository

    @MockK
    lateinit var reportUnitCostRepository: ProjectPartnerReportUnitCostRepository

    @MockK
    lateinit var reportInvestmentRepository: ProjectPartnerReportInvestmentRepository

    @MockK
    lateinit var fileRepository: JemsProjectFileRepository

    @MockK
    lateinit var reportCostCategoriesRepository: ReportProjectPartnerExpenditureCostCategoryRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportExpenditurePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportExpenditureRepository)
    }

    @Test
    fun getPartnerReportExpenditureCosts() {
        val LUMP_SUM_ID = 808L
        val UNIT_COST_ID = 809L
        val INVESTMENT_ID = 810L
        val report = mockk<ProjectPartnerReportEntity>()
        val lumpSum = mockk<PartnerReportLumpSumEntity>()
        val unitCost = mockk<PartnerReportUnitCostEntity>()
        val investment = mockk<PartnerReportInvestmentEntity>()
        every { lumpSum.id } returns LUMP_SUM_ID
        every { unitCost.id } returns UNIT_COST_ID
        every { investment.id } returns INVESTMENT_ID
        val expenditure = dummyExpenditure(id = 14L, report, lumpSum, unitCost, investment)
        every { reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            reportId = 44L,
            partnerId = PARTNER_ID,
        ) } returns mutableListOf(expenditure)

        assertThat(persistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 44L))
            .containsExactly(
                dummyExpectedExpenditure(id = 14L, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID)
                    .copy(contractId = PROCUREMENT_ID)
            )
    }

    @Test
    fun existsByExpenditureId() {
        every { reportExpenditureRepository.existsByPartnerReportPartnerIdAndPartnerReportIdAndId(
            PARTNER_ID, reportId = 18L, 45L) } returns false
        assertThat(persistence.existsByExpenditureId(PARTNER_ID, reportId = 18L, 45L)).isFalse
    }

    @Test
    fun getAvailableLumpSums() {
        every { reportLumpSumRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(PARTNER_ID, reportId = 20L) } returns
            mutableListOf(dummyLumpSumEntity(mockk()))
        assertThat(persistence.getAvailableLumpSums(PARTNER_ID, reportId = 20L)).containsExactly(dummyLumpSum)
    }

    @Test
    fun getAvailableUnitCosts() {
        every { reportUnitCostRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(PARTNER_ID, reportId = 20L) } returns
            mutableListOf(dummyUnitCostEntity(mockk()))
        assertThat(persistence.getAvailableUnitCosts(PARTNER_ID, reportId = 20L)).containsExactly(dummyUnitCost)
    }

    @Test
    fun getAvailableInvestments() {
        every { reportInvestmentRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(PARTNER_ID, reportId = 20L)
        } returns mutableListOf(dummyInvestmentEntity(mockk()))
        assertThat(persistence.getAvailableInvestments(PARTNER_ID, reportId = 20L)).containsExactly(dummyInvestment)
    }

    @Test
    fun getAvailableBudgetOptions() {
        val budgetOptionsEntity = mockk<ReportProjectPartnerExpenditureCostCategoryEntity>()
        every { budgetOptionsEntity.reportEntity } returns mockk()
        every { budgetOptionsEntity.reportEntity.partnerId } returns PARTNER_ID
        every { budgetOptionsEntity.officeAndAdministrationOnStaffCostsFlatRate } returns 20
        every { budgetOptionsEntity.officeAndAdministrationOnDirectCostsFlatRate } returns 22
        every { budgetOptionsEntity.travelAndAccommodationOnStaffCostsFlatRate } returns 8
        every { budgetOptionsEntity.staffCostsFlatRate } returns 1
        every { budgetOptionsEntity.otherCostsOnStaffCostsFlatRate } returns null
        every { reportCostCategoriesRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 20L)
        } returns budgetOptionsEntity
        assertThat(persistence.getAvailableBudgetOptions(PARTNER_ID, reportId = 20L)).isEqualTo(dummyBudgetOptions)
    }

    @Test
    fun updatePartnerReportExpenditureCosts() {
        val LUMP_SUM_ID = 708L
        val UNIT_COST_ID = 709L
        val INVESTMENT_ID = 710L
        val report = mockk<ProjectPartnerReportEntity>()
        val lumpSum = mockk<PartnerReportLumpSumEntity>()
        val unitCost = mockk<PartnerReportUnitCostEntity>()
        val investment = mockk<PartnerReportInvestmentEntity>()
        every { lumpSum.id } returns LUMP_SUM_ID
        every { unitCost.id } returns UNIT_COST_ID
        every { investment.id } returns INVESTMENT_ID

        val entityToStay = dummyExpenditure(EXPENDITURE_TO_STAY, report, null, null, null)
        val entityToDelete = dummyExpenditure(EXPENDITURE_TO_DELETE, report, null, null, null)
        val entityToUpdate = dummyExpenditure(EXPENDITURE_TO_UPDATE, report, lumpSum, unitCost, investment)
        every { reportRepository.findByIdAndPartnerId(id = 58L, PARTNER_ID) } returns report

        every { reportExpenditureRepository.findByPartnerReportOrderByIdDesc(report) } returns
            mutableListOf(entityToStay, entityToDelete, entityToUpdate)

        every { fileRepository.delete(dummyAttachment) } answers { }
        val slotDeleted = slot<Iterable<PartnerReportExpenditureCostEntity>>()
        every { reportExpenditureRepository.deleteAll(capture(slotDeleted)) } answers { }

        every { reportLumpSumRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(PARTNER_ID, reportId = 58L) } returns
            mutableListOf(lumpSum)

        every { reportUnitCostRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(PARTNER_ID, reportId = 58L) } returns
            mutableListOf(unitCost)

        every { reportInvestmentRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(PARTNER_ID, reportId = 58L)
        } returns mutableListOf(investment)

        val slotSavedEntities = mutableListOf<PartnerReportExpenditureCostEntity>()
        every { reportExpenditureRepository.save(capture(slotSavedEntities)) } returnsArgument 0

        assertThat(persistence.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId = 58L, listOf(
            dummyExpectedExpenditure(id = EXPENDITURE_TO_STAY, null, null, null),
            dummyExpectedExpenditure(id = EXPENDITURE_TO_UPDATE, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID),
            dummyExpectedExpenditureNew(id = EXPENDITURE_TO_ADD_1, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID),
            dummyExpectedExpenditureNew(id = EXPENDITURE_TO_ADD_2, null, null, null),
        ))).containsExactly(
            dummyExpectedExpenditure(id = EXPENDITURE_TO_STAY, null, null, null),
            dummyExpectedExpenditure(id = EXPENDITURE_TO_UPDATE, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID),
            dummyExpectedExpenditureNew(id = EXPENDITURE_TO_ADD_1, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID),
            dummyExpectedExpenditureNew(id = EXPENDITURE_TO_ADD_2, null, null, null),
        )

        assertThat(slotDeleted.captured).containsExactly(entityToDelete)
        assertThat(slotSavedEntities.map { it.id }).containsExactly(
            // order is important, because not-yet-existing elements will get ID based on insertion order
            EXPENDITURE_TO_ADD_1, EXPENDITURE_TO_ADD_2
        )

        slotSavedEntities.forEachIndexed { index, it ->
            assertThat(it.reportLumpSum).isEqualTo(if (index == 0) lumpSum else null)
            assertThat(it.reportUnitCost).isEqualTo(if (index == 0) unitCost else null)
            assertThat(it.costCategory).isEqualTo(ReportBudgetCategory.EquipmentCosts)
            assertThat(it.reportInvestment).isEqualTo(if (index == 0) investment else null)
            assertThat(it.procurementId).isEqualTo(PROCUREMENT_ID + 10)
            assertThat(it.internalReferenceNumber).isEqualTo("irn NEW")
            assertThat(it.invoiceNumber).isEqualTo("invoice NEW")
            assertThat(it.invoiceDate).isEqualTo(YESTERDAY.minusDays(1))
            assertThat(it.dateOfPayment).isEqualTo(TOMORROW.plusDays(1))
            assertThat(it.translatedValues.first().comment).isEqualTo("comment EN NEW")
            assertThat(it.translatedValues.first().description).isEqualTo("desc EN NEW")
            assertThat(it.totalValueInvoice).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(it.vat).isEqualByComparingTo(BigDecimal.TEN)
            assertThat(it.numberOfUnits).isEqualByComparingTo(BigDecimal.ONE)
            assertThat(it.pricePerUnit).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(it.declaredAmount).isEqualByComparingTo(BigDecimal.ONE)
        }
    }

}
