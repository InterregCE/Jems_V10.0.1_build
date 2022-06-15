package io.cloudflight.jems.server.project.service.report.partner.submitProjectPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
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
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class SubmitProjectPartnerReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 256L
        private const val PARTNER_ID = 579L
        private val TODAY = LocalDate.now()
        private val YEAR = TODAY.year
        private val MONTH = TODAY.monthValue

        private val mockedResult = ProjectPartnerReportSubmissionSummary(
            id = 888L,
            reportNumber = 4,
            status = ReportStatus.Submitted,
            version = "5.6.0",
            // not important
            firstSubmission = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.PARTNER,
        )

        private val expenditure1 = ProjectPartnerReportExpenditureCost(
            id = 630,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            investmentId = 10L,
            contractId = 54L,
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1),
            dateOfPayment = LocalDate.of(2022, 2, 1),
            numberOfUnits = BigDecimal.ZERO,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.valueOf(25448, 2),
            currencyCode = "CZK",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )

        private val options = mockk<ReportExpenditureCostCategory>().also {
            every { it.options } returns ProjectPartnerBudgetOptions(
                partnerId = PARTNER_ID,
                officeAndAdministrationOnStaffCostsFlatRate = null,
                officeAndAdministrationOnDirectCostsFlatRate = 10,
                travelAndAccommodationOnStaffCostsFlatRate = 15,
                staffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
            )
        }

        private val expectedPersistedExpenditureCostCategory = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(999, 2),
            office = BigDecimal.valueOf(114, 2),
            travel = BigDecimal.valueOf(149, 2),
            external = BigDecimal.ZERO,
            equipment = BigDecimal.ZERO,
            infrastructure = BigDecimal.ZERO,
            other = BigDecimal.ZERO,
            lumpSum = BigDecimal.ZERO,
            unitCost = BigDecimal.ZERO,
            sum = BigDecimal.valueOf(1262, 2),
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @MockK
    lateinit var currencyPersistence: CurrencyPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectReportExpenditureCostCategoryPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var submitReport: SubmitProjectPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(reportExpenditurePersistence)
        clearMocks(auditPublisher)
    }

    @Test
    fun submit() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Draft

        val submissionTime = slot<ZonedDateTime>()

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 35L) } returns report

        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, 35L) } returns
            listOf(expenditure1)
        every { currencyPersistence.findAllByIdYearAndIdMonth(year = YEAR, month = MONTH) } returns
            listOf(
                CurrencyConversion("CZK", YEAR, MONTH, "", BigDecimal.valueOf(254855, 4)),
                CurrencyConversion("PLN", YEAR, MONTH, "", BigDecimal.ONE) /* not used */,
            )
        val slotExpenditures = slot<List<ProjectPartnerReportExpenditureCost>>()
        every { reportExpenditurePersistence
            .updatePartnerReportExpenditureCosts(PARTNER_ID, 35L, capture(slotExpenditures)) } returnsArgument 2

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 35L) } returns options
        val expenditureCcSlot = slot<BudgetCostsCalculationResultFull>()
        every { reportExpenditureCostCategoryPersistence
            .updateCurrentlyReportedValues(PARTNER_ID, reportId = 35L, capture(expenditureCcSlot))
        } answers { }

        every { reportPersistence.submitReportById(any(), any(), capture(submissionTime)) } returns mockedResult
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "5.6.0") } returns PROJECT_ID

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        submitReport.submit(PARTNER_ID, 35L)

        verify(exactly = 1) { reportPersistence.submitReportById(PARTNER_ID, 35L, any()) }
        assertThat(submissionTime.captured).isAfter(ZonedDateTime.now().minusMinutes(1))
        assertThat(submissionTime.captured).isBefore(ZonedDateTime.now().plusMinutes(1))

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_REPORT_SUBMITTED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("FG01_654")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(888L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[FG01_654] [PP1] Partner report R.4 submitted")

        assertThat(slotExpenditures.captured).containsExactly(
            expenditure1.copy(
                currencyConversionRate = BigDecimal.valueOf(254855, 4),
                declaredAmountAfterSubmission = BigDecimal.valueOf(999, 2),
            ),
        )
        assertThat(expenditureCcSlot.captured).isEqualTo(expectedPersistedExpenditureCostCategory)
    }

    @Test
    fun `submit - report is not draft`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Submitted

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 36L) } returns report

        assertThrows<ReportAlreadyClosed> { submitReport.submit(PARTNER_ID, 36L) }
        verify(exactly = 0) { reportPersistence.submitReportById(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `submit - needed rates not available`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Draft

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 40L) } returns report
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, 40L) } returns
            listOf(expenditure1)
        every { currencyPersistence.findAllByIdYearAndIdMonth(year = YEAR, month = MONTH) } returns emptyList()

        assertThrows<CurrencyRatesMissing> { submitReport.submit(PARTNER_ID, 40L) }
        verify(exactly = 0) { reportExpenditurePersistence.updatePartnerReportExpenditureCosts(any(), any(), any()) }
    }

}
