package io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetPerPartnerCostCategoryBreakdownTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L

        private val current = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(11L),
            office = BigDecimal.valueOf(12L),
            travel = BigDecimal.valueOf(13L),
            external = BigDecimal.valueOf(14L),
            equipment = BigDecimal.valueOf(15L),
            infrastructure = BigDecimal.valueOf(16L),
            other = BigDecimal.valueOf(17L),
            lumpSum = BigDecimal.valueOf(18L),
            unitCost = BigDecimal.valueOf(19L),
            sum = BigDecimal.valueOf(135L),
        )
        private val afterControl = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(21L),
            office = BigDecimal.valueOf(22L),
            travel = BigDecimal.valueOf(23L),
            external = BigDecimal.valueOf(24L),
            equipment = BigDecimal.valueOf(25L),
            infrastructure = BigDecimal.valueOf(26L),
            other = BigDecimal.valueOf(27L),
            lumpSum = BigDecimal.valueOf(28L),
            unitCost = BigDecimal.valueOf(29L),
            sum = BigDecimal.valueOf(225L),
        )

        private val perPartner_15 = PerPartnerCostCategoryBreakdownLine(
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
            current = current,
            afterControl = afterControl,
        )
        private val perPartner_16 = PerPartnerCostCategoryBreakdownLine(
            partnerId = 16L,
            partnerNumber = 6,
            partnerAbbreviation = "abbr",
            partnerRole = ProjectPartnerRole.PARTNER,
            country = "country",
            officeAndAdministrationOnStaffCostsFlatRate = 42,
            officeAndAdministrationOnDirectCostsFlatRate = null,
            travelAndAccommodationOnStaffCostsFlatRate = null,
            staffCostsFlatRate = 12,
            otherCostsOnStaffCostsFlatRate = 15,
            current = current,
            afterControl = afterControl,
        )

        private val expectedPerPartner = PerPartnerCostCategoryBreakdown(
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
                    current = current.copy(),
                    afterControl = afterControl.copy(),
                ),
                PerPartnerCostCategoryBreakdownLine(
                    partnerId = 16L,
                    partnerNumber = 6,
                    partnerAbbreviation = "abbr",
                    partnerRole = ProjectPartnerRole.PARTNER,
                    country = "country",
                    officeAndAdministrationOnStaffCostsFlatRate = 42,
                    officeAndAdministrationOnDirectCostsFlatRate = null,
                    travelAndAccommodationOnStaffCostsFlatRate = null,
                    staffCostsFlatRate = 12,
                    otherCostsOnStaffCostsFlatRate = 15,
                    current = current.copy(),
                    afterControl = afterControl.copy(),
                ),
            ),
            totalCurrent = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(22L),
                office = BigDecimal.valueOf(24L),
                travel = BigDecimal.valueOf(26L),
                external = BigDecimal.valueOf(28L),
                equipment = BigDecimal.valueOf(30L),
                infrastructure = BigDecimal.valueOf(32L),
                other = BigDecimal.valueOf(34L),
                lumpSum = BigDecimal.valueOf(36L),
                unitCost = BigDecimal.valueOf(38L),
                sum = BigDecimal.valueOf(270L),
            ),
            totalAfterControl = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(42L),
                office = BigDecimal.valueOf(44L),
                travel = BigDecimal.valueOf(46L),
                external = BigDecimal.valueOf(48L),
                equipment = BigDecimal.valueOf(50L),
                infrastructure = BigDecimal.valueOf(52L),
                other = BigDecimal.valueOf(54L),
                lumpSum = BigDecimal.valueOf(56L),
                unitCost = BigDecimal.valueOf(58L),
                sum = BigDecimal.valueOf(450L),
            ),
        )

        private val expectedPerPartnerEmpty = PerPartnerCostCategoryBreakdown(
            partners = emptyList(),
            totalCurrent = BudgetCostsCalculationResultFull(
                staff = BigDecimal.ZERO,
                office = BigDecimal.ZERO,
                travel = BigDecimal.ZERO,
                external = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructure = BigDecimal.ZERO,
                other = BigDecimal.ZERO,
                lumpSum = BigDecimal.ZERO,
                unitCost = BigDecimal.ZERO,
                sum = BigDecimal.ZERO,
            ),
            totalAfterControl = BudgetCostsCalculationResultFull(
                staff = BigDecimal.ZERO,
                office = BigDecimal.ZERO,
                travel = BigDecimal.ZERO,
                external = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructure = BigDecimal.ZERO,
                other = BigDecimal.ZERO,
                lumpSum = BigDecimal.ZERO,
                unitCost = BigDecimal.ZERO,
                sum = BigDecimal.ZERO,
            ),
        )
    }

    @MockK
    private lateinit var reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence

    @InjectMockKs
    lateinit var interactor: GetPerPartnerCostCategoryBreakdown

    @BeforeEach
    fun reset() {
        clearMocks(reportCertificateCostCategoryPersistence)
    }

    @Test
    fun get() {
        every { reportCertificateCostCategoryPersistence.getCostCategoriesPerPartner(PROJECT_ID, REPORT_ID) } returns
            listOf(perPartner_15, perPartner_16)
        assertThat(interactor.get(PROJECT_ID, REPORT_ID)).isEqualTo(expectedPerPartner)
    }

    @Test
    fun `get - empty`() {
        every { reportCertificateCostCategoryPersistence.getCostCategoriesPerPartner(PROJECT_ID, REPORT_ID) } returns emptyList()
        assertThat(interactor.get(PROJECT_ID, REPORT_ID)).isEqualTo(expectedPerPartnerEmpty)
    }

}
