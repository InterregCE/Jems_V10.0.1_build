package io.cloudflight.jems.server.project.service.partner.budget.updateSpfCosts

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.budget.updateBudgetSpfCosts.UpdateBudgetSpfCosts
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetSpfCostEntry
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.stream.IntStream
import kotlin.streams.toList

internal class UpdateBudgetSpfCostsTest : UnitTest() {

    companion object {
        private const val partnerId = 1L
        private const val projectId = 2L
        val listBudgetEntriesIds = setOf(1L, 2L)
        val validPeriodNumbers = IntStream.range(1, 4).toList().toSet()
        val projectPeriods = createProjectPeriods(validPeriodNumbers)
        private val validBudgetPeriods = createBudgetPeriods(validPeriodNumbers)
        private val invalidBudgetPeriods = createBudgetPeriods(setOf(7))
        val spfCostEntries = spfCostEntries(listBudgetEntriesIds, validBudgetPeriods)
        val spfCostEntriesWithInvalidPeriods = spfCostEntries(listBudgetEntriesIds, invalidBudgetPeriods)
        val callSettings = ProjectCallSettings(
            callId = 2,
            callName = "callName",
            callType = CallType.SPF,
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            endDateStep1 = null,
            lengthOfPeriod = 6,
            isAdditionalFundAllowed = false,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
            stateAids = emptyList(),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            costOption = mockk(),
        )
    }

    @MockK
    lateinit var persistence: ProjectPartnerBudgetCostsUpdatePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var budgetCostValidator: BudgetCostValidator

    @InjectMockKs
    lateinit var updateBudgetSpfCosts: UpdateBudgetSpfCosts


    @BeforeAll
    fun setup() {
        every { projectPersistence.getCallIdOfProject(projectId) } returns callSettings.callId
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { projectPersistence.getProjectCallSettings(projectId) } returns callSettings
        every { budgetCostValidator.validateAllowedSpfCosts(callSettings) } returns Unit
    }

    @Test
    fun `should update and return budget spf cost entries for the specified partner`() {
        val periods = spfCostEntries.map { it.budgetPeriods }.flatten().toSet()
        val pricePerUnits = spfCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            periods,
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(spfCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } returns Unit
        every { budgetCostValidator.validateAllowedUnitCosts(emptyList(), emptyList()) } returns Unit
        every { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) } returns Unit
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods
        every { persistence.deleteAllBudgetSpfCostsExceptFor(partnerId, listBudgetEntriesIds) } returns Unit
        every {
            persistence.createOrUpdateBudgetSpfCosts(projectId, partnerId, spfCostEntries.toList())
        } returns spfCostEntries

        val result = updateBudgetSpfCosts.updateBudgetSpfCosts(partnerId, spfCostEntries)

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            periods,
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) }
        verify { projectPersistence.getProjectCallSettings(projectId) }
        verify { budgetCostValidator.validateBaseEntries(spfCostEntries) }
        verify { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify { budgetCostValidator.validateAllowedUnitCosts(emptyList(), emptyList()) }
        verify { budgetCostValidator.validateBudgetPeriods(periods, validPeriodNumbers) }
        verify { budgetCostValidator.validateAllowedSpfCosts(callSettings) }
        verify { projectPersistence.getProjectPeriods(projectId) }
        verify { persistence.deleteAllBudgetSpfCostsExceptFor(partnerId, listBudgetEntriesIds) }
        verify {
            persistence.createOrUpdateBudgetSpfCosts(projectId, partnerId, spfCostEntries.toList())
        }
        confirmVerified(persistence, budgetCostValidator, projectPersistence, partnerPersistence)
        assertEquals(spfCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a base validation error`() {
        every { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            spfCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(spfCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetSpfCosts.updateBudgetSpfCosts(partnerId, spfCostEntries)
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            spfCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) }
        verify { budgetCostValidator.validateBaseEntries(spfCostEntries) }
        confirmVerified(budgetCostValidator, projectPersistence, partnerPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is validation error in pricePerUnits`() {
        val pricePerUnits = spfCostEntries.map { it.pricePerUnit }
        every { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            spfCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit

        every { budgetCostValidator.validateBaseEntries(spfCostEntries) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(pricePerUnits) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetSpfCosts.updateBudgetSpfCosts(partnerId, spfCostEntries)
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            spfCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) }
        verify { budgetCostValidator.validatePricePerUnits(pricePerUnits) }
        verify { projectPersistence.getProjectCallSettings(projectId) }
        verify { budgetCostValidator.validateAllowedSpfCosts(callSettings) }
        verify { budgetCostValidator.validateBaseEntries(spfCostEntries) }
        confirmVerified(budgetCostValidator, projectPersistence, partnerPersistence)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error in budgetPeriods`() {
        val budgetPeriods = spfCostEntriesWithInvalidPeriods.map { it.budgetPeriods }.flatten().toSet()
        every { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            budgetPeriods,
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } returns Unit
        every { budgetCostValidator.validateBaseEntries(spfCostEntriesWithInvalidPeriods) } returns Unit
        every { budgetCostValidator.validatePricePerUnits(spfCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) } returns Unit
        every { budgetCostValidator.validateAllowedUnitCosts(emptyList(), emptyList()) } returns Unit
        every {
            budgetCostValidator.validateBudgetPeriods(budgetPeriods, validPeriodNumbers)
        } throws I18nValidationException()
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods

        assertThrows<I18nValidationException> {
            updateBudgetSpfCosts.updateBudgetSpfCosts(partnerId, spfCostEntriesWithInvalidPeriods)
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            budgetPeriods,
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) }
        verify { budgetCostValidator.validateBaseEntries(spfCostEntriesWithInvalidPeriods) }
        verify { budgetCostValidator.validatePricePerUnits(spfCostEntriesWithInvalidPeriods.map { it.pricePerUnit }) }
        verify { budgetCostValidator.validateAllowedUnitCosts(emptyList(), emptyList()) }
        verify { budgetCostValidator.validateBudgetPeriods(budgetPeriods, validPeriodNumbers) }
        verify { projectPersistence.getProjectCallSettings(projectId) }
        verify { budgetCostValidator.validateAllowedSpfCosts(callSettings) }
        verify { projectPersistence.getProjectPeriods(projectId) }
        confirmVerified(budgetCostValidator, projectPersistence, partnerPersistence)
    }

    @Test
    fun `should throw I18nValidationException when budget data not according to AF Config`() {
        every { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            spfCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetSpfCosts.updateBudgetSpfCosts(partnerId, spfCostEntries)
        }

        verify { partnerPersistence.getProjectIdForPartnerId(partnerId) }
        verify { projectPersistence.getCallIdOfProject(projectId) }
        verify { budgetCostValidator.validateAgainstAFConfig(
            callSettings.callId,
            spfCostEntries.map { it.budgetPeriods }.flatten().toSet(),
            null,
            spfCostEntries.map { it.numberOfUnits }.toList(),
            spfCostEntries.map { Pair(it.unitCostId, it.unitType) }.toList()
        ) }
        confirmVerified(budgetCostValidator, projectPersistence, partnerPersistence)
    }
}

private fun spfCostEntries(listBudgetEntriesIds: Set<Long>, budgetPeriods: MutableSet<BudgetPeriod>) =
    listBudgetEntriesIds
        .map {
            BudgetSpfCostEntry(
                it, BigDecimal.ONE, BigDecimal.ONE, budgetPeriods,
                null, BigDecimal.ONE, emptySet(), emptySet()
            )
        }
        .plus(
            BudgetSpfCostEntry(
                null, BigDecimal.ONE, BigDecimal.ONE, budgetPeriods,
                null, BigDecimal.ONE, emptySet(), emptySet()
            )
        )

private fun createBudgetPeriods(numbers: Set<Int>) =
    numbers.map { BudgetPeriod(it, BigDecimal.TEN.truncate()) }.toMutableSet()

private fun createProjectPeriods(numbers: Set<Int>) =
    numbers.map { ProjectPeriod(it, 1, 12) }
