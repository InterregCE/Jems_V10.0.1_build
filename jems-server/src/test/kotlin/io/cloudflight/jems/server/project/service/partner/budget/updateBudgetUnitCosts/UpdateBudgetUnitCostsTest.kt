package io.cloudflight.jems.server.project.service.partner.budget.updateBudgetUnitCosts

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

internal class UpdateBudgetUnitCostsTest : UnitTest() {

    private val PARTNER_ID = 2003L
    private val PROJECT_ID = 3058L

    private val budgetUnitCostExisting = BudgetUnitCostEntry(
        id = 17L,
        numberOfUnits = BigDecimal.valueOf(20007, 4),
        budgetPeriods = mutableSetOf(BudgetPeriod(1, BigDecimal.TEN), BudgetPeriod(2, BigDecimal.TEN)),
        rowSum = null,
        unitCostId = 45L,
    )

    private val budgetUnitCostNew = BudgetUnitCostEntry(
        id = null,
        numberOfUnits = BigDecimal.valueOf(5),
        budgetPeriods = mutableSetOf(BudgetPeriod(1, BigDecimal.valueOf(50))),
        rowSum = null,
        unitCostId = 45L,
    )

    @MockK
    lateinit var persistence: ProjectPartnerBudgetCostsUpdatePersistence
    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var projectUnitCostPersistence: ProjectUnitCostPersistence
    @MockK
    lateinit var callPersistence: CallPersistence
    @MockK
    lateinit var budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence
    @MockK
    lateinit var budgetCostValidator: BudgetCostValidator

    @InjectMockKs
    lateinit var updateBudgetUnitCosts: UpdateBudgetUnitCosts

    @Test
    fun success() {
        val entries = listOf(budgetUnitCostExisting.copy(), budgetUnitCostNew.copy())
        every { budgetCostValidator.validateBaseEntries(entries) } answers { }
        every { budgetOptionsPersistence.getBudgetOptions(PARTNER_ID) } returns
            ProjectPartnerBudgetOptions(partnerId = PARTNER_ID, otherCostsOnStaffCostsFlatRate = null)
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        val unitCost = ProgrammeUnitCost(id = 45L, projectId = null, isOneCostCategory = false, costPerUnit = BigDecimal.TEN)
        val call = mockk<CallDetail>()
        every { call.unitCosts } returns listOf(unitCost)
        every { call.projectDefinedUnitCostAllowed } returns false
        every { callPersistence.getCallByProjectId(PROJECT_ID) } returns call
        every { projectUnitCostPersistence.getAvailableUnitCostsForProjectId(PROJECT_ID) } returns listOf(unitCost)
        val idsToKeep = slot<Set<Long>>()
        every { persistence.deleteAllUnitCostsExceptFor(PARTNER_ID, capture(idsToKeep)) } answers { }
        every { persistence.createOrUpdateBudgetUnitCosts(PROJECT_ID, PARTNER_ID, any()) } returnsArgument 2

        assertThat(updateBudgetUnitCosts.updateBudgetUnitCosts(PARTNER_ID, unitCosts = entries)).containsExactly(
            budgetUnitCostExisting.copy(rowSum = BigDecimal.valueOf(2000, 2)),
            budgetUnitCostNew.copy(rowSum = BigDecimal.valueOf(5000, 2)),
        )

        assertThat(idsToKeep.captured).containsExactly(17L)
    }

    @Test
    fun throwIfOtherCostFlatRateIsSet() {
        every { budgetCostValidator.validateBaseEntries(any()) } answers { }
        every { budgetOptionsPersistence.getBudgetOptions(55L) } returns
            ProjectPartnerBudgetOptions(partnerId = 55L, otherCostsOnStaffCostsFlatRate = 1)

        assertThrows<I18nValidationException> { updateBudgetUnitCosts.updateBudgetUnitCosts(55L, emptyList()) }
    }

    @Test
    fun `section is not allowed to be set`() {
        every { budgetCostValidator.validateBaseEntries(any()) } answers { }
        every { budgetOptionsPersistence.getBudgetOptions(57L) } returns
            ProjectPartnerBudgetOptions(partnerId = 57L, otherCostsOnStaffCostsFlatRate = null)
        every { partnerPersistence.getProjectIdForPartnerId(57L) } returns 1057L

        val unitCost = ProgrammeUnitCost(id = 1L, projectId = null, isOneCostCategory = true, costPerUnit = BigDecimal.TEN)
        val call = mockk<CallDetail>()
        every { call.unitCosts } returns listOf(unitCost)
        every { call.projectDefinedUnitCostAllowed } returns false
        every { callPersistence.getCallByProjectId(1057) } returns call

        assertThrows<UnitCostsBudgetSectionIsNotAllowed> {
            updateBudgetUnitCosts.updateBudgetUnitCosts(57L, listOf(budgetUnitCostNew))
        }
    }

    @Test
    fun `empty unit cost section can be saved`() {
        val projectId = 1057L
        val partnerId = 57L
        every { budgetCostValidator.validateBaseEntries(any()) } answers { }
        every { budgetOptionsPersistence.getBudgetOptions(partnerId) } returns
            ProjectPartnerBudgetOptions(partnerId = partnerId, otherCostsOnStaffCostsFlatRate = null)
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId

        val unitCost = ProgrammeUnitCost(id = 1L, projectId = null, isOneCostCategory = true, costPerUnit = BigDecimal.TEN)
        val call = mockk<CallDetail>()
        every { call.unitCosts } returns listOf(unitCost)
        every { callPersistence.getCallByProjectId(projectId) } returns call
        every { projectUnitCostPersistence.getAvailableUnitCostsForProjectId(projectId) } returns listOf(unitCost)
        every { persistence.deleteAllUnitCostsExceptFor(partnerId, emptySet()) } returns Unit
        every {
            persistence.createOrUpdateBudgetUnitCosts(projectId, partnerId, emptyList())
        } returns listOf(budgetUnitCostExisting)

        assertThat(updateBudgetUnitCosts.updateBudgetUnitCosts(partnerId, emptyList()))
            .contains(budgetUnitCostExisting)
    }
}
