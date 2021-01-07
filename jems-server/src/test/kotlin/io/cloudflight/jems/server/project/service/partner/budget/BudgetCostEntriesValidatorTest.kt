package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.stream.IntStream
import kotlin.streams.toList

internal class BudgetCostEntriesValidatorTest : UnitTest() {

    @InjectMockKs
    lateinit var budgetCostEntriesValidator: BudgetCostEntriesValidator

    @Test
    fun `should throw I18nValidationException when number of entries is more that allowed`() {

        val budgetCostEntries = IntStream.range(0, MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES.plus(1)).toList().map {
            object : BaseBudgetEntry {
                override val id = it.toLong()
                override val numberOfUnits = BigDecimal.ZERO
                override val pricePerUnit = BigDecimal.ZERO
                override val rowSum = BigDecimal.ZERO
            }
        }
        val ex = assertThrows<I18nValidationException> {
            budgetCostEntriesValidator.validate(budgetCostEntries)
        }

        assertEquals(BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY, ex.i18nKey)
    }

    @TestFactory
    fun `should throw I18nValidationException when at least one of budget cost values is more that allowed`() =
        listOf(
            createBaseBudgetEntries(withInvalidNumberOfUnits = true) to "numberOfUnits is more than allowed ",
            createBaseBudgetEntries(withInvalidPricePerUnit = true) to "pricePerUnit is more than allowed ",
            createBaseBudgetEntries(withInvalidRowSum = true) to "rowSum is more than allowed ",
        ).map { (budgetEntries, argument) ->
            DynamicTest.dynamicTest(
                "should throw I18nValidationException when at least in one budget cost entry $argument"
            ) {
                val ex = assertThrows<I18nValidationException> {
                    budgetCostEntriesValidator.validate(budgetEntries)
                }
                assertEquals(BUDGET_COST_VALUE_LIMIT_ERROR_KEY, ex.i18nKey)
            }
        }

    private fun createBaseBudgetEntries(withInvalidNumberOfUnits: Boolean = false, withInvalidPricePerUnit: Boolean = false, withInvalidRowSum: Boolean = false): List<BaseBudgetEntry> =
        IntStream.range(1, 10).toList().map {
            object : BaseBudgetEntry {
                override val id = it.toLong()
                override val numberOfUnits = BigDecimal.ZERO
                override val pricePerUnit = BigDecimal.ZERO
                override val rowSum = BigDecimal.ZERO
            }
        }.let {
            it.plus(
                object : BaseBudgetEntry {
                    override val id = 20L
                    override val numberOfUnits = if (withInvalidNumberOfUnits) MAX_ALLOWED_BUDGET_VALUE.plus(BigDecimal.ONE) else BigDecimal.ZERO
                    override val pricePerUnit = if (withInvalidPricePerUnit) MAX_ALLOWED_BUDGET_VALUE.plus(BigDecimal.ONE) else BigDecimal.ZERO
                    override val rowSum = if (withInvalidRowSum) MAX_ALLOWED_BUDGET_VALUE.plus(BigDecimal.ONE) else BigDecimal.ZERO
                }
            )
        }
}
