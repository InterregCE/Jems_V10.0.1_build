package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudgetInteractor
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing.GetProjectBudgetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.toScaledBigDecimal
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
    lateinit var getProjectBudgetCoFinancingInteractor: GetProjectBudgetCoFinancingInteractor

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
            endDateStep1 = null,
            lengthOfPeriod = 6,
            isAdditionalFundAllowed = false,
            flatRates = setOf(
                ProjectCallFlatRate(type = FlatRateType.STAFF_COSTS, rate = 15, isAdjustable = true),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(
                    id = 32,
                    name = setOf(InputTranslation(SystemLanguage.EN, "LumpSum")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "pls 32")),
                    cost = BigDecimal.TEN,
                    splittingAllowed = false,
                    phase = ProgrammeLumpSumPhase.Preparation,
                    categories = setOf(BudgetCategory.EquipmentCosts, BudgetCategory.TravelAndAccommodationCosts),
                ),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(
                    id = 4,
                    name = setOf(InputTranslation(SystemLanguage.EN, "UnitCost")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "pus 4")),
                    type = setOf(InputTranslation(SystemLanguage.EN, "type of unit cost")),
                    costPerUnit = BigDecimal.ONE,
                    isOneCostCategory = false,
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
                endDateStep1 = null,
                lengthOfPeriod = 6,
                isAdditionalFundAllowed = false,
                flatRates = FlatRateSetupDTO(
                    staffCostFlatRateSetup = FlatRateDTO(15, true),
                ),
                lumpSums = listOf(
                    ProgrammeLumpSumDTO(
                        id = 32,
                        name = setOf(InputTranslation(SystemLanguage.EN, "LumpSum")),
                        description = setOf(InputTranslation(SystemLanguage.EN, "pls 32")),
                        cost = BigDecimal.TEN,
                        splittingAllowed = false,
                        phase = ProgrammeLumpSumPhase.Preparation,
                        categories = setOf(BudgetCategory.EquipmentCosts, BudgetCategory.TravelAndAccommodationCosts),
                    ),
                ),
                unitCosts = listOf(
                    ProgrammeUnitCostDTO(
                        id = 4,
                        name = setOf(InputTranslation(SystemLanguage.EN, "UnitCost")),
                        description = setOf(InputTranslation(SystemLanguage.EN, "pus 4")),
                        type = setOf(InputTranslation(SystemLanguage.EN, "type of unit cost")),
                        costPerUnit = BigDecimal.ONE,
                        isOneCostCategory = false,
                        categories = setOf(BudgetCategory.ExternalCosts, BudgetCategory.OfficeAndAdministrationCosts),
                    )
                ),
            )
        )
    }

    @Test
    fun `test partners sorting 1`() {
        val partnerBudget2 = PartnerBudget(
            partner = partner2,
            staffCosts = 4865.toScaledBigDecimal(),
            travelCosts = 9004.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            lumpSumContribution = 2787.toScaledBigDecimal(),
        )
        val partnerBudget1 = PartnerBudget(
            partner = partner1,
            staffCosts = 4865.toScaledBigDecimal(),
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            lumpSumContribution = 1213.toScaledBigDecimal(),
        )
        val projectBudgetList = listOf(partnerBudget2, partnerBudget1)

        every { getProjectBudgetInteractor.getBudget(1L) } returns projectBudgetList
        assertThat(controller.getProjectBudget(1L)).containsExactly(
            partnerBudget1.toDTO(),
            partnerBudget2.toDTO()
        )
    }
}
