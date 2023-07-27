package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectExpenditureVerification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdate
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditure.UpdateProjectReportVerificationExpenditure
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class UpdateProjectReportVerificationExpenditureTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val PROJECT_REPORT_ID = 20L
        private const val TYPOLOGY_OF_ERROR_ID = 3L
        private const val EXPENDITURE_ID = 1L
        private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

        private const val REPORT_ID = 101L
        private const val PARTNER_ID = 10L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)

        private val parkingMetadata = ExpenditureParkingMetadata(
            reportOfOriginId = 70L,
            reportProjectOfOriginId = null,
            reportOfOriginNumber = 5,
            originalExpenditureNumber = 3
        )


        private val dummyLineLumpSum = ExpenditureLumpSumBreakdownLine(
            reportLumpSumId = 36L,
            lumpSumId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
            period = 4,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
            currentReport = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.valueOf(1000),
            currentReportReIncluded = BigDecimal.valueOf(100),
            previouslyValidated = BigDecimal.valueOf(6)

        )

        private val dummyInvestmentLine = ExpenditureInvestmentBreakdownLine(
            reportInvestmentId = 845L,
            investmentId = 22L,
            investmentNumber = 1,
            workPackageNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            currentReport = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.valueOf(100),
            currentReportReIncluded = BigDecimal.ZERO,
            deactivated = false,
            previouslyValidated = BigDecimal.valueOf(7)
        )


        private val dummyLineUnitCost = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = 44L,
            unitCostId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some unit cost 44 (or 945)")),
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            currentReport = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO,
            previouslyValidated = BigDecimal.valueOf(8)
        )

        private val procurement = ProjectPartnerReportProcurement(
            id = 265,
            reportId = REPORT_ID,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName 265",
            referenceNumber = "referenceNumber 100",
            contractDate = NEXT_WEEK,
            contractType = "contractType 265",
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName 265",
            vatNumber = "vat number 265",
            comment = "comment 265",
        )

        private val expenditureItem = ProjectPartnerReportExpenditureItem(
            id = EXPENDITURE_ID,
            number = 1,

            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,

            partnerReportId = REPORT_ID,
            partnerReportNumber = 1,

            lumpSum = dummyLineLumpSum,
            unitCost = dummyLineUnitCost,
            gdpr = false,
            costCategory = ReportBudgetCategory.EquipmentCosts,
            investment = dummyInvestmentLine,
            contract = procurement,
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1),
            dateOfPayment = LocalDate.of(2022, 2, 1),
            description = emptySet(),
            comment = emptySet(),
            totalValueInvoice = BigDecimal.valueOf(22),
            vat = BigDecimal.valueOf(18.0),
            numberOfUnits = BigDecimal.ZERO,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.valueOf(31.2),
            currencyCode = "CZK",
            currencyConversionRate = BigDecimal.valueOf(24),
            declaredAmountAfterSubmission = BigDecimal.valueOf(1.3),
            attachment = JemsFileMetadata(500L, "file.txt", UPLOADED),

            partOfSample = false,
            partOfSampleLocked = false,
            certifiedAmount = BigDecimal.valueOf(101),
            deductedAmount = BigDecimal.valueOf(101),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = true,
            verificationComment = "VERIFICATION COMM",

            parkingMetadata = parkingMetadata
        )

        private val expenditureLine = ProjectReportVerificationExpenditureLine(
            expenditure = expenditureItem,
            partOfVerificationSample = false,
            deductedByJs = BigDecimal.valueOf(100),
            deductedByMa = BigDecimal.valueOf(200),
            amountAfterVerification = BigDecimal.valueOf(300),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = false,
            verificationComment = "VERIFICATION COMM"
        )

        private val expectedExpenditureLine = ProjectReportVerificationExpenditureLine(
            expenditure = expenditureItem,
            partOfVerificationSample = false,
            deductedByJs = BigDecimal.valueOf(100),
            deductedByMa = BigDecimal.valueOf(200),
            amountAfterVerification = BigDecimal.valueOf(300),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = false,
            verificationComment = "NEW VERIFICATION COMM"
        )

        val expendituresToUpdate = listOf(
            ProjectReportVerificationExpenditureLineUpdate(
                expenditureId = EXPENDITURE_ID,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(150),
                deductedByMa = BigDecimal.valueOf(250),
                typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
                parked = false,
                verificationComment = "NEW VERIFICATION COMM"
            )
        )

    }

    @MockK
    lateinit var projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence

    @MockK
    lateinit var partnerReportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence

    @MockK
    lateinit var typologyPersistence: ProgrammeTypologyErrorsPersistence

    @InjectMockKs
    lateinit var updateProjectReportVerificationExpenditure: UpdateProjectReportVerificationExpenditure

    @Test
    fun updateExpenditureVerification() {

        every {
            projectReportExpenditureVerificationPersistence.getProjectReportExpenditureVerification(
                PROJECT_REPORT_ID
            )
        } returns listOf(expenditureLine)
        every {
            projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerification(
                PROJECT_REPORT_ID,
                any()
            )
        } returns listOf(expectedExpenditureLine)
        every { partnerReportParkedExpenditurePersistence.parkExpenditures(any()) } returns Unit
        every { partnerReportParkedExpenditurePersistence.unParkExpenditures(any()) } returns Unit

        every { typologyPersistence.getAllTypologyErrors() } returns listOf(TypologyErrors(PROJECT_ID, "Typology 1"))

        assertThat(
            updateProjectReportVerificationExpenditure.updateExpenditureVerification(
                PROJECT_REPORT_ID,
                expendituresToUpdate
            )
        ).isEqualTo(
            listOf(expectedExpenditureLine)
        )
    }
}
