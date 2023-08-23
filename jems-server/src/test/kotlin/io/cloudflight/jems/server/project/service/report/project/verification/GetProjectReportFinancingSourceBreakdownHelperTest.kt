package io.cloudflight.jems.server.project.service.report.project.verification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionRow
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerReportFinancialData
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.calculateSourcesAndSplits
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.cloudflight.jems.server.utils.IPA_III_FUND
import io.cloudflight.jems.server.utils.NDCI_FUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class GetProjectReportFinancingSourceBreakdownHelperTest : UnitTest() {

    companion object {

        private const val PARTNER_ID = 1L
        private const val PARTNER_REPORT_ID = 1L
        private const val EXPENDITURE_ID = 1L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)
        private const val TYPOLOGY_OF_ERROR_ID = 3L
        val availableFunds = listOf(ERDF_FUND, NDCI_FUND, IPA_III_FUND)


        private val coFinancingFromAF = listOf(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = ERDF_FUND,
                percentage = 60.00.toScaledBigDecimal(),
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = NDCI_FUND,
                percentage = 20.00.toScaledBigDecimal(),
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = IPA_III_FUND,
                percentage = 5.00.toScaledBigDecimal(),
            )
        )

        private val contributionFromAF = ProjectPartnerReportContributionOverview(
            public = ProjectPartnerReportContributionRow(
                amount = 9777.50.toScaledBigDecimal(),
                previouslyReported = BigDecimal.ZERO,
                currentlyReported = 11612.22.toScaledBigDecimal(),
                totalReportedSoFar = 11612.22.toScaledBigDecimal(),
            ),
            automaticPublic = ProjectPartnerReportContributionRow(
                amount = 5000.00.toScaledBigDecimal(),
                previouslyReported = BigDecimal.ZERO,
                currentlyReported = 5938.24.toScaledBigDecimal(),
                totalReportedSoFar = 5938.24.toScaledBigDecimal(),
            ),
            private = ProjectPartnerReportContributionRow(
                amount = 1010.00.toScaledBigDecimal(),
                previouslyReported = BigDecimal.ZERO,
                currentlyReported = 1199.52.toScaledBigDecimal(),
                totalReportedSoFar = 1199.52.toScaledBigDecimal(),
            ),
            total = ProjectPartnerReportContributionRow(
                amount = 18750.01.toScaledBigDecimal(),
                previouslyReported = BigDecimal.ZERO,
                currentlyReported = 18750.01.toScaledBigDecimal(),
                totalReportedSoFar = 18750.01.toScaledBigDecimal(),
            )
        )

        private val flatRatesFromAF = ProjectPartnerBudgetOptions(
            partnerId = PARTNER_ID,
            officeAndAdministrationOnStaffCostsFlatRate = null,
            officeAndAdministrationOnDirectCostsFlatRate = null,
            travelAndAccommodationOnStaffCostsFlatRate = null,
            staffCostsFlatRate = null,
            otherCostsOnStaffCostsFlatRate = null
        )


        private val dummyLineLumpSum = ProjectPartnerReportLumpSum(
            id = 36L,
            lumpSumProgrammeId = 945L,
            fastTrack = false,
            orderNr = 7,
            period = 4,
            cost = BigDecimal.TEN,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
        )

        private val dummyLineUnitCost = ProjectPartnerReportUnitCost(
            id = 44L,
            unitCostProgrammeId = 945L,
            projectDefined = false,
            costPerUnit = BigDecimal.ONE,
            numberOfUnits = BigDecimal.TEN,
            total = BigDecimal.TEN,
            name = setOf(InputTranslation(SystemLanguage.EN, "some unit cost 44 (or 945)")),
            category = ReportBudgetCategory.ExternalCosts,
        )

        private val procurement = ProjectPartnerReportProcurement(
            id = 265,
            reportId = PARTNER_REPORT_ID,
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

            partnerReportId = PARTNER_REPORT_ID,
            partnerReportNumber = 1,

            lumpSum = dummyLineLumpSum,
            unitCost = dummyLineUnitCost,
            gdpr = false,
            costCategory = ReportBudgetCategory.StaffCosts,
            investment = null,
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
            declaredAmount = 124999.96.toScaledBigDecimal(),
            currencyCode = "EUR",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = 124999.96.toScaledBigDecimal(),
            attachment = null,

            partOfSample = false,
            partOfSampleLocked = false,
            certifiedAmount = BigDecimal.valueOf(101),
            deductedAmount = BigDecimal.valueOf(101),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = true,
            verificationComment = "VERIFICATION COMM",

            parkingMetadata = null

        )

        private val expenditureVerification = ProjectReportVerificationExpenditureLine(
            expenditure = expenditureItem,
            partOfVerificationSample = true,
            deductedByJs = BigDecimal.ZERO,
            deductedByMa = BigDecimal.ZERO,
            amountAfterVerification = 124999.96.toScaledBigDecimal(),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = false,
            verificationComment = null
        )

        private fun financialData(projectReportId: Long): PartnerReportFinancialData {
            return PartnerReportFinancialData(
                coFinancingFromAF = coFinancingFromAF,
                contributionsFromAF = contributionFromAF,
                totalEligibleBudgetFromAF = 105250.00.toScaledBigDecimal(),
                flatRatesFromAF = flatRatesFromAF
            )
        }

    }


    @Test
    fun testCal() {
        val expectedFinancialDataBreakDownLine = FinancingSourceBreakdownLine(
            partnerReportId = 1L,
            partnerReportNumber = 1,
            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,
            fundsSorted = listOf(
                Pair(ERDF_FUND, 74999.97.toScaledBigDecimal()),
                Pair(NDCI_FUND, 24999.99.toScaledBigDecimal()),
                Pair(IPA_III_FUND, 6249.99.toScaledBigDecimal()),
            ),
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = listOf(
                FinancingSourceBreakdownSplitLine(
                    fundId = 1L,
                    value = 74999.97.toScaledBigDecimal(),
                    partnerContribution = 13235.30.toScaledBigDecimal(),
                    publicContribution = 8196.86.toScaledBigDecimal(),
                    automaticPublicContribution = 4191.69.toScaledBigDecimal(),
                    privateContribution = 846.72.toScaledBigDecimal(),
                    total = 88235.27.toScaledBigDecimal(),
                ),
                FinancingSourceBreakdownSplitLine(
                    fundId = 5L,
                    value = 24999.99.toScaledBigDecimal(),
                    partnerContribution = 4411.77.toScaledBigDecimal(),
                    publicContribution = 2732.28.toScaledBigDecimal(),
                    automaticPublicContribution = 1397.23.toScaledBigDecimal(),
                    privateContribution = 282.24.toScaledBigDecimal(),
                    total = 29411.76.toScaledBigDecimal(),
                ),
                FinancingSourceBreakdownSplitLine(
                    fundId = 4L,
                    value = 6249.99.toScaledBigDecimal(),
                    partnerContribution = 1102.94.toScaledBigDecimal(),
                    publicContribution = 683.07.toScaledBigDecimal(),
                    automaticPublicContribution = 349.30.toScaledBigDecimal(),
                    privateContribution = 70.56.toScaledBigDecimal(),
                    total = 7352.93.toScaledBigDecimal(),
                ),
            )
        )

        assertThat(
            calculateSourcesAndSplits(verification = listOf(expenditureVerification),
                availableFunds = availableFunds,
                partnerReportFinancialDataResolver = { financialData(it) }).first()
        ).isEqualTo(expectedFinancialDataBreakDownLine)
    }
}
