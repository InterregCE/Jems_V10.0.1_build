package io.cloudflight.jems.server.project.controller.report.project.financialOverview

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.BudgetCostsCalculationResultFullDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCostCategoryBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateInvestmentBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateInvestmentBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateLumpSumBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateLumpSumBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateUnitCostBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateUnitCostBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.FinancingSourceBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.FinancingSourceBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.FinancingSourceBreakdownSplitLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.FinancingSourceFundDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.PerPartnerCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.PerPartnerCostCategoryBreakdownLineDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdownInteractor.GetReportCertificateInvestmentsBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.GetReportCertificateCoFinancingBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown.GetReportCertificateCostCategoryBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown.GetReportCertificateLumpSumBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown.GetReportCertificateUnitCostsBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner.GetPerPartnerCostCategoryBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.GetProjectReportFinancingSourceBreakdownInteractor
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectProjectReportFinancialOverviewControllerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 526L
        private const val REPORT_ID = 606L

        private val dummyLineCoFin = CertificateCoFinancingBreakdownLine(
            fundId = null,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
            currentReport = BigDecimal.ZERO,
            previouslyVerified = BigDecimal.valueOf(150L),
            currentVerified = BigDecimal.valueOf(15L),
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val dummyCertificateCoFinancing = CertificateCoFinancingBreakdown(
            funds = listOf(
                dummyLineCoFin.copy(fundId = 7L),
                dummyLineCoFin,
            ),
            partnerContribution = dummyLineCoFin,
            publicContribution = dummyLineCoFin,
            automaticPublicContribution = dummyLineCoFin,
            privateContribution = dummyLineCoFin,
            total = dummyLineCoFin,
        )


        private val expectedDummyLineCoFin = CertificateCoFinancingBreakdownLineDTO(
            fundId = null,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
            currentReport = BigDecimal.ZERO,
            previouslyVerified = BigDecimal.valueOf(150L),
            currentVerified = BigDecimal.valueOf(15L),
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val expectedDummyCertificateCoFinancing = CertificateCoFinancingBreakdownDTO(
            funds = listOf(
                expectedDummyLineCoFin.copy(fundId = 7L),
                expectedDummyLineCoFin,
            ),
            partnerContribution = expectedDummyLineCoFin,
            publicContribution = expectedDummyLineCoFin,
            automaticPublicContribution = expectedDummyLineCoFin,
            privateContribution = expectedDummyLineCoFin,
            total = expectedDummyLineCoFin,
        )

        private val dummyCostCategoryLine = CertificateCostCategoryBreakdownLine(
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.ONE,
        )

        private val dummyCostCategory = CertificateCostCategoryBreakdown(
            staff = dummyCostCategoryLine,
            office = dummyCostCategoryLine,
            travel = dummyCostCategoryLine,
            external = dummyCostCategoryLine,
            equipment = dummyCostCategoryLine,
            infrastructure = dummyCostCategoryLine,
            other = dummyCostCategoryLine,
            lumpSum = dummyCostCategoryLine,
            unitCost = dummyCostCategoryLine,
            total = dummyCostCategoryLine,
        )

        private val expectedDummyCostCategoryLine = CertificateCostCategoryBreakdownLineDTO(
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.ONE,
        )

        private val expectedDummyCostCategory = CertificateCostCategoryBreakdownDTO(
            staff = expectedDummyCostCategoryLine,
            office = expectedDummyCostCategoryLine,
            travel = expectedDummyCostCategoryLine,
            external = expectedDummyCostCategoryLine,
            equipment = expectedDummyCostCategoryLine,
            infrastructure = expectedDummyCostCategoryLine,
            other = expectedDummyCostCategoryLine,
            lumpSum = expectedDummyCostCategoryLine,
            unitCost = expectedDummyCostCategoryLine,
            total = expectedDummyCostCategoryLine,
        )

        private val dummyCostCategoryPartner = PerPartnerCostCategoryBreakdown(
            partners = listOf(
                PerPartnerCostCategoryBreakdownLine(
                    partnerId = 15L,
                    partnerNumber = 5,
                    partnerAbbreviation = "abbr",
                    partnerRole = ProjectPartnerRole.PARTNER,
                    country = "country",
                    officeAndAdministrationOnStaffCostsFlatRate = null,
                    officeAndAdministrationOnDirectCostsFlatRate = 12,
                    travelAndAccommodationOnStaffCostsFlatRate = 18,
                    staffCostsFlatRate = 79,
                    otherCostsOnStaffCostsFlatRate = 24,
                    current = BudgetCostsCalculationResultFull(
                        staff = BigDecimal.valueOf(11L),
                        office = BigDecimal.valueOf(12L),
                        travel = BigDecimal.valueOf(13L),
                        external = BigDecimal.valueOf(14L),
                        equipment = BigDecimal.valueOf(15L),
                        infrastructure = BigDecimal.valueOf(16L),
                        other = BigDecimal.valueOf(17L),
                        lumpSum = BigDecimal.valueOf(18L),
                        unitCost = BigDecimal.valueOf(19L),
                        sum = BigDecimal.valueOf(20L),
                    ),
                    deduction = BudgetCostsCalculationResultFull(
                        staff = BigDecimal.valueOf(21L),
                        office = BigDecimal.valueOf(22L),
                        travel = BigDecimal.valueOf(23L),
                        external = BigDecimal.valueOf(24L),
                        equipment = BigDecimal.valueOf(25L),
                        infrastructure = BigDecimal.valueOf(26L),
                        other = BigDecimal.valueOf(27L),
                        lumpSum = BigDecimal.valueOf(28L),
                        unitCost = BigDecimal.valueOf(29L),
                        sum = BigDecimal.valueOf(30L),
                    ),
                )
            ),
            totalCurrent = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(31L),
                office = BigDecimal.valueOf(32L),
                travel = BigDecimal.valueOf(33L),
                external = BigDecimal.valueOf(34L),
                equipment = BigDecimal.valueOf(35L),
                infrastructure = BigDecimal.valueOf(36L),
                other = BigDecimal.valueOf(37L),
                lumpSum = BigDecimal.valueOf(38L),
                unitCost = BigDecimal.valueOf(39L),
                sum = BigDecimal.valueOf(40L),
            ),
            totalDeduction = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(41L),
                office = BigDecimal.valueOf(42L),
                travel = BigDecimal.valueOf(43L),
                external = BigDecimal.valueOf(44L),
                equipment = BigDecimal.valueOf(45L),
                infrastructure = BigDecimal.valueOf(46L),
                other = BigDecimal.valueOf(47L),
                lumpSum = BigDecimal.valueOf(48L),
                unitCost = BigDecimal.valueOf(49L),
                sum = BigDecimal.valueOf(50L),
            ),
        )

        private val expectedCostCategoryPartner = PerPartnerCostCategoryBreakdownDTO(
            partners = listOf(
                PerPartnerCostCategoryBreakdownLineDTO(
                    partnerId = 15L,
                    partnerNumber = 5,
                    partnerAbbreviation = "abbr",
                    partnerRole = ProjectPartnerRoleDTO.PARTNER,
                    country = "country",
                    officeAndAdministrationOnStaffCostsFlatRate = null,
                    officeAndAdministrationOnDirectCostsFlatRate = 12,
                    travelAndAccommodationOnStaffCostsFlatRate = 18,
                    staffCostsFlatRate = 79,
                    otherCostsOnStaffCostsFlatRate = 24,
                    current = BudgetCostsCalculationResultFullDTO(
                        staff = BigDecimal.valueOf(11L),
                        office = BigDecimal.valueOf(12L),
                        travel = BigDecimal.valueOf(13L),
                        external = BigDecimal.valueOf(14L),
                        equipment = BigDecimal.valueOf(15L),
                        infrastructure = BigDecimal.valueOf(16L),
                        other = BigDecimal.valueOf(17L),
                        lumpSum = BigDecimal.valueOf(18L),
                        unitCost = BigDecimal.valueOf(19L),
                        sum = BigDecimal.valueOf(20L),
                    ),
                    deduction = BudgetCostsCalculationResultFullDTO(
                        staff = BigDecimal.valueOf(21L),
                        office = BigDecimal.valueOf(22L),
                        travel = BigDecimal.valueOf(23L),
                        external = BigDecimal.valueOf(24L),
                        equipment = BigDecimal.valueOf(25L),
                        infrastructure = BigDecimal.valueOf(26L),
                        other = BigDecimal.valueOf(27L),
                        lumpSum = BigDecimal.valueOf(28L),
                        unitCost = BigDecimal.valueOf(29L),
                        sum = BigDecimal.valueOf(30L),
                    ),
                )
            ),
            totalCurrent = BudgetCostsCalculationResultFullDTO(
                staff = BigDecimal.valueOf(31L),
                office = BigDecimal.valueOf(32L),
                travel = BigDecimal.valueOf(33L),
                external = BigDecimal.valueOf(34L),
                equipment = BigDecimal.valueOf(35L),
                infrastructure = BigDecimal.valueOf(36L),
                other = BigDecimal.valueOf(37L),
                lumpSum = BigDecimal.valueOf(38L),
                unitCost = BigDecimal.valueOf(39L),
                sum = BigDecimal.valueOf(40L),
            ),
            totalDeduction = BudgetCostsCalculationResultFullDTO(
                staff = BigDecimal.valueOf(41L),
                office = BigDecimal.valueOf(42L),
                travel = BigDecimal.valueOf(43L),
                external = BigDecimal.valueOf(44L),
                equipment = BigDecimal.valueOf(45L),
                infrastructure = BigDecimal.valueOf(46L),
                other = BigDecimal.valueOf(47L),
                lumpSum = BigDecimal.valueOf(48L),
                unitCost = BigDecimal.valueOf(49L),
                sum = BigDecimal.valueOf(50L),
            ),
        )

        private val dummyLumpSumLine = CertificateLumpSumBreakdownLine(
            reportLumpSumId = 1L,
            lumpSumId = 1L,
            name = setOf(),
            period = 0,
            orderNr = 0,
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
            previouslyPaid = BigDecimal.valueOf(7)
        )

        private val dummyLumpSum = CertificateLumpSumBreakdown(
            lumpSums = listOf(dummyLumpSumLine),
            total = dummyLumpSumLine,
        )

        private val expectedDummyLumpSumLine = CertificateLumpSumBreakdownLineDTO(
            reportLumpSumId = 1L,
            lumpSumId = 1L,
            name = setOf(),
            period = 0,
            orderNr = 0,
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
            previouslyPaid = BigDecimal.valueOf(7)
        )

        private val expectedDummyLumpSum = CertificateLumpSumBreakdownDTO(
            lumpSums = listOf(expectedDummyLumpSumLine),
            total = expectedDummyLumpSumLine,
        )

        private val dummyUnitCostLine = CertificateUnitCostBreakdownLine(
            reportUnitCostId = 1L,
            unitCostId = 1L,
            name = setOf(),
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
        )

        private val dummyUnitCost = CertificateUnitCostBreakdown(
            unitCosts = listOf(dummyUnitCostLine),
            total = dummyUnitCostLine,
        )

        private val expectedDummyUnitCostLine = CertificateUnitCostBreakdownLineDTO(
            reportUnitCostId = 1L,
            unitCostId = 1L,
            name = setOf(),
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
        )

        private val expectedDummyUnitCost = CertificateUnitCostBreakdownDTO(
            unitCosts = listOf(expectedDummyUnitCostLine),
            total = expectedDummyUnitCostLine,
        )

        private val dummyInvestmentLine = CertificateInvestmentBreakdownLine(
            reportInvestmentId = 1L,
            investmentId = 1L,
            investmentNumber = 1,
            workPackageNumber = 1,
            deactivated = false,
            title = setOf(),
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
        )

        private val dummyInvestment = CertificateInvestmentBreakdown(
            investments = listOf(dummyInvestmentLine),
            total = dummyInvestmentLine,
        )

        private val expectedDummyInvestmentLine = CertificateInvestmentBreakdownLineDTO(
            reportInvestmentId = 1L,
            investmentId = 1L,
            investmentNumber = 1,
            workPackageNumber = 1,
            deactivated = false,
            title = setOf(),
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
        )

        private val expectedDummyInvestment = CertificateInvestmentBreakdownDTO(
            investments = listOf(expectedDummyInvestmentLine),
            total = expectedDummyInvestmentLine,
        )


        val ERDF = ProgrammeFund(
            id = 1L, type = ProgrammeFundType.ERDF, selected = true,
            abbreviation = setOf(
                InputTranslation(
                    SystemLanguage.EN, "EN ERDF"
                ),
                InputTranslation(SystemLanguage.SK, "SK ERDF")
            ),
            description = setOf(
                InputTranslation(SystemLanguage.EN, "EN desc"),
                InputTranslation(SystemLanguage.SK, "SK desc")
            )
        )
        val NDCI = ProgrammeFund(id = 5L, type = ProgrammeFundType.NDICI, selected = true,
            abbreviation = setOf(
                InputTranslation(
                    SystemLanguage.EN, "EN NDCI"
                ),
                InputTranslation(SystemLanguage.SK, "SK NDCI")
            ),
            description = setOf(
                InputTranslation(SystemLanguage.EN, "EN desc"),
                InputTranslation(SystemLanguage.SK, "SK desc")
            ))
        val IPA_III = ProgrammeFund(id = 4L, type = ProgrammeFundType.IPA_III, selected = true,
            abbreviation = setOf(
                InputTranslation(
                    SystemLanguage.EN, "EN IPA_III"
                ),
                InputTranslation(SystemLanguage.SK, "SK IPA_III")
            ),
            description = setOf(
                InputTranslation(SystemLanguage.EN, "EN desc"),
                InputTranslation(SystemLanguage.SK, "SK desc")
            ))

        private val fundsSorted = listOf(
            Pair(ERDF, 74999.97.toScaledBigDecimal()),
            Pair(NDCI, 24999.99.toScaledBigDecimal()),
            Pair(IPA_III, 6249.99.toScaledBigDecimal())
        )

        private val splits = listOf(
            FinancingSourceBreakdownSplitLine(
                fundId = 1L,
                value = 74999.97.toScaledBigDecimal(),
                partnerContribution = 13235.30.toScaledBigDecimal(),
                publicContribution = 8196.86.toScaledBigDecimal(),
                automaticPublicContribution = 4191.69.toScaledBigDecimal(),
                privateContribution = 846.72.toScaledBigDecimal(),
                total = 88235.27.toScaledBigDecimal()
            ),
            FinancingSourceBreakdownSplitLine(
                fundId = 5L,
                value = 24999.99.toScaledBigDecimal(),
                partnerContribution = 4411.77.toScaledBigDecimal(),
                publicContribution = 2732.28.toScaledBigDecimal(),
                automaticPublicContribution = 1397.23.toScaledBigDecimal(),
                privateContribution = 282.24.toScaledBigDecimal(),
                total = 29411.76.toScaledBigDecimal()
            ),
            FinancingSourceBreakdownSplitLine(
                fundId = 4L,
                value = 6249.99.toScaledBigDecimal(),
                partnerContribution = 1102.94.toScaledBigDecimal(),
                publicContribution = 683.07.toScaledBigDecimal(),
                automaticPublicContribution = 349.30.toScaledBigDecimal(),
                privateContribution = 70.56.toScaledBigDecimal(),
                total = 7352.93.toScaledBigDecimal()
            ),
        )

        private val financingSource = FinancingSourceBreakdownLine(
            partnerReportId = 1L,
            partnerReportNumber = 1,
            partnerId = 1L,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,
            fundsSorted = fundsSorted,
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = splits
        )

        private val financingSourcesTotal = FinancingSourceBreakdownLine(
            partnerReportId = null,
            partnerReportNumber = null,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = fundsSorted,
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = emptyList()
        )

        private val financingSourceBreakdown = FinancingSourceBreakdown(
            sources = listOf(financingSource),
            total = financingSourcesTotal
        )

    }

    @MockK
    private lateinit var getReportCertificateCoFinancingBreakdown: GetReportCertificateCoFinancingBreakdownInteractor

    @MockK
    private lateinit var getReportCertificateCostCategoryBreakdown: GetReportCertificateCostCategoryBreakdownInteractor

    @MockK
    private lateinit var getPerPartnerCostCategoryBreakdown: GetPerPartnerCostCategoryBreakdownInteractor

    @MockK
    private lateinit var getReportCertificateLumpSumBreakdown: GetReportCertificateLumpSumBreakdownInteractor

    @MockK
    private lateinit var getReportCertificateUnitCostBreakdown: GetReportCertificateUnitCostsBreakdownInteractor

    @MockK
    private lateinit var getReportCertificateInvestmentsBreakdown: GetReportCertificateInvestmentsBreakdownInteractor

    @MockK
    private lateinit var getProjectReportFinancingSourceBreakdown: GetProjectReportFinancingSourceBreakdownInteractor

    @InjectMockKs
    private lateinit var controller: ProjectReportFinancialOverviewController

    @BeforeEach
    fun resetMocks() {
        clearMocks(
            getReportCertificateCoFinancingBreakdown,
            getReportCertificateCostCategoryBreakdown,
            getPerPartnerCostCategoryBreakdown,
            getReportCertificateLumpSumBreakdown,
            getReportCertificateUnitCostBreakdown,
        )
    }

    @Test
    fun getCoFinancingBreakdown() {
        every { getReportCertificateCoFinancingBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns
            dummyCertificateCoFinancing
        assertThat(controller.getCoFinancingBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyCertificateCoFinancing)
    }

    @Test
    fun getCostCategoriesBreakdown() {
        every { getReportCertificateCostCategoryBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns
            dummyCostCategory
        assertThat(controller.getCostCategoriesBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyCostCategory)
    }

    @Test
    fun getLumpSumsBreakdown() {
        every { getReportCertificateLumpSumBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns
            dummyLumpSum
        assertThat(controller.getLumpSumsBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyLumpSum)
    }

    @Test
    fun getCostCategoriesPerPartnerBreakdown() {
        every { getPerPartnerCostCategoryBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns
            dummyCostCategoryPartner
        assertThat(controller.getCostCategoriesPerPartnerBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expectedCostCategoryPartner)
    }

    @Test
    fun getUnitCostsBreakdown() {
        every { getReportCertificateUnitCostBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns
            dummyUnitCost
        assertThat(controller.getUnitCostsBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyUnitCost)
    }

    @Test
    fun getInvestmentsBreakdown() {
        every { getReportCertificateInvestmentsBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns
            dummyInvestment
        assertThat(controller.getInvestmentsBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyInvestment)
    }

    @Test
    fun getFinancingSourceBreakdown() {
        val funds = listOf(
            FinancingSourceFundDTO(
                id = 1L,
                type = ProgrammeFundTypeDTO.ERDF,
                abbreviation = ERDF.abbreviation,
                amount = 74999.97.toScaledBigDecimal()
            ),
            FinancingSourceFundDTO(
                id = 5L,
                type = ProgrammeFundTypeDTO.NDICI,
                abbreviation = NDCI.abbreviation,
                amount = 24999.99.toScaledBigDecimal()
            ),
            FinancingSourceFundDTO(
                id = 4L,
                type = ProgrammeFundTypeDTO.IPA_III,
                abbreviation = IPA_III.abbreviation,
                amount = 6249.99.toScaledBigDecimal()
            )
        )

        val splits = listOf(
            FinancingSourceBreakdownSplitLineDTO(
                fundId = 1L,
                value = 74999.97.toScaledBigDecimal(),
                partnerContribution = 13235.30.toScaledBigDecimal(),
                publicContribution = 8196.86.toScaledBigDecimal(),
                automaticPublicContribution = 4191.69.toScaledBigDecimal(),
                privateContribution = 846.72.toScaledBigDecimal(),
                total = 88235.27.toScaledBigDecimal()
            ),
            FinancingSourceBreakdownSplitLineDTO(
                fundId = 5L,
                value = 24999.99.toScaledBigDecimal(),
                partnerContribution = 4411.77.toScaledBigDecimal(),
                publicContribution = 2732.28.toScaledBigDecimal(),
                automaticPublicContribution = 1397.23.toScaledBigDecimal(),
                privateContribution = 282.24.toScaledBigDecimal(),
                total = 29411.76.toScaledBigDecimal()
            ),
            FinancingSourceBreakdownSplitLineDTO(
                fundId = 4L,
                value = 6249.99.toScaledBigDecimal(),
                partnerContribution = 1102.94.toScaledBigDecimal(),
                publicContribution = 683.07.toScaledBigDecimal(),
                automaticPublicContribution = 349.30.toScaledBigDecimal(),
                privateContribution = 70.56.toScaledBigDecimal(),
                total = 7352.93.toScaledBigDecimal(),
            )
        )

        val financingSourceLine = FinancingSourceBreakdownLineDTO(
            partnerReportId = 1L,
            partnerReportNumber = 1,
            partnerId = 1L,
            partnerRole = ProjectPartnerRoleDTO.LEAD_PARTNER,
            partnerNumber = 1,
            fundsSorted = funds,
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = splits
        )

        val financingSourcesTotal = FinancingSourceBreakdownLineDTO(
            partnerReportId = null,
            partnerReportNumber = null,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = funds,
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = emptyList()
        )

        val expected = FinancingSourceBreakdownDTO(
            sources = listOf(financingSourceLine),
            total = financingSourcesTotal
        )

        every { getProjectReportFinancingSourceBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns financingSourceBreakdown
        assertThat(controller.getFinancingSourceBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expected)
    }

}
