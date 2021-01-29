package io.cloudflight.jems.server.project.service.common

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.toScaledBigDecimal
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class BudgetCostCalculatorTest : UnitTest() {

    private val partnerId = 1L
    private val budgetCostCalculator = BudgetCostsCalculator()

    @Test
    fun `should calculate staffCosts correctly when staffCostsFlatRate and travelAndAccommodationOnStaffCostsFlatRate are set`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(
                partnerId,
                travelAndAccommodationOnStaffCostsFlatRate = 12,
                staffCostsFlatRate = 15
            ),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 9004.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = BigDecimal.ZERO
        )

        Assertions.assertEquals(3000.toScaledBigDecimal(), result.staffCosts)

    }

    @Test
    fun `should calculate staffCosts correctly when staffCostsFlatRate is set and travelAndAccommodationOnStaffCostsFlatRate is null`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId, staffCostsFlatRate = 15),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = BigDecimal.ZERO
        )
        Assertions.assertEquals(3300.toScaledBigDecimal(), result.staffCosts)
    }

    @Test
    fun `should calculate officeAndAdministrationCosts correctly when officeAndAdministrationOnStaffCostsFlatRate is set`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId, officeAndAdministrationOnStaffCostsFlatRate = 7),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 6200.toScaledBigDecimal(),
        )
        Assertions.assertEquals(434.toScaledBigDecimal(), result.officeAndAdministrationCosts)
    }

    @Test
    fun `should calculate officeAndAdministrationCosts correctly when officeAndAdministrationOnDirectCostsFlatRate is set`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId, officeAndAdministrationOnDirectCostsFlatRate = 10),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 6200.toScaledBigDecimal(),
        )
        Assertions.assertEquals(2820.toScaledBigDecimal(), result.officeAndAdministrationCosts)
    }

    @Test
    fun `should calculate officeAndAdministrationCosts correctly when officeAndAdministrationOnStaffCostsFlatRate is null`() {

        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 6200.toScaledBigDecimal(),
        )

        Assertions.assertEquals(BigDecimal.ZERO, result.officeAndAdministrationCosts)
    }

    @Test
    fun `should calculate officeAndAdministrationCosts correctly when officeAndAdministrationOnStaffCostsFlatRate and staffCostsFlatRate are set`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId,
                officeAndAdministrationOnDirectCostsFlatRate = 10,
                staffCostsFlatRate = 15,
            ),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 6200.toScaledBigDecimal(),
        )
        Assertions.assertEquals(2530.toScaledBigDecimal(), result.officeAndAdministrationCosts)
    }

    @Test
    fun `should calculate officeAndAdministrationCosts correctly when officeAndAdministrationOnStaffCostsFlatRate and travelAndAccommodationOnStaffCostsFlatRate are set`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId,
                officeAndAdministrationOnDirectCostsFlatRate = 10,
                travelAndAccommodationOnStaffCostsFlatRate = 15,
            ),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 6200.toScaledBigDecimal(),
        )
        Assertions.assertEquals(2713.toScaledBigDecimal(), result.officeAndAdministrationCosts)
    }

    @Test
    fun `should calculate officeAndAdministrationCosts correctly when officeAndAdministrationOnStaffCostsFlatRate, staffCostsFlatRate and travelAndAccommodationOnStaffCostsFlatRate are set`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId,
                officeAndAdministrationOnDirectCostsFlatRate = 10,
                travelAndAccommodationOnStaffCostsFlatRate = 15,
                staffCostsFlatRate = 20
            ),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(), // 600
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 6200.toScaledBigDecimal(), // 4000
        )
        Assertions.assertEquals(2460.toScaledBigDecimal(), result.officeAndAdministrationCosts)
    }


    @Test
    fun `should calculate otherCosts correctly when otherCostsOnStaffCostsFlatRate and staffCostsFlatRate are set`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(
                partnerId,
                staffCostsFlatRate = 7,
                otherCostsOnStaffCostsFlatRate = 12
            ),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 6200.toScaledBigDecimal(),
        )
        Assertions.assertEquals(BigDecimal.ZERO, result.otherCosts)
    }

    @Test
    fun `should calculate otherCosts correctly when otherCostsOnStaffCostsFlatRate is set and staffCostsFlatRate is null`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId, otherCostsOnStaffCostsFlatRate = 12),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 7500.toScaledBigDecimal(),
        )
        Assertions.assertEquals(900.toScaledBigDecimal(), result.otherCosts)
    }

    @Test
    fun `should calculate otherCosts correctly when otherCostsOnStaffCostsFlatRate is null`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId, staffCostsFlatRate = 12),
            unitCosts = BigDecimal.ZERO,
            lumpSumsCosts = BigDecimal.ZERO,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 6200.toScaledBigDecimal(),
        )
        Assertions.assertEquals(BigDecimal.ZERO, result.otherCosts)
    }

    @Test
    fun `should calculate totalCost correctly when no flatRate is set`() {
        val result = budgetCostCalculator.calculateCosts(
            budgetOptions = ProjectPartnerBudgetOptions(partnerId),
            unitCosts = BigDecimal.TEN,
            lumpSumsCosts = BigDecimal.ONE,
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            staffCosts = 6200.toScaledBigDecimal(),
        )
        Assertions.assertEquals(
            BigDecimal.TEN
                .plus(BigDecimal.ONE)
                .plus(2000.toScaledBigDecimal())
                .plus(10000.toScaledBigDecimal())
                .plus(7500.toScaledBigDecimal())
                .plus(2500.toScaledBigDecimal())
                .plus(6200.toScaledBigDecimal())
            , result.totalCosts
        )
    }

}
