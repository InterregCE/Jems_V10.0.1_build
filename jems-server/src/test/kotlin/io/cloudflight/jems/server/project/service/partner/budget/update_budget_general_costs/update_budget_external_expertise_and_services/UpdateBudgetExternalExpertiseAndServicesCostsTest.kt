package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_external_expertise_and_services

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.UpdateBudgetGeneralCostsTest
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateBudgetExternalExpertiseAndServicesCostsTest : UpdateBudgetGeneralCostsTest() {

    @InjectMockKs
    lateinit var updateExternalExpertiseAndServicesCosts: UpdateBudgetExternalExpertiseAndServicesCosts

    @Test
    fun `should update and return budget external expertise and services cost entries for the specified partner when there isn't any validation error`() {
        val pricePerUnits = budgetGeneralCostEntries.map { it.pricePerUnit }
        val periods = budgetGeneralCostEntries.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } returns Unit
        every { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) } returns Unit
        every { projectPersistence.getProjectIdForPartner(partnerId) } returns projectId
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods
        every { budgetCostsPersistence.deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(partnerId, listBudgetEntriesIds) } returns Unit
        every { budgetCostsPersistence.createOrUpdateBudgetExternalExpertiseAndServicesCosts(projectId, partnerId, budgetGeneralCostEntries.toSet()) } returns budgetGeneralCostEntries

        val result = updateExternalExpertiseAndServicesCosts.updateBudgetGeneralCosts(partnerId, budgetGeneralCostEntries)

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify(atLeast = 1) { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) }
        verify(atLeast = 1) { projectPersistence.getProjectIdForPartner(partnerId) }
        verify(atLeast = 1) { projectPersistence.getProjectPeriods(projectId) }
        verify(atLeast = 1) { budgetCostsPersistence.deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(partnerId, listBudgetEntriesIds) }
        verify(atLeast = 1) { budgetCostsPersistence.createOrUpdateBudgetExternalExpertiseAndServicesCosts(projectId, partnerId, budgetGeneralCostEntries.toSet()) }
        confirmVerified(budgetCostsPersistence, budgetCostValidator, projectPersistence)

        assertEquals(budgetGeneralCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a base validation error`() {
        every { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateExternalExpertiseAndServicesCosts.updateBudgetGeneralCosts(partnerId, budgetGeneralCostEntries)
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
            updateExternalExpertiseAndServicesCosts.updateBudgetGeneralCosts(partnerId, budgetGeneralCostEntries)
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
        every { projectPersistence.getProjectIdForPartner(partnerId) } returns projectId
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods


        assertThrows<I18nValidationException> {
            updateExternalExpertiseAndServicesCosts.updateBudgetGeneralCosts(partnerId, budgetGeneralCostEntriesWithInvalidPeriods)
        }

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetGeneralCostEntriesWithInvalidPeriods) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(budgetGeneralCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) }
        verify(atLeast = 1) { budgetCostValidator.validateBudgetPeriods(budgetPeriods, validPeriodNumbers) }
        verify(atLeast = 1) { projectPersistence.getProjectIdForPartner(partnerId) }
        verify(atLeast = 1) { projectPersistence.getProjectPeriods(projectId) }
        confirmVerified(budgetCostValidator,  projectPersistence)
    }

}
