package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Collections
import kotlin.collections.ArrayList

internal class UpdateProjectPartnerReportExpenditureTest : UnitTest() {

    private val PARTNER_ID = 489L
    private val UPLOADED = ZonedDateTime.now()

    private val reportExpenditureCost = ProjectPartnerReportExpenditureCost(
        id = 780,
        lumpSumId = null,
        unitCostId = null,
        costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts,
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
        numberOfUnits = BigDecimal.ZERO,
        pricePerUnit = BigDecimal.ZERO,
        declaredAmount = BigDecimal.TEN,
        currencyCode = "GBP",
        currencyConversionRate = BigDecimal.valueOf(0.84),
        declaredAmountAfterSubmission = BigDecimal.valueOf(8.4),
        attachment = JemsFileMetadata(47L, "file.xlsx", UPLOADED),
    )

    private fun reportWithCurrency(id: Long, status: ReportStatus, version: String, currency: String?): ProjectPartnerReport {
        val identification = mockk<PartnerReportIdentification>()
        every { identification.currency } returns currency
        return ProjectPartnerReport(
            id = id,
            reportNumber = 1,
            status = status,
            version = version,
            identification = identification,
            firstSubmission = UPLOADED,
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectPartnerReportProcurementPersistence

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
    }

    @Test
    fun `update - successfully - with existing procurement, existing investment, and currency not EUR`() {
        val slotString = mutableListOf<String>()
        val slotTranslations = mutableListOf<Set<InputTranslation>>()
        val slotBigDecimal = mutableListOf<BigDecimal>()
        val slotCurrencies = slot<Set<String>>()
        every { generalValidator.maxLength(capture(slotString), any(), any()) } returns emptyMap()
        every { generalValidator.maxLength(capture(slotTranslations), any(), any()) } returns emptyMap()
        every { generalValidator.numberBetween(capture(slotBigDecimal), BigDecimal.ZERO, any(), any()) } returns emptyMap()
        every { generalValidator.onlyValidCurrencies(capture(slotCurrencies), any()) } returns emptyMap()

        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, 84L) } returns
            reportWithCurrency(id = 84L, ReportStatus.Draft, "0.8", "GBP")
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, 84L) } returns setOf(83L)

        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(83L, 84L)) } returns setOf(Pair(26L, "Proc26"))

        every { reportExpenditurePersistence.getAvailableLumpSums(PARTNER_ID, reportId = 84L) } returns emptyList()
        every { reportExpenditurePersistence.getAvailableUnitCosts(PARTNER_ID, reportId = 84L) } returns emptyList()
        every { reportExpenditurePersistence.getAvailableInvestments(PARTNER_ID, reportId = 84L) } returns emptyList()

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
        assertThat(slotBigDecimal).containsExactly(BigDecimal.TEN)
        assertThat(slotCurrencies.captured).containsExactly("GBP")
    }

    @Test
    fun `update - successfully - with not-existing procurement and not-existing investment`() {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 90L) } returns
            reportWithCurrency(90L, status = ReportStatus.Draft, version = "0.9", currency = "HUF")
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, 90L) } returns setOf(89L)

        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(90L, 89L)) } returns setOf(Pair(30L, "Proc30"))

        every { reportExpenditurePersistence.getAvailableLumpSums(PARTNER_ID, reportId = 90L) } returns emptyList()
        every { reportExpenditurePersistence.getAvailableUnitCosts(PARTNER_ID, reportId = 90L) } returns emptyList()
        every { reportExpenditurePersistence.getAvailableInvestments(PARTNER_ID, reportId = 90L) } returns emptyList()

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

    @Test
    fun `update - successfully - when staff costs, do not save some other fields`() {
        mockGeneralStuffForTestingCategories(
            reportId = 92L,
            procurementId = 1015L,
        )

        val input = reportExpenditureCost.copy(
            unitCostId = null,
            lumpSumId = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            contractId = 1015L,
            investmentId = 1016L,
            vat = ONE,
            invoiceNumber = "some value",
        )

        assertThat(
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID,
                92L,
                listOf(input.copy())
            )
        ).containsExactly(input.copy(
            contractId = null,
            investmentId = null,
            vat = null,
            invoiceNumber = null,
            // these next are always cleared
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
        ))
    }

    @Test
    fun `update - successfully - when travel costs, do not save investmentId`() {
        mockGeneralStuffForTestingCategories(
            reportId = 95L,
            procurementId = 1020L,
        )

        val input = reportExpenditureCost.copy(
            unitCostId = null,
            lumpSumId = null,
            costCategory = ReportBudgetCategory.TravelAndAccommodationCosts,
            contractId = 1020L,
            investmentId = 1021L,
            vat = ONE,
            invoiceNumber = "some value",
        )

        val result = updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
            PARTNER_ID,
            95L,
            listOf(input.copy())
        )

        assertThat(result).containsExactly(input.copy(
            investmentId = null,
            // these next are always cleared
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
        ))
    }

    @ParameterizedTest(name = "update - successfully - when {0}, do not save other fields")
    @EnumSource(value = ReportBudgetCategory::class, names = ["StaffCosts", "TravelAndAccommodationCosts"], mode = EnumSource.Mode.EXCLUDE)
    fun `update - successfully - when CAT, do not save other fields`(category: ReportBudgetCategory) {
        mockGeneralStuffForTestingCategories(
            reportId = 96L + category.ordinal,
            procurementId = 500L,
        )

        val input = reportExpenditureCost.copy(
            unitCostId = null,
            lumpSumId = null,
            costCategory = category,
            contractId = 500L,
            investmentId = 666L,
            vat = ONE,
            invoiceNumber = "some value",
        )

        val result = updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
            PARTNER_ID,
            96L + category.ordinal,
            listOf(input.copy())
        )

        assertThat(result).containsExactly(input.copy(
            investmentId = null /* not available investment */,
            // these next are always cleared, so nothing should be specifically extra removed now
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
        ))
    }

    private fun mockGeneralStuffForTestingCategories(
        reportId: Long,
        procurementId: Long,
    ) {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId) } returns
            reportWithCurrency(reportId, status = ReportStatus.Draft, version = "1", currency = null)
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, reportId) } returns emptySet()

        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(reportId)) } returns setOf(Pair(procurementId, "contractName"))

        every { reportExpenditurePersistence.getAvailableLumpSums(PARTNER_ID, reportId = reportId) } returns emptyList()
        every { reportExpenditurePersistence.getAvailableUnitCosts(PARTNER_ID, reportId = reportId) } returns emptyList()
        every { reportExpenditurePersistence.getAvailableInvestments(PARTNER_ID, reportId = reportId) } returns emptyList()

        every { reportExpenditurePersistence.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId = reportId, any()) } returnsArgument 2
    }

}
