package io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost

import io.cloudflight.jems.server.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetBudgetTotalCostTest : UnitTest() {

    val partnerId = 1L

    @MockK
    lateinit var getBudgetTotalCostCalculator: GetBudgetTotalCostCalculator

    @InjectMockKs
    lateinit var getBudgetTotalCost: GetBudgetTotalCost


    @Test
    fun `should return budget total cost`() {
        every { getBudgetTotalCostCalculator.getBudgetTotalCost(partnerId, "v1.0") } returns BigDecimal(100)
        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId, "v1.0")
        assertEquals(BigDecimal(100), result)
    }

    @Test
    fun `should return budget total spf cost`() {
        every { getBudgetTotalCostCalculator.getBudgetTotalSpfCost(partnerId, "v1.0") } returns BigDecimal(200)
        val result = getBudgetTotalCost.getBudgetTotalSpfCost(partnerId, "v1.0")
        assertEquals(BigDecimal(200), result)
    }
}
