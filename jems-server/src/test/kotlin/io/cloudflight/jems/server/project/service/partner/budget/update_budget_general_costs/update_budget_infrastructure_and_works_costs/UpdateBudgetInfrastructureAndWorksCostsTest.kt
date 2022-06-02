package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_infrastructure_and_works_costs

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.UpdateBudgetGeneralCostsTest
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateBudgetInfrastructureAndWorksCostsTest : UpdateBudgetGeneralCostsTest() {

    @InjectMockKs
    lateinit var updateBudgetInfrastructureAndWorksCosts: UpdateBudgetInfrastructureAndWorksCosts

    @Test
    fun `should update and return budget infrastructure and works cost entries for the specified partner when there isn't any validation error`() {
        val pricePerUnits = budgetGeneralCostEntries.map { it.pricePerUnit }
        val periods = budgetGeneralCostEntries.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            periods,
            BudgetCategory.InfrastructureCosts,
            budgetGeneralCostEntries.map { it.numberOfUnits }.toList(),
            budgetGeneralCostEntries.map { it.unitType }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } returns Unit
        every { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) } returns Unit
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns null
        every { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) } returns Unit

        every {
            budgetCostsPersistence.deleteAllBudgetInfrastructureAndWorksCostsExceptFor(
                partnerId,
                listBudgetEntriesIds
            )
        } returns Unit
        every {
            budgetCostsPersistence.createOrUpdateBudgetInfrastructureAndWorksCosts(
                projectId,
                partnerId,
                budgetGeneralCostEntries.toList()
            )
        } returns budgetGeneralCostEntries

        val result =
            updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(
                partnerId,
                budgetGeneralCostEntries,
                BudgetCategory.InfrastructureCosts
            )

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) }
        verify { projectPersistence.getProjectPeriods(projectId) }
        verify { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) }
        verify { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        verify {
            budgetCostsPersistence.deleteAllBudgetInfrastructureAndWorksCostsExceptFor(
                partnerId,
                listBudgetEntriesIds
            )
        }
        verify {
            budgetCostsPersistence.createOrUpdateBudgetInfrastructureAndWorksCosts(
                projectId,
                partnerId,
                budgetGeneralCostEntries.toList()
            )
        }
        confirmVerified(budgetCostsPersistence, budgetCostValidator, projectPersistence, partnerPersistence)

        assertEquals(budgetGeneralCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a base validation error`() {
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetGeneralCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.InfrastructureCosts,
            budgetGeneralCostEntries.map { it.numberOfUnits }.toList(),
            budgetGeneralCostEntries.map { it.unitType }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(
                partnerId,
                budgetGeneralCostEntries,
                BudgetCategory.InfrastructureCosts
            )
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) }
        confirmVerified(budgetCostValidator, partnerPersistence, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is validation error in pricePerUnits`() {
        val pricePerUnits = budgetGeneralCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetGeneralCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.InfrastructureCosts,
            budgetGeneralCostEntries.map { it.numberOfUnits }.toList(),
            budgetGeneralCostEntries.map { it.unitType }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(
                partnerId,
                budgetGeneralCostEntries,
                BudgetCategory.InfrastructureCosts
            )
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        confirmVerified(budgetCostValidator, partnerPersistence, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error in budgetPeriods`() {
        val budgetPeriods = budgetGeneralCostEntriesWithInvalidPeriods.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetPeriods,
            BudgetCategory.InfrastructureCosts,
            budgetGeneralCostEntries.map { it.numberOfUnits }.toList(),
            budgetGeneralCostEntries.map { it.unitType }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntriesWithInvalidPeriods) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(budgetGeneralCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) } returns Unit
        every {
            budgetCostValidator.validateBudgetPeriods(
                budgetPeriods,
                validPeriodNumbers
            )
        } throws I18nValidationException()
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods


        assertThrows<I18nValidationException> {
            updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(
                partnerId,
                budgetGeneralCostEntriesWithInvalidPeriods,
                BudgetCategory.InfrastructureCosts
            )
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntriesWithInvalidPeriods) }
        verify { budgetCostValidator.validatePricePerUnits(budgetGeneralCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) }
        verify { budgetCostValidator.validateBudgetPeriods(budgetPeriods, validPeriodNumbers) }
        verify { projectPersistence.getProjectPeriods(projectId) }
        confirmVerified(budgetCostValidator, projectPersistence, partnerPersistence)
    }

    @Test
    fun `should throw I18nValidationException when otherCostsOnStaffCostsFlatRate is set in the budget options`() {
        val pricePerUnits = budgetGeneralCostEntries.map { it.pricePerUnit }
        val periods = budgetGeneralCostEntries.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            periods,
            BudgetCategory.InfrastructureCosts,
            budgetGeneralCostEntries.map { it.numberOfUnits }.toList(),
            budgetGeneralCostEntries.map { it.unitType }.toList()
        ) } returns Unit
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns ProjectPartnerBudgetOptions(
            partnerId,
            otherCostsOnStaffCostsFlatRate = 10
        )
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } returns Unit
        every { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) } returns Unit
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods


        assertThrows<I18nValidationException> {
            updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(
                partnerId,
                budgetGeneralCostEntries,
                BudgetCategory.InfrastructureCosts
            )
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) }
        verify { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) }
        verify { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify { projectPersistence.getProjectPeriods(projectId) }
        verify { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        confirmVerified(budgetCostValidator, budgetOptionsPersistence, partnerPersistence, projectPersistence)
    }


}
