package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudgetInteractor
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
class ProjectControllerTest {

    companion object {
        private val startDate = ZonedDateTime.now().minusDays(2)
        private val endDate = ZonedDateTime.now().plusDays(5)

        private val partner1 = ProjectPartner(
            id = 2,
            abbreviation = "Partner 1",
            role = ProjectPartnerRole.LEAD_PARTNER,
            sortNumber = 1,
            country = "AT",
        )

        private val partner2 = ProjectPartner(
            id = 1,
            abbreviation = "Partner 2",
            role = ProjectPartnerRole.PARTNER,
            sortNumber = 2,
            country = "CZ",
        )

        private val outputPartner1 = OutputProjectPartner(
            id = partner1.id!!,
            abbreviation = partner1.abbreviation,
            role = partner1.role,
            sortNumber = partner1.sortNumber,
            country = partner1.country,
        )

        private val outputPartner2 = OutputProjectPartner(
            id = partner2.id!!,
            abbreviation = partner2.abbreviation,
            role = partner2.role,
            sortNumber = partner2.sortNumber,
            country = partner2.country,
        )
    }

    @RelaxedMockK
    lateinit var projectService: ProjectService

    @MockK
    lateinit var getProjectBudgetInteractor: GetProjectBudgetInteractor

    @MockK
    lateinit var getProjectInteractor: GetProjectInteractor

    @InjectMockKs
    private lateinit var controller: ProjectController


    @Test
    fun getProjectCallSettings() {
        val callSettings = ProjectCallSettings(
            callId = 10,
            callName = "call for applications",
            startDate = startDate,
            endDate = endDate,
            lengthOfPeriod = 6,
            flatRates = setOf(
                ProjectCallFlatRate(type = FlatRateType.STAFF_COSTS, rate = 15, isAdjustable = true),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(
                    id = 32,
                    name = "LumpSum",
                    description = "pls 32",
                    cost = BigDecimal.TEN,
                    splittingAllowed = false,
                    phase = ProgrammeLumpSumPhase.Preparation,
                    categories = setOf(BudgetCategory.EquipmentCosts, BudgetCategory.TravelAndAccommodationCosts),
                ),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(
                    id = 4,
                    name = "UnitCost",
                    description = "pus 4",
                    type = "type of unit cost",
                    costPerUnit = BigDecimal.ONE,
                    categories = setOf(BudgetCategory.ExternalCosts, BudgetCategory.OfficeAndAdministrationCosts),
                ),
            ),
        )
        every { getProjectInteractor.getProjectCallSettings(1L) } returns callSettings
        assertThat(controller.getProjectCallSettingsById(1L)).isEqualTo(
            ProjectCallSettingsDTO(
                callId = 10,
                callName = "call for applications",
                startDate = startDate,
                endDate = endDate,
                lengthOfPeriod = 6,
                flatRates = FlatRateSetupDTO(
                    staffCostFlatRateSetup = FlatRateDTO(15, true),
                ),
                lumpSums = listOf(
                    ProgrammeLumpSumDTO(
                        id = 32,
                        name = "LumpSum",
                        description = "pls 32",
                        cost = BigDecimal.TEN,
                        splittingAllowed = false,
                        phase = ProgrammeLumpSumPhase.Preparation,
                        categories = setOf(BudgetCategory.EquipmentCosts, BudgetCategory.TravelAndAccommodationCosts),
                    ),
                ),
                unitCosts = listOf(
                    ProgrammeUnitCostDTO(
                        id = 4,
                        name = "UnitCost",
                        description = "pus 4",
                        type = "type of unit cost",
                        costPerUnit = BigDecimal.ONE,
                        categories = setOf(BudgetCategory.ExternalCosts, BudgetCategory.OfficeAndAdministrationCosts),
                    )
                ),
            )
        )
    }

    @Test
    fun `test various partner budget calculations and partners sorting 1`() {
        val projectBudget = listOf(
            PartnerBudget(
                partner = partner2,
                staffCostsFlatRate = 15,
                officeAndAdministrationOnStaffCostsFlatRate = 7,
                travelAndAccommodationOnStaffCostsFlatRate = 12,
                otherCostsOnStaffCostsFlatRate = null,
                staffCosts = toBd(4865), // should be ignored because of flat rate
                travelCosts = toBd(9004), // should be ignored because of flat rate
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
                lumpSumContribution = toBd(2787),
            ),
            PartnerBudget(
                partner = partner1,
                staffCostsFlatRate = 15,
                officeAndAdministrationOnStaffCostsFlatRate = 7,
                travelAndAccommodationOnStaffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
                staffCosts = toBd(4865), // should be ignored because of flat rate
                travelCosts = toBd(2000),
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
                lumpSumContribution = toBd(1213),
            ),
        )
        every { getProjectBudgetInteractor.getBudget(1L) } returns projectBudget
        assertThat(controller.getProjectBudget(1L)).containsExactly(
            ProjectPartnerBudgetDTO(
                partner = outputPartner1,
                staffCosts = toBd(3300), // as 15% from total
                travelCosts = toBd(2000),
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
                officeAndAdministrationCosts = toBd(231), // as 7% from 15% from total
                totalSum = toBd(26744),
                lumpSumContribution = toBd(1213),
            ),
            ProjectPartnerBudgetDTO(
                partner = outputPartner2,
                staffCosts = toBd(3000), // as 15% from total
                travelCosts = toBd(360), // as 12% from 15% from total
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
                officeAndAdministrationCosts = toBd(210), // as 7% from 15% from total
                totalSum = toBd(26357),
                lumpSumContribution = toBd(2787),
            ),
        )
    }

    @Test
    fun `test various partner budget calculations and partners sorting 2`() {
        val projectBudget = listOf(
            PartnerBudget(
                partner = partner2,
                staffCostsFlatRate = null,
                officeAndAdministrationOnStaffCostsFlatRate = 7,
                travelAndAccommodationOnStaffCostsFlatRate = 12,
                otherCostsOnStaffCostsFlatRate = null,
                staffCosts = toBd(6200),
                travelCosts = toBd(9004), // should be ignored because of flat rate
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
            ),
            PartnerBudget(
                partner = partner1,
                staffCostsFlatRate = null,
                officeAndAdministrationOnStaffCostsFlatRate = 7,
                travelAndAccommodationOnStaffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
                staffCosts = toBd(6200),
                travelCosts = toBd(2000),
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
            ),
        )
        every { getProjectBudgetInteractor.getBudget(1L) } returns projectBudget
        assertThat(controller.getProjectBudget(1L)).containsExactly(
            ProjectPartnerBudgetDTO(
                partner = outputPartner1,
                staffCosts = toBd(6200),
                travelCosts = toBd(2000),
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
                officeAndAdministrationCosts = toBd(434), // as 7% from staff
                totalSum = toBd(28634),
            ),
            ProjectPartnerBudgetDTO(
                partner = outputPartner2,
                staffCosts = toBd(6200),
                travelCosts = toBd(744), // as 12% from staff
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
                officeAndAdministrationCosts = toBd(434), // as 7% from staff
                totalSum = toBd(27378),
            ),
        )
    }

    private fun toBd(value: Double): BigDecimal {
        return BigDecimal.valueOf((value * 100).toLong(), 2)
    }

    private fun toBd(value: Int): BigDecimal {
        return toBd(value.toDouble())
    }

}
