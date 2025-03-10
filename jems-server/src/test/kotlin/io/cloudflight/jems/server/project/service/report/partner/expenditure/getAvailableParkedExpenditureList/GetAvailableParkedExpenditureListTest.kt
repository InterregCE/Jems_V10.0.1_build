package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableParkedExpenditureList

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.PARTNER_ID
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedExpenditure
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedLinked
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.anonymizeIfSensitive
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class GetAvailableParkedExpenditureListTest : UnitTest() {

    companion object {
        private val DATE_TIME_NOW = ZonedDateTime.now()

        private val parkingMetadata = ExpenditureParkingMetadata(
            reportOfOriginId = 75L,
            reportOfOriginNumber = 4,
            reportProjectOfOriginId = null,
            originalExpenditureNumber = 3,
            parkedOn = DATE_TIME_NOW,
            parkedFromExpenditureId = 14L
        )
        private val uploadTime = ZonedDateTime.now()

        private fun parked(gdpr: Boolean = false) = ProjectPartnerReportParkedExpenditure(
            expenditure = ProjectPartnerReportExpenditureCost(
                id = 14L,
                number = 19,
                lumpSumId = 21L,
                unitCostId = 541L,
                costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts,
                gdpr = gdpr,
                investmentId = 49L,
                contractId = 28L,
                internalReferenceNumber = "irn",
                invoiceNumber = "invoice",
                invoiceDate = LocalDate.now().minusDays(1),
                dateOfPayment = LocalDate.now().plusDays(1),
                description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
                comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
                totalValueInvoice = BigDecimal.ONE,
                vat = BigDecimal.ZERO,
                numberOfUnits = BigDecimal.valueOf(77),
                pricePerUnit = BigDecimal.valueOf(44),
                declaredAmount = BigDecimal.TEN,
                currencyCode = "GBP",
                currencyConversionRate = BigDecimal.valueOf(0.84),
                declaredAmountAfterSubmission = BigDecimal.valueOf(8.4),
                attachment = JemsFileMetadata(47L, "file.xlsx", uploadTime),
                parkingMetadata = parkingMetadata,
            ),
            lumpSum = ProjectPartnerReportParkedLinked(51L, 52L, 15, true),
            lumpSumName = setOf(InputTranslation(SystemLanguage.EN, "ls-name")),
            unitCost = ProjectPartnerReportParkedLinked(61L, 62L, null, false),
            unitCostName = setOf(InputTranslation(SystemLanguage.EN, "uc-name")),
            investment = ProjectPartnerReportParkedLinked(71L, 72L, null, true),
            investmentName = "investment-name",
        )

        private fun lumpSum(): ProjectPartnerReportLumpSum {
            val result = mockk<ProjectPartnerReportLumpSum>()
            every { result.lumpSumProgrammeId } returns 52L
            every { result.orderNr } returns 15
            return result
        }
        private fun unitCost(): ProjectPartnerReportUnitCost {
            val result = mockk<ProjectPartnerReportUnitCost>()
            every { result.unitCostProgrammeId } returns -1L
            return result
        }
        private fun investment(): ProjectPartnerReportInvestment {
            val result = mockk<ProjectPartnerReportInvestment>()
            every { result.investmentId } returns 72L
            return result
        }
    }

    @MockK
    private lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence
    @MockK
    private lateinit var reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence
    @RelaxedMockK
    private lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService


    @InjectMockKs
    lateinit var interactor: GetAvailableParkedExpenditureList

    @Test
    fun getParked() {
        val reportId = 12L
        every { reportParkedExpenditurePersistence.getParkedExpendituresByIdForPartnerReport(45L, reportId) } returns
            mapOf(14L to parkingMetadata)

        every { reportExpenditurePersistence.getAvailableLumpSums(45L, reportId) } returns listOf(lumpSum())
        every { reportExpenditurePersistence.getAvailableUnitCosts(45L, reportId) } returns listOf(unitCost())
        every { reportExpenditurePersistence.getAvailableInvestments(45L, reportId) } returns listOf(investment())

        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(setOf(14L), Pageable.unpaged()) } returns
            PageImpl(listOf(parked()))

        every { sensitiveDataAuthorization.canViewPartnerSensitiveData(PARTNER_ID) } returns true

        assertThat(interactor.getParked(45L, reportId, Pageable.unpaged()).content)
            .usingRecursiveComparison()
            .isEqualTo(listOf(parked()))
    }


    @Test
    fun `getParked - check gdpr anonymization`() {
        val reportId = 12L
        every { reportParkedExpenditurePersistence.getParkedExpendituresByIdForPartnerReport(45L, reportId ) } returns
            mapOf(14L to parkingMetadata)

        every { reportExpenditurePersistence.getAvailableLumpSums(45L, reportId) } returns listOf(lumpSum())
        every { reportExpenditurePersistence.getAvailableUnitCosts(45L, reportId) } returns listOf(unitCost())
        every { reportExpenditurePersistence.getAvailableInvestments(45L, reportId) } returns listOf(investment())

        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(setOf(14L), Pageable.unpaged()) } returns
            PageImpl(listOf(parked(true)))

        every { sensitiveDataAuthorization.canViewPartnerSensitiveData(PARTNER_ID) } returns false

        val expectedParked = parked(true).also { it.expenditure.anonymizeIfSensitive() }
        assertThat(interactor.getParked(45L, reportId, Pageable.unpaged()).content)
            .usingRecursiveComparison()
            .isEqualTo(listOf(expectedParked))
    }
}
