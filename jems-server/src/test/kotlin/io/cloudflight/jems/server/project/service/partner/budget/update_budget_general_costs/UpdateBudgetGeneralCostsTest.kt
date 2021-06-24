package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeAll
import java.math.BigDecimal
import java.util.stream.IntStream
import kotlin.streams.toList

open class UpdateBudgetGeneralCostsTest : UnitTest() {

    protected val partnerId = 1L
    protected val projectId = 2L
    protected val listBudgetEntriesIds = setOf(1L, 2L)
    protected val validPeriodNumbers = IntStream.range(1, 4).toList().toSet()
    protected val projectPeriods = createProjectPeriods(validPeriodNumbers)
    protected val validBudgetPeriods = createBudgetPeriods(validPeriodNumbers)
    private val invalidBudgetPeriods = createBudgetPeriods(setOf(7))
    protected val budgetGeneralCostEntries = budgetGeneralCostEntries(listBudgetEntriesIds, validBudgetPeriods)
    protected val budgetGeneralCostEntriesWithInvalidPeriods =
        budgetGeneralCostEntries(listBudgetEntriesIds, invalidBudgetPeriods)


    @MockK
    lateinit var budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @MockK
    lateinit var budgetCostsPersistence: ProjectPartnerBudgetCostsUpdatePersistence

    @MockK
    lateinit var budgetCostValidator: BudgetCostValidator

    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @BeforeAll
    fun setup() {
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
    }

    private fun budgetGeneralCostEntries(listBudgetEntriesIds: Set<Long>, budgetPeriods: MutableSet<BudgetPeriod>) =
        listBudgetEntriesIds
    .map {
        BudgetGeneralCostEntry(
            it, BigDecimal.ONE, BigDecimal.ONE,
            budgetPeriods, BigDecimal.ONE, null, emptySet(), emptySet()
        )
    }.plus(
            BudgetGeneralCostEntry(
                null, BigDecimal.ONE, BigDecimal.ONE,
                budgetPeriods, BigDecimal.ONE, null, emptySet(), emptySet()
            )
        )

    private fun createBudgetPeriods(numbers: Set<Int>) =
        numbers.map { BudgetPeriod(it, BigDecimal.TEN.truncate()) }.toMutableSet()

    private fun createProjectPeriods(numbers: Set<Int>) =
        numbers.map { ProjectPeriod(it, 1, 12) }
}
