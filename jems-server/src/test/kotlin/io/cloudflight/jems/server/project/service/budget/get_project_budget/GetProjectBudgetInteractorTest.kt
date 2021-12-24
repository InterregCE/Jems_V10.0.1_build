package io.cloudflight.jems.server.project.service.budget.get_project_budget

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetProjectBudgetInteractorTest : UnitTest() {

    private val partner1Id = 1L
    private val partner2Id = 2L
    private val partner1 = ProjectPartnerSummary(
        id = partner1Id,
        active = true,
        abbreviation = "PP 2",
        role = ProjectPartnerRole.PARTNER,
        sortNumber = 2,
        country = "SK",
        region = "SK000"
    )
    private val partner2 = ProjectPartnerSummary(
        id = partner2Id,
        active = true,
        abbreviation = "LP 1",
        role = ProjectPartnerRole.LEAD_PARTNER,
        sortNumber = 1
    )

    private val partner1Options = ProjectPartnerBudgetOptions(
        partnerId = partner1.id!!,
        officeAndAdministrationOnStaffCostsFlatRate = 10,
        officeAndAdministrationOnDirectCostsFlatRate = null,
        staffCostsFlatRate = 10,
        travelAndAccommodationOnStaffCostsFlatRate = 15,
    )

    private fun budget(partnerId: Long, sum: Double) = ProjectPartnerCost(
        partnerId = partnerId,
        sum = sum.toScaledBigDecimal()
    )

    @MockK
    lateinit var persistence: ProjectBudgetPersistence

    @MockK
    lateinit var optionOptionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @MockK
    lateinit var lumpSumPersistence: ProjectLumpSumPersistence

    @MockK
    lateinit var budgetCalculator: BudgetCostsCalculatorService

    @RelaxedMockK
    lateinit var auditService: AuditService

    @InjectMockKs
    private lateinit var getProjectBudget: GetProjectBudget


    @Test
    fun getBudget() {
        every { persistence.getPartnersForProjectId(1) } returns listOf(partner1, partner2)
        every { optionOptionsPersistence.getBudgetOptions(setOf(partner1Id, partner2Id), 1L) } returns listOf(
            partner1Options
        )
        every { persistence.getStaffCosts(setOf(partner2Id), 1L) } returns listOf(budget(partner1Id, 50.0))
        every { persistence.getTravelCosts(setOf(partner2Id), 1L) } returns listOf(
            budget(partner1Id, 800.0),
            budget(partner2Id, 100.0)
        )
        every { persistence.getExternalCosts(setOf(partner1Id, partner2Id), 1L) } returns listOf(budget(partner2Id, 1000.0))
        every { persistence.getEquipmentCosts(setOf(partner1Id, partner2Id), 1L) } returns emptyList()
        every { persistence.getInfrastructureCosts(setOf(partner1Id, partner2Id), 1L) } returns listOf(
            budget(
                partner1Id,
                300.0
            ), budget(partner2Id, 300.0)
        )

        every {persistence.getUnitCostsPerPartner(setOf(partner1Id, partner2Id), 1L) } returns mapOf(
            partner1Id to 25.0.toScaledBigDecimal()
        )
        every { persistence.getLumpSumContributionPerPartner(setOf(partner1Id, partner2Id), 1L) } returns mapOf(
            partner1Id to BigDecimal.ONE,
            partner2Id to BigDecimal.TEN,
        )

        every {
            budgetCalculator.calculateCosts(
                partner1Options,
                25.0.toScaledBigDecimal(),
                BigDecimal.ONE,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                300.0.toScaledBigDecimal(),
                800.0.toScaledBigDecimal(),
                50.0.toScaledBigDecimal()
            )
        } returns BudgetCostsCalculationResult(
            1200.0.toScaledBigDecimal(),
            165.toScaledBigDecimal(),
            120.toScaledBigDecimal(),
            BigDecimal.ZERO,
            1811.toScaledBigDecimal()
        )

        every {
            budgetCalculator.calculateCosts(
                null,
                BigDecimal.ZERO,
                BigDecimal.TEN,
                1000.0.toScaledBigDecimal(),
                BigDecimal.ZERO,
                300.0.toScaledBigDecimal(),
                100.0.toScaledBigDecimal(),
                BigDecimal.ZERO
            )
        } returns BudgetCostsCalculationResult(
            BigDecimal.ZERO, 100.0.toScaledBigDecimal(),
            BigDecimal.ZERO, BigDecimal.ZERO,
            1410.toScaledBigDecimal()
        )

        assertThat(getProjectBudget.getBudget(1))
            .containsExactlyInAnyOrder(
                PartnerBudget(
                    partner = partner2,
                    staffCosts = BigDecimal.ZERO,
                    travelCosts = 100.toScaledBigDecimal(),
                    externalCosts = 1000.toScaledBigDecimal(),
                    equipmentCosts = BigDecimal.ZERO,
                    infrastructureCosts = 300.toScaledBigDecimal(),
                    officeAndAdministrationCosts = BigDecimal.ZERO,
                    otherCosts = BigDecimal.ZERO,
                    lumpSumContribution = BigDecimal.TEN,
                    totalCosts = 1410.toScaledBigDecimal()
                ),
                PartnerBudget(
                    partner = partner1,
                    staffCosts = 1200.toScaledBigDecimal(),
                    travelCosts = 165.toScaledBigDecimal(),
                    externalCosts = BigDecimal.ZERO,
                    equipmentCosts = BigDecimal.ZERO,
                    infrastructureCosts = 300.toScaledBigDecimal(),
                    officeAndAdministrationCosts = 120.toScaledBigDecimal(),
                    otherCosts = BigDecimal.ZERO,
                    lumpSumContribution = BigDecimal.ONE,
                    unitCosts = 25.0.toScaledBigDecimal(),
                    totalCosts = 1811.toScaledBigDecimal()
                )
            )
    }

}
