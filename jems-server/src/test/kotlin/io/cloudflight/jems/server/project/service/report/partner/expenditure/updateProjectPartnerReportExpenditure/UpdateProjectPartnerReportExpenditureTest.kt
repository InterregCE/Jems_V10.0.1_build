package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Collections
import kotlin.collections.ArrayList

internal class UpdateProjectPartnerReportExpenditureTest : UnitTest() {

    private val PROJECT_ID = 350L
    private val PARTNER_ID = 489L

    private val reportExpenditureCost = ProjectPartnerReportExpenditureCost(
        id = 780,
        costCategory = BudgetCategory.OfficeAndAdministrationCosts,
        investmentId = 50L,
        contractId = 26L,
        internalReferenceNumber = "irn",
        invoiceNumber = "invoice",
        invoiceDate = LocalDate.now().minusDays(1),
        dateOfPayment = LocalDate.now().plusDays(1),
        description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
        comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        totalValueInvoice = BigDecimal.ONE,
        vat = BigDecimal.ZERO,
        declaredAmount = BigDecimal.TEN,
        currencyCode = "GBP",
        currencyConversionRate = BigDecimal.valueOf(0.84),
        declaredAmountAfterSubmission = BigDecimal.valueOf(8.4),
    )

    private fun reportWithCurrency(id: Long, status: ReportStatus, version: String, currency: String): ProjectPartnerReport {
        val identification = mockk<PartnerReportIdentification>()
        every { identification.currency } returns currency
        return ProjectPartnerReport(
            id = id,
            reportNumber = 1,
            status = status,
            version = version,
            identification = identification,
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @MockK
    lateinit var workPackagePersistence: WorkPackagePersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updatePartnerReportExpenditureCosts: UpdateProjectPartnerReportExpenditure

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, any()) } returns PROJECT_ID
    }

    @Test
    fun `update - successfully - with existing procurement, existing investment, and currency not EUR`() {
        val slotString = mutableListOf<String>()
        val slotTranslations = mutableListOf<Set<InputTranslation>>()
        val slotCurrencies = slot<Set<String>>()
        every { generalValidator.maxLength(capture(slotString), any(), any()) } returns emptyMap()
        every { generalValidator.maxLength(capture(slotTranslations), any(), any()) } returns emptyMap()
        every { generalValidator.onlyValidCurrencies(capture(slotCurrencies), any()) } returns emptyMap()

        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, 84L) } returns
            reportWithCurrency(id = 84L, ReportStatus.Draft, "0.8", "GBP")
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, 84L) } returns setOf(83L)

        val procurement26 = mockk<ProjectPartnerReportProcurement>()
        every { procurement26.id } returns 26L
        every { reportProcurementPersistence.getProcurementsForReportIds(setOf(83L, 84L)) } returns listOf(procurement26)

        val investment50 = mockk<InvestmentSummary>()
        every { investment50.id } returns 50L
        every { workPackagePersistence.getProjectInvestmentSummaries(projectId = PROJECT_ID, "0.8") } returns listOf(investment50)

        every {
            reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
                partnerId = PARTNER_ID,
                reportId = 84L,
                any(),
            )
        } returnsArgument 2

        assertThat(
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID,
                84L,
                listOf(reportExpenditureCost)
            )
        ).containsExactly(
            // calculations are not saved on update, but on report submission
            reportExpenditureCost.copy(currencyConversionRate = null, declaredAmountAfterSubmission = null),
        )

        assertThat(slotString).containsExactlyInAnyOrder("invoice", "irn")
        assertThat(slotTranslations.map { it.first().translation }).containsExactlyInAnyOrder("comment EN", "desc EN")
        assertThat(slotCurrencies.captured).containsExactly("GBP")
    }

    @Test
    fun `update - successfully - with not-existing procurement and not-existing investment`() {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 90L) } returns
            reportWithCurrency(90L, status = ReportStatus.Draft, version = "0.9", currency = "HUF")
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, 90L) } returns setOf(89L)

        val procurement30 = mockk<ProjectPartnerReportProcurement>()
        every { procurement30.id } returns 30L
        every { reportProcurementPersistence.getProcurementsForReportIds(setOf(90L, 89L)) } returns listOf(procurement30)

        val investment60 = mockk<InvestmentSummary>()
        every { investment60.id } returns 60L
        every { workPackagePersistence.getProjectInvestmentSummaries(projectId = PROJECT_ID, "0.9") } returns listOf(investment60)

        every {
            reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
                partnerId = PARTNER_ID,
                reportId = 90L,
                any(),
            )
        } returnsArgument 2

        assertThat(
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID,
                90L,
                listOf(reportExpenditureCost.copy(contractId = 777L, investmentId = 888L))
            )
        ).containsExactly(reportExpenditureCost.copy(contractId = null, investmentId = null))
    }

    @Test
    fun `update - max amount reached`() {
        val listMock = ArrayList(Collections.nCopies(151, mockk<ProjectPartnerReportExpenditureCost>()))
        assertThrows<MaxAmountOfExpendituresReached> {
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId = 84L, listMock)
        }
    }


    @Test
    fun `update - currency change forbidden when partner in EUR`() {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 93L) } returns
            reportWithCurrency(93L, status = ReportStatus.Draft, version = "0.9.1", currency = "EUR")

        assertThrows<PartnerWithDefaultEurCannotSelectOtherCurrency> {
            updatePartnerReportExpenditureCosts
                .updatePartnerReportExpenditureCosts(PARTNER_ID, 93L, listOf(reportExpenditureCost))
        }
    }

    @Test
    fun `update - report closed`() {
        val reportClosed = mockk<ProjectPartnerReport>()
        every { reportClosed.status } returns ReportStatus.Submitted
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 87L) } returns reportClosed

        assertThrows<ReportAlreadyClosed> {
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId = 87L, listOf(reportExpenditureCost))
        }
    }

}
