package io.cloudflight.jems.server.project.repository.report.expenditure.verification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.*
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectControlReportExpenditurePersistenceProvider
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectControlReportExpenditurePersistenceProviderTest : UnitTest() {
    companion object {
        private const val PARTNER_ID = 380L
        private const val PROCUREMENT_ID = 18L
        private const val INVESTMENT_ID = 28L

        private const val EXPENDITURE_TO_UPDATE = 40L

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

        private fun dummyExpectedExpenditure(id: Long, lumpSumId: Long?, unitCostId: Long?, investmentId: Long?) =
            ProjectPartnerControlReportExpenditureVerification(
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
            partOfSample = false,
            certifiedAmount = BigDecimal.valueOf(3680),
            deductedAmount = BigDecimal.ZERO,
            typologyOfErrorId = null,
            verificationComment = null
        )

        private fun dummyExpectedExpenditureUpdate(id: Long) =
            ProjectPartnerControlReportExpenditureVerificationUpdate(
                id = id,
                partOfSample = true,
                certifiedAmount = BigDecimal.valueOf(3670),
                deductedAmount = BigDecimal.valueOf(10),
                typologyOfErrorId = 1,
                verificationComment = "Some text"
            )
    }

    @MockK
    lateinit var reportExpenditureRepository: ProjectPartnerReportExpenditureRepository

    @InjectMockKs
    lateinit var persistence: ProjectControlReportExpenditurePersistenceProvider

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
        val expenditure = dummyExpenditure(
            id = 14L,
            report,
            lumpSum,
            unitCost,
            investment
        )
        every { reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            reportId = 44L,
            partnerId = PARTNER_ID,
        ) } returns mutableListOf(expenditure)

        assertThat(
            persistence.getPartnerControlReportExpenditureVerification(
                PARTNER_ID,
                reportId = 44L
            )
        )
            .containsExactly(
                dummyExpectedExpenditure(
                    id = 14L,
                    LUMP_SUM_ID,
                    UNIT_COST_ID,
                    INVESTMENT_ID
                )
                    .copy(contractId = PROCUREMENT_ID)
            )
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

        val entityToUpdate = dummyExpenditure(
            EXPENDITURE_TO_UPDATE,
            report,
            lumpSum,
            unitCost,
            investment
        )

        every { reportExpenditureRepository
            .findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(58L, PARTNER_ID) } returns
            mutableListOf(entityToUpdate)

        val result = persistence.updatePartnerControlReportExpenditureVerification(
            PARTNER_ID, reportId = 58L, listOf(
                dummyExpectedExpenditureUpdate(
                    id = EXPENDITURE_TO_UPDATE
                )
            )
        )

        assertThat(result.first().lumpSumId).isEqualTo(lumpSum.id)
        assertThat(result.first().unitCostId).isEqualTo(unitCost.id)
        assertThat(result.first().costCategory).isEqualTo(ReportBudgetCategory.InfrastructureCosts)
        assertThat(result.first().investmentId).isEqualTo(investment.id)
        assertThat(result.first().internalReferenceNumber).isEqualTo("irn")
        assertThat(result.first().invoiceNumber).isEqualTo("invoice")
        assertThat(result.first().invoiceDate).isEqualTo(YESTERDAY)
        assertThat(result.first().dateOfPayment).isEqualTo(TOMORROW)
        assertThat(result.first().comment.first().translation).isEqualTo("comment EN")
        assertThat(result.first().description.first().translation).isEqualTo("desc EN")
        assertThat(result.first().totalValueInvoice).isEqualByComparingTo(BigDecimal.ONE)
        assertThat(result.first().vat).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(result.first().numberOfUnits).isEqualByComparingTo(BigDecimal.ONE)
        assertThat(result.first().pricePerUnit).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(result.first().declaredAmount).isEqualByComparingTo(BigDecimal.TEN)
        assertThat(result.first().currencyCode).isEqualTo("HUF")
        assertThat(result.first().currencyConversionRate).isEqualByComparingTo(BigDecimal.valueOf(368))
        assertThat(result.first().declaredAmountAfterSubmission).isEqualByComparingTo(BigDecimal.valueOf(3680))
        assertThat(result.first().partOfSample).isEqualTo(true)
        assertThat(result.first().certifiedAmount).isEqualByComparingTo(BigDecimal.valueOf(3670))
        assertThat(result.first().deductedAmount).isEqualByComparingTo(BigDecimal.valueOf(10))
        assertThat(result.first().typologyOfErrorId).isEqualTo(1)
        assertThat(result.first().verificationComment).isEqualTo("Some text")
    }
}
