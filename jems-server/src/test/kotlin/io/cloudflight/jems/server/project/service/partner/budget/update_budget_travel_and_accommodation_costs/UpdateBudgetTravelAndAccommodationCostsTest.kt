package io.cloudflight.jems.server.project.service.partner.budget.update_budget_travel_and_accommodation_costs

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
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

internal class UpdateBudgetTravelAndAccommodationCostsTest : UnitTest() {

    private val partnerId = 1L
    private val projectId = 2L
    private val callId = 3L
    private val listBudgetEntriesIds = setOf(1L, 2L)
    private val validPeriodNumbers = IntStream.range(1, 4).toList().toSet()
    private val projectPeriods = createProjectPeriods(validPeriodNumbers)
    private val validBudgetPeriods = createBudgetPeriods(validPeriodNumbers)
    private val invalidBudgetPeriods = createBudgetPeriods(setOf(7))
    private val budgetTravelCostEntries = budgetTravelCostEntries(listBudgetEntriesIds, validBudgetPeriods)
    private val budgetTravelCostEntriesWithInvalidPeriods =
        budgetTravelCostEntries(listBudgetEntriesIds, invalidBudgetPeriods)

    @MockK
    lateinit var persistence: ProjectPartnerBudgetCostsUpdatePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @MockK
    lateinit var budgetCostValidator: BudgetCostValidator

    @MockK
    lateinit var projectUnitCostPersistence: ProjectUnitCostPersistence

    @InjectMockKs
    lateinit var updateBudgetTravelAndAccommodationCosts: UpdateBudgetTravelAndAccommodationCosts


    @BeforeAll
    fun setup() {
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { projectPersistence.getCallIdOfProject(projectId) } returns callId
        every { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) } returns Unit
    }

    @Test
    fun `should update and return budget travel and accommodation cost entries for the specified partner when there isn't any validation error`() {
        val periods = budgetTravelCostEntries.map { it.budgetPeriods }.flatten().toSet()
        val pricePerUnits = budgetTravelCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            periods,
            BudgetCategory.TravelAndAccommodationCosts,
            budgetTravelCostEntries.map { it.numberOfUnits }.toList(),
            budgetTravelCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } returns Unit
        every { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) } returns Unit
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns null
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every {
            persistence.deleteAllBudgetTravelAndAccommodationCostsExceptFor(
                partnerId,
                listBudgetEntriesIds
            )
        } returns Unit
        every {
            persistence.createOrUpdateBudgetTravelAndAccommodationCosts(
                projectId,
                partnerId,
                budgetTravelCostEntries.toList()
            )
        } returns budgetTravelCostEntries

        val result = updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(
            partnerId,
            budgetTravelCostEntries
        )

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, periods, any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) }
        verify { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) }
        verify { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        verify { projectPersistence.getProjectPeriods(projectId) }
        verify {
            persistence.deleteAllBudgetTravelAndAccommodationCostsExceptFor(
                partnerId,
                listBudgetEntriesIds
            )
        }
        verify {
            persistence.createOrUpdateBudgetTravelAndAccommodationCosts(
                projectId,
                partnerId,
                budgetTravelCostEntries.toList()
            )
        }
        confirmVerified(persistence, budgetCostValidator, budgetOptionsPersistence, projectPersistence, partnerPersistence)

        assertEquals(budgetTravelCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a base validation error`() {
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetTravelCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.TravelAndAccommodationCosts,
            budgetTravelCostEntries.map { it.numberOfUnits }.toList(),
            budgetTravelCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(
                partnerId,
                budgetTravelCostEntries
            )
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) }
        confirmVerified(budgetCostValidator, partnerPersistence, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is validation error in pricePerUnits`() {
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetTravelCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.TravelAndAccommodationCosts,
            budgetTravelCostEntries.map { it.numberOfUnits }.toList(),
            budgetTravelCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        val pricePerUnits = budgetTravelCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(
                partnerId,
                budgetTravelCostEntries
            )
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) }
        confirmVerified(budgetCostValidator, partnerPersistence, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when travelAndAccommodationOnStaffCostsFlatRate is set in the budget options`() {
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetTravelCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.TravelAndAccommodationCosts,
            budgetTravelCostEntries.map { it.numberOfUnits }.toList(),
            budgetTravelCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(budgetTravelCostEntries.map { it.pricePerUnit }) } returns Unit
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns ProjectPartnerBudgetOptions(
            partnerId,
            travelAndAccommodationOnStaffCostsFlatRate = 10
        )

        assertThrows<I18nValidationException> {
            updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(
                partnerId,
                budgetTravelCostEntries
            )
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(budgetTravelCostEntries.map { it.pricePerUnit }) }
        verify { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        confirmVerified(budgetCostValidator, budgetOptionsPersistence, partnerPersistence, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when otherCostsOnStaffCostsFlatRate is set in the budget options`() {
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetTravelCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            BudgetCategory.TravelAndAccommodationCosts,
            budgetTravelCostEntries.map { it.numberOfUnits }.toList(),
            budgetTravelCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(budgetTravelCostEntries.map { it.pricePerUnit }) } returns Unit
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns ProjectPartnerBudgetOptions(
            partnerId,
            otherCostsOnStaffCostsFlatRate = 10
        )

        assertThrows<I18nValidationException> {
            updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(
                partnerId,
                budgetTravelCostEntries
            )
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetTravelCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(budgetTravelCostEntries.map { it.pricePerUnit }) }
        verify { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        confirmVerified(budgetCostValidator, budgetOptionsPersistence, partnerPersistence, projectPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error in budgetPeriods`() {
        val budgetPeriods = budgetTravelCostEntriesWithInvalidPeriods.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateAgainstAFConfig(
            callId,
            budgetPeriods,
            BudgetCategory.TravelAndAccommodationCosts,
            budgetTravelCostEntries.map { it.numberOfUnits }.toList(),
            budgetTravelCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(budgetTravelCostEntriesWithInvalidPeriods) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(budgetTravelCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) } returns Unit
        every {
            budgetCostValidator.validateBudgetPeriods(
                budgetPeriods,
                validPeriodNumbers
            )
        } throws I18nValidationException()
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns ProjectPartnerBudgetOptions(partnerId)
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId


        assertThrows<I18nValidationException> {
            updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(
                partnerId,
                budgetTravelCostEntriesWithInvalidPeriods
            )
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(callId, any(), any(), any(), any()) }
        verify { budgetCostValidator.validateBaseEntries(budgetTravelCostEntriesWithInvalidPeriods) }
        verify { budgetCostValidator.validatePricePerUnits(budgetTravelCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) }
        verify { budgetCostValidator.validateBudgetPeriods(budgetPeriods, validPeriodNumbers) }
        verify { budgetCostValidator.validateAllowedRealCosts(callId, any(), any()) }
        verify { budgetOptionsPersistence.getBudgetOptions(partnerId) }
        verify { projectPersistence.getProjectPeriods(projectId) }
        confirmVerified(budgetCostValidator, budgetOptionsPersistence, projectPersistence, partnerPersistence)
    }

    private fun budgetTravelCostEntries(listBudgetEntriesIds: Set<Long>, budgetPeriods: MutableSet<BudgetPeriod>) =
        listBudgetEntriesIds
            .map {
                BudgetTravelAndAccommodationCostEntry(
                    it, BigDecimal.ONE, BigDecimal.ONE, budgetPeriods,
                    null, BigDecimal.ONE, emptySet(), emptySet()
                )
            }
            .plus(
                BudgetTravelAndAccommodationCostEntry(
                    null, BigDecimal.ONE, BigDecimal.ONE, budgetPeriods,
                    null, BigDecimal.ONE, emptySet(), emptySet()
                )
            )

    private fun createBudgetPeriods(numbers: Set<Int>) =
        numbers.map { BudgetPeriod(it, BigDecimal.TEN.truncate()) }.toMutableSet()

    private fun createProjectPeriods(numbers: Set<Int>) =
        numbers.map { ProjectPeriod(it, 1, 12) }

}
