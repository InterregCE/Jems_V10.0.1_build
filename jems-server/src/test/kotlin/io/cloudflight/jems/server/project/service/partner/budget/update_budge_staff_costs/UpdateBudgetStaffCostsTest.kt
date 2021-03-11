package io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.*
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.stream.IntStream
import kotlin.streams.toList

internal class UpdateBudgetStaffCostsTest : UnitTest() {

    private val partnerId = 1L
    private val projectId = 2L
    private val listBudgetEntriesIds = setOf(1L, 2L)
    private val validPeriodNumbers = IntStream.range(1, 4).toList().toSet()
    private val projectPeriods = createProjectPeriods(validPeriodNumbers)
    private val validBudgetPeriods = createBudgetPeriods(validPeriodNumbers)
    private val invalidBudgetPeriods = createBudgetPeriods(setOf(7))
    private val budgetStaffCostEntries = budgetStaffCostEntries(listBudgetEntriesIds, validBudgetPeriods)
    private val budgetStaffCostEntriesWithInvalidPeriods =
        budgetStaffCostEntries(listBudgetEntriesIds, invalidBudgetPeriods)

    @MockK
    lateinit var persistence: ProjectPartnerBudgetCostsUpdatePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var budgetCostValidator: BudgetCostValidator

    @MockK
    lateinit var budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @InjectMockKs
    lateinit var updateBudgetStaffCosts: UpdateBudgetStaffCosts

    @BeforeAll
    fun setup() {
        every { projectPersistence.getProjectIdForPartner(partnerId) } returns projectId
    }

    @Test
    fun `should update and return budget staff cost entries for the specified partner when there isn't any validation error`() {
        val periods = budgetStaffCostEntries.map { it.budgetPeriods }.flatten().toSet()
        val pricePerUnits = budgetStaffCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } returns Unit
        every { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) } returns Unit
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns null
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods
        every { projectPersistence.getProjectIdForPartner(partnerId) } returns projectId
        every { persistence.deleteAllBudgetStaffCostsExceptFor(partnerId, listBudgetEntriesIds) } returns Unit
        every {
            persistence.createOrUpdateBudgetStaffCosts(
                projectId,
                partnerId,
                budgetStaffCostEntries.toSet()
            )
        } returns budgetStaffCostEntries

        val result = updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify(atLeast = 1) { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) }
        verify(atLeast = 1) { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { projectPersistence.getProjectPeriods(projectId) }
        verify(atLeast = 1) { projectPersistence.getProjectIdForPartner(partnerId) }
        verify(atLeast = 1) { persistence.deleteAllBudgetStaffCostsExceptFor(partnerId, listBudgetEntriesIds) }
        verify(atLeast = 1) {
            persistence.createOrUpdateBudgetStaffCosts(
                projectId,
                partnerId,
                budgetStaffCostEntries.toSet()
            )
        }
        confirmVerified(persistence, budgetCostValidator, budgetOptionsPersistence, projectPersistence)


        assertEquals(budgetStaffCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a base validation error`() {
        every { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)
        }

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) }
        confirmVerified(budgetCostValidator)
    }

    @Test
    fun `should throw I18nValidationException when there is validation error in pricePerUnits`() {
        val pricePerUnits = budgetStaffCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)
        }

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        confirmVerified(budgetCostValidator)
    }

    @Test
    fun `should throw I18nValidationException when staffCostsFlatRate is set in the budget options`() {

        every { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(budgetStaffCostEntries.map { it.pricePerUnit }) } returns Unit
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns ProjectPartnerBudgetOptions(
            partnerId,
            staffCostsFlatRate = 10
        )

        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)
        }

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(budgetStaffCostEntries.map { it.pricePerUnit }) }
        verify(atLeast = 1) { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        confirmVerified(budgetCostValidator, budgetOptionsPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error in budgetPeriods`() {
        val budgetPeriods = budgetStaffCostEntriesWithInvalidPeriods.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateBaseEntries(budgetStaffCostEntriesWithInvalidPeriods) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(budgetStaffCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) } returns Unit
        every {
            budgetCostValidator.validateBudgetPeriods(
                budgetPeriods,
                validPeriodNumbers
            )
        } throws I18nValidationException()
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns ProjectPartnerBudgetOptions(partnerId)
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods
        every { projectPersistence.getProjectIdForPartner(partnerId) } returns projectId



        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntriesWithInvalidPeriods)
        }

        verify(atLeast = 1) { budgetCostValidator.validateBaseEntries(budgetStaffCostEntriesWithInvalidPeriods) }
        verify(atLeast = 1) { budgetCostValidator.validatePricePerUnits(budgetStaffCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) }
        verify(atLeast = 1) { budgetCostValidator.validateBudgetPeriods(budgetPeriods, validPeriodNumbers) }
        verify(atLeast = 1) { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { projectPersistence.getProjectPeriods(projectId) }
        verify(atLeast = 1) { projectPersistence.getProjectIdForPartner(partnerId) }
        confirmVerified(budgetCostValidator, budgetOptionsPersistence, projectPersistence)
    }


    private fun budgetStaffCostEntries(listBudgetEntriesIds: Set<Long>, budgetPeriods: MutableSet<BudgetPeriod>) =
        listBudgetEntriesIds
            .map {
                BudgetStaffCostEntry(
                    it,
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    budgetPeriods,
                    BigDecimal.ONE,
                    StaffCostUnitType.HOUR,
                    StaffCostType.UNIT_COST,
                    emptySet(),
                    emptySet()
                )
            }.plus(
                BudgetStaffCostEntry(
                    null,
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    budgetPeriods,
                    BigDecimal.ONE,
                    StaffCostUnitType.HOUR,
                    StaffCostType.UNIT_COST,
                    emptySet(),
                    emptySet(),
                    1
                )
            )

    private fun createBudgetPeriods(numbers: Set<Int>) =
        numbers.map { BudgetPeriod(it, BigDecimal.TEN.truncate()) }.toMutableSet()

    private fun createProjectPeriods(numbers: Set<Int>) =
        numbers.map { ProjectPeriod(it, 1, 12) }


}
