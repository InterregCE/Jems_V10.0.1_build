package io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
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
    private val callId = 3L
    private val unitCostId = 4L
    private val listBudgetEntriesIds = setOf(1L, 2L)
    private val validPeriodNumbers = IntStream.range(1, 4).toList().toSet()
    private val projectPeriods = createProjectPeriods(validPeriodNumbers)
    private val validBudgetPeriods = createBudgetPeriods(validPeriodNumbers)
    private val invalidBudgetPeriods = createBudgetPeriods(setOf(7))
    private val budgetStaffCostEntries = budgetStaffCostEntries(listBudgetEntriesIds, validBudgetPeriods)
    private val budgetStaffCostEntriesWithInvalidPeriods =
        budgetStaffCostEntries(listBudgetEntriesIds, invalidBudgetPeriods)
    private val projectUnitCost = ProgrammeUnitCost(id = unitCostId, isOneCostCategory = true)

    @MockK
    lateinit var persistence: ProjectPartnerBudgetCostsUpdatePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var budgetCostValidator: BudgetCostValidator

    @MockK
    lateinit var budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @InjectMockKs
    lateinit var updateBudgetStaffCosts: UpdateBudgetStaffCosts

    @BeforeAll
    fun setup() {
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { projectPersistence.getCallIdOfProject(projectId) } returns callId
        every { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) } returns Unit
    }

    @Test
    fun `should update and return budget staff cost entries for the specified partner when there isn't any validation error`() {
        val periods = budgetStaffCostEntries.map { it.budgetPeriods }.flatten().toSet()
        val pricePerUnits = budgetStaffCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            periods,
            BudgetCategory.StaffCosts,
            budgetStaffCostEntries.map { it.numberOfUnits }.toList(),
            budgetStaffCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } returns Unit
        every { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) } returns Unit
        every { projectPersistence.getProjectUnitCosts(projectId) } returns listOf(projectUnitCost)
        every { budgetCostValidator.validateAllowedUnitCosts(listOf(projectUnitCost), any()) } returns Unit
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns null
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { persistence.deleteAllBudgetStaffCostsExceptFor(partnerId, listBudgetEntriesIds) } returns Unit
        every {
            persistence.createOrUpdateBudgetStaffCosts(
                projectId,
                partnerId,
                budgetStaffCostEntries.toList()
            )
        } returns budgetStaffCostEntries

        val result = updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) }
        verify { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) }
        verify { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        verify { projectPersistence.getProjectPeriods(projectId) }
        verify { projectPersistence.getProjectUnitCosts(projectId) }
        verify { budgetCostValidator.validateAllowedUnitCosts(listOf(projectUnitCost), any()) }
        verify { persistence.deleteAllBudgetStaffCostsExceptFor(partnerId, listBudgetEntriesIds) }
        verify {
            persistence.createOrUpdateBudgetStaffCosts(
                projectId,
                partnerId,
                budgetStaffCostEntries.toList()
            )
        }
        confirmVerified(persistence, budgetCostValidator, budgetOptionsPersistence, projectPersistence, partnerPersistence)
        assertEquals(budgetStaffCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a base validation error`() {
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetStaffCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.StaffCosts,
            budgetStaffCostEntries.map { it.numberOfUnits }.toList(),
            budgetStaffCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) }
        confirmVerified(budgetCostValidator, partnerPersistence, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is validation error in pricePerUnits`() {
        val pricePerUnits = budgetStaffCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetStaffCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.StaffCosts,
            budgetStaffCostEntries.map { it.numberOfUnits }.toList(),
            budgetStaffCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        confirmVerified(budgetCostValidator, partnerPersistence, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when staffCostsFlatRate is set in the budget options`() {
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetStaffCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.StaffCosts,
            budgetStaffCostEntries.map { it.numberOfUnits }.toList(),
            budgetStaffCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(budgetStaffCostEntries.map { it.pricePerUnit }) } returns Unit
        every { projectPersistence.getProjectUnitCosts(projectId) } returns listOf(projectUnitCost)
        every { budgetCostValidator.validateAllowedUnitCosts(listOf(projectUnitCost), any()) } returns Unit
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns ProjectPartnerBudgetOptions(
            partnerId,
            staffCostsFlatRate = 10
        )

        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetStaffCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(budgetStaffCostEntries.map { it.pricePerUnit }) }
        verify { projectPersistence.getProjectUnitCosts(projectId) }
        verify { budgetCostValidator.validateAllowedUnitCosts(listOf(projectUnitCost), any()) }
        verify { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        confirmVerified(budgetCostValidator, budgetOptionsPersistence, partnerPersistence, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error in budgetPeriods`() {
        val budgetPeriods = budgetStaffCostEntriesWithInvalidPeriods.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetPeriods,
            BudgetCategory.StaffCosts,
            budgetStaffCostEntries.map { it.numberOfUnits }.toList(),
            budgetStaffCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
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
        every { projectPersistence.getProjectUnitCosts(projectId) } returns listOf(projectUnitCost)
        every { budgetCostValidator.validateAllowedUnitCosts(listOf(projectUnitCost), any()) } returns Unit
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId

        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntriesWithInvalidPeriods)
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetStaffCostEntriesWithInvalidPeriods) }
        verify { budgetCostValidator.validatePricePerUnits(budgetStaffCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) }
        verify { budgetCostValidator.validateBudgetPeriods(budgetPeriods, validPeriodNumbers) }
        verify { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) }
        verify { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        verify { projectPersistence.getProjectPeriods(projectId) }
        verify { projectPersistence.getProjectUnitCosts(projectId) }
        verify { budgetCostValidator.validateAllowedUnitCosts(listOf(projectUnitCost), any()) }
        confirmVerified(budgetCostValidator, budgetOptionsPersistence, projectPersistence, partnerPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error in unitCosts`() {
        val budgetStaffCostEntry = BudgetStaffCostEntry(
            id = 1L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.ONE,
            budgetPeriods = validBudgetPeriods,
            unitCostId = unitCostId,
            pricePerUnit = BigDecimal.TEN, // wrong
            description = emptySet(),
            comments = emptySet(),
            unitType = emptySet()
        )
        val budgetStaffCostList = listOf(budgetStaffCostEntry)
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetStaffCostList.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.StaffCosts,
            budgetStaffCostList.map { it.numberOfUnits }.toList(),
            budgetStaffCostList.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetStaffCostList) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(budgetStaffCostList.map { it.pricePerUnit }) } returns Unit
        every { projectPersistence.getProjectUnitCosts(projectId) } returns listOf(projectUnitCost)
        every { budgetCostValidator.validateAllowedUnitCosts(listOf(projectUnitCost), any()) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntriesWithInvalidPeriods)
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetStaffCostEntriesWithInvalidPeriods) }
        verify { budgetCostValidator.validatePricePerUnits(budgetStaffCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) }
        verify { projectPersistence.getProjectUnitCosts(projectId) }
        verify { budgetCostValidator.validateAllowedUnitCosts(listOf(projectUnitCost), any()) }
        confirmVerified(budgetCostValidator, projectPersistence, partnerPersistence)
    }

    private fun budgetStaffCostEntries(listBudgetEntriesIds: Set<Long>, budgetPeriods: MutableSet<BudgetPeriod>) =
        listBudgetEntriesIds
            .map {
                BudgetStaffCostEntry(
                    it,
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    budgetPeriods,
                    null,
                    BigDecimal.ONE,
                    emptySet(),
                    emptySet(),
                    emptySet()
                )
            }.plus(
                BudgetStaffCostEntry(
                    null,
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    budgetPeriods,
                    unitCostId,
                    BigDecimal.ONE,
                    emptySet(),
                    emptySet(),
                    emptySet()
                )
            )

    private fun createBudgetPeriods(numbers: Set<Int>) =
        numbers.map { BudgetPeriod(it, BigDecimal.TEN.truncate()) }.toMutableSet()

    private fun createProjectPeriods(numbers: Set<Int>) =
        numbers.map { ProjectPeriod(it, 1, 12) }

}
