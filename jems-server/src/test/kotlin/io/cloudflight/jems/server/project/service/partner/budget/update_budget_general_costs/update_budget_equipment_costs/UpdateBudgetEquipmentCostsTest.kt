package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_equipment_costs

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

internal class UpdateBudgetEquipmentCostsTest : UpdateBudgetGeneralCostsTest() {

    @InjectMockKs
    lateinit var updateBudgetEquipmentCosts: UpdateBudgetEquipmentCosts

    @Test
    fun `should update and return budget equipment cost entries for the specified partner when there isn't any validation error`() {
        val callId = 3L
        val pricePerUnits = budgetGeneralCostEntries.map { it.pricePerUnit }
        val periods = budgetGeneralCostEntries.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } returns Unit
        every { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) } returns Unit
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns null
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods
        every { projectPersistence.getCallIdOfProject(projectId) } returns callId
        every { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) } returns Unit

        every {
            budgetCostsPersistence.deleteAllBudgetEquipmentCostsExceptFor(
                partnerId,
                listBudgetEntriesIds
            )
        } returns Unit
        every {
            budgetCostsPersistence.createOrUpdateBudgetEquipmentCosts(
                projectId,
                partnerId,
                budgetGeneralCostEntries.toList()
            )
        } returns budgetGeneralCostEntries

        val result = updateBudgetEquipmentCosts.updateBudgetGeneralCosts(
            partnerId,
            budgetGeneralCostEntries,
            BudgetCategory.InfrastructureCosts
        )

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify(atLeast = 1) { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) }
        verify(atLeast = 1) { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify(atLeast = 1) { projectPersistence.getProjectPeriods(projectId) }
        verify(atLeast = 1) { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { projectPersistence.getCallIdOfProject(projectId) }
        verify(atLeast = 1) { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) }
        verify(atLeast = 1) {
            budgetCostsPersistence.deleteAllBudgetEquipmentCostsExceptFor(
                partnerId,
                listBudgetEntriesIds
            )
        }
        verify(atLeast = 1) {
            budgetCostsPersistence.createOrUpdateBudgetEquipmentCosts(
                projectId,
                partnerId,
                budgetGeneralCostEntries.toList()
            )
        }
        confirmVerified(budgetCostsPersistence, budgetCostValidator, projectPersistence)

        assertEquals(budgetGeneralCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a base validation error`() {
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetEquipmentCosts.updateBudgetGeneralCosts(
                partnerId,
                budgetGeneralCostEntries,
                BudgetCategory.InfrastructureCosts
            )
        }

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) }
        confirmVerified(budgetCostValidator)
    }

    @Test
    fun `should throw I18nValidationException when there is validation error in pricePerUnits`() {

        val pricePerUnits = budgetGeneralCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetEquipmentCosts.updateBudgetGeneralCosts(
                partnerId,
                budgetGeneralCostEntries,
                BudgetCategory.InfrastructureCosts
            )
        }

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        confirmVerified(budgetCostValidator)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error in budgetPeriods`() {
        val budgetPeriods = budgetGeneralCostEntriesWithInvalidPeriods.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntriesWithInvalidPeriods) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(budgetGeneralCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) } returns Unit
        every {
            budgetCostValidator.validateBudgetPeriods(
                budgetPeriods,
                validPeriodNumbers
            )
        } throws I18nValidationException()
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods


        assertThrows<I18nValidationException> {
            updateBudgetEquipmentCosts.updateBudgetGeneralCosts(
                partnerId,
                budgetGeneralCostEntriesWithInvalidPeriods,
                BudgetCategory.InfrastructureCosts
            )
        }

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntriesWithInvalidPeriods) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(budgetGeneralCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) }
        verify(atLeast = 1) { budgetCostValidator.validateBudgetPeriods(budgetPeriods, validPeriodNumbers) }
        verify(atLeast = 1) { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify(atLeast = 1) { projectPersistence.getProjectPeriods(projectId) }
        confirmVerified(budgetCostValidator, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when otherCostsOnStaffCostsFlatRate is set in the budget options`() {

        val pricePerUnits = budgetGeneralCostEntries.map { it.pricePerUnit }
        val periods = budgetGeneralCostEntries.map { it.budgetPeriods }.flatten().toSet()
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns ProjectPartnerBudgetOptions(
            partnerId,
            otherCostsOnStaffCostsFlatRate = 10
        )
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } returns Unit
        every { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) } returns Unit
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods


        assertThrows<I18nValidationException> {
            updateBudgetEquipmentCosts.updateBudgetGeneralCosts(
                partnerId,
                budgetGeneralCostEntries,
                BudgetCategory.InfrastructureCosts
            )
        }

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) }
        verify(atLeast = 1) { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify(atLeast = 1) { projectPersistence.getProjectPeriods(projectId) }
        verify(atLeast = 1) { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        confirmVerified(budgetCostValidator, budgetOptionsPersistence)
    }


}
