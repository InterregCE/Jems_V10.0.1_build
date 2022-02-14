package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetGeneralCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetPeriodDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetSpfCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetStaffCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetTravelAndAccommodationCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetUnitCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.AutomaticPublic
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Private
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Public
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs.GetBudgetCosts
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.budget.updateBudgetSpfCosts.UpdateBudgetSpfCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs.UpdateBudgetStaffCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_equipment_costs.UpdateBudgetEquipmentCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_external_expertise_and_services.UpdateBudgetExternalExpertiseAndServicesCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_infrastructure_and_works_costs.UpdateBudgetInfrastructureAndWorksCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_options.UpdateBudgetOptions
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_travel_and_accommodation_costs.UpdateBudgetTravelAndAccommodationCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_unit_costs.UpdateBudgetUnitCosts
import io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing.GetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing.UpdateCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectPartnerBudgetControllerTest : UnitTest() {

    private val PARTNER_ID = 1L

    private val expectedDto = ProjectPartnerCoFinancingAndContributionOutputDTO(
        finances = listOf(
            ProjectPartnerCoFinancingOutputDTO(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                percentage = BigDecimal.valueOf(20.5),
                fund = ProgrammeFundDTO(
                    id = 10,
                    selected = true,
                )
            ),
            ProjectPartnerCoFinancingOutputDTO(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(30.5),
                fund = ProgrammeFundDTO(id = 2, selected = true /* abbreviation missing for ids 1..9 */)
            ),
            ProjectPartnerCoFinancingOutputDTO(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(50.5),
                fund = null
            )
        ),
        partnerContributions = listOf(
            ProjectPartnerContributionDTO(
                id = 21,
                name = "PartnerName",
                status = Public,
                partner = true,
                amount = BigDecimal.ONE
            ),
            ProjectPartnerContributionDTO(
                id = 22,
                name = "supporter 1",
                status = Private,
                partner = false,
                amount = BigDecimal.TEN
            ),
            ProjectPartnerContributionDTO(
                id = 23,
                name = "supporter 2",
                status = AutomaticPublic,
                partner = false,
                amount = BigDecimal.TEN
            )
        )
    )

    private val contribution1 = ProjectPartnerContribution(
        id = 21,
        name = null,
        status = Public,
        isPartner = true,
        amount = BigDecimal.ONE
    )

    private val contribution2 = ProjectPartnerContribution(
        id = 22,
        name = "supporter 1",
        status = Private,
        isPartner = false,
        amount = BigDecimal.TEN
    )

    private val contribution3 = ProjectPartnerContribution(
        id = 23,
        name = "supporter 2",
        status = AutomaticPublic,
        isPartner = false,
        amount = BigDecimal.TEN
    )

    private val modelMock = ProjectPartnerCoFinancingAndContribution(
        finances = listOf(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                percentage = BigDecimal.valueOf(20.5),
                fund = ProgrammeFund(
                    id = 10,
                    selected = true,
                )
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(30.5),
                fund = ProgrammeFund(id = 2, selected = true)
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(50.5),
                fund = null
            )
        ),
        partnerContributions = listOf(contribution3, contribution2, contribution1),
        partnerAbbreviation = "PartnerName"
    )

    @MockK
    lateinit var getBudgetOptions: GetBudgetOptionsInteractor

    @MockK
    lateinit var updateBudgetOptions: UpdateBudgetOptions

    @MockK
    lateinit var getCoFinancing: GetCoFinancingInteractor

    @MockK
    lateinit var updateCoFinancing: UpdateCoFinancing

    @MockK
    lateinit var getBudgetCosts: GetBudgetCosts

    @MockK
    lateinit var updateBudgetEquipmentCosts: UpdateBudgetEquipmentCosts

    @MockK
    lateinit var updateBudgetTravelAndAccommodationCosts: UpdateBudgetTravelAndAccommodationCosts

    @MockK
    lateinit var updateBudgetExternalExpertiseAndServicesCosts: UpdateBudgetExternalExpertiseAndServicesCosts

    @MockK
    lateinit var updateBudgetInfrastructureAndWorksCosts: UpdateBudgetInfrastructureAndWorksCosts

    @MockK
    lateinit var updateBudgetStaffCosts: UpdateBudgetStaffCosts

    @MockK
    lateinit var updateBudgetUnitCosts: UpdateBudgetUnitCosts

    @MockK
    lateinit var updateBudgetSpfCosts: UpdateBudgetSpfCosts

    @MockK
    lateinit var getBudgetTotalCost: GetBudgetTotalCost

    @InjectMockKs
    private lateinit var controller: ProjectPartnerBudgetController


    @Test
    fun getBudgetCosts() {
        val budgetCosts = BudgetCosts(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        every { getBudgetCosts.getBudgetCosts(PARTNER_ID) } returns budgetCosts
        assertThat(controller.getBudgetCosts(PARTNER_ID)).isEqualTo(budgetCosts.toBudgetCostsDTO())
    }

    @Test
    fun updateBudgetStaffCost() {
        val budgetCosts = listOf(
            BudgetStaffCostEntryDTO(
                id = 1,
                numberOfUnits = BigDecimal.ONE,
                pricePerUnit = BigDecimal.TEN,
                rowSum = BigDecimal.TEN,
                budgetPeriods = setOf(BudgetPeriodDTO(1, BigDecimal.ONE)),
                unitCostId = 1
            )
        )
        every {
            updateBudgetStaffCosts.updateBudgetStaffCosts(
                PARTNER_ID,
                any()
            )
        } returns budgetCosts.toBudgetStaffCostEntryList()
        assertThat(controller.updateBudgetStaffCosts(PARTNER_ID, budgetCosts)).isEqualTo(budgetCosts)
    }

    @Test
    fun updateBudgetTravel() {
        val travels = listOf(
            BudgetTravelAndAccommodationCostEntryDTO(
                id = 1,
                numberOfUnits = BigDecimal.ONE,
                pricePerUnit = BigDecimal.TEN,
                rowSum = BigDecimal.TEN,
                budgetPeriods = emptySet(),
                unitCostId = 1
            )
        )
        every {
            updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(
                PARTNER_ID,
                any()
            )
        } returns travels.toBudgetTravelAndAccommodationCostEntryList()
        assertThat(controller.updateBudgetTravel(PARTNER_ID, travels)).isEqualTo(travels)
    }

    @Test
    fun updateBudgetExternal() {
        val externals = listOf(
            BudgetGeneralCostEntryDTO(
                id = 1,
                numberOfUnits = BigDecimal.ONE,
                pricePerUnit = BigDecimal.TEN,
                rowSum = BigDecimal.TEN,
                budgetPeriods = emptySet(),
                unitCostId = 1
            )
        )
        every {
            updateBudgetExternalExpertiseAndServicesCosts.updateBudgetGeneralCosts(
                PARTNER_ID,
                any(),
                BudgetCategory.ExternalCosts
            )
        } returns externals.toBudgetGeneralCostEntryList()
        assertThat(controller.updateBudgetExternal(PARTNER_ID, externals)).isEqualTo(externals)
    }

    @Test
    fun updateBudgetEquipment() {
        val equipments = emptyList<BudgetGeneralCostEntryDTO>()
        every {
            updateBudgetEquipmentCosts.updateBudgetGeneralCosts(
                PARTNER_ID,
                equipments.toBudgetGeneralCostEntryList(),
                BudgetCategory.EquipmentCosts
            )
        } returns equipments.toBudgetGeneralCostEntryList()
        assertThat(controller.updateBudgetEquipment(PARTNER_ID, equipments)).isEqualTo(equipments)
    }

    @Test
    fun updateBudgetInfrastructure() {
        val infrastructures = emptyList<BudgetGeneralCostEntryDTO>()
        every {
            updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(
                PARTNER_ID,
                infrastructures.toBudgetGeneralCostEntryList(),
                BudgetCategory.InfrastructureCosts
            )
        } returns infrastructures.toBudgetGeneralCostEntryList()
        assertThat(controller.updateBudgetInfrastructure(PARTNER_ID, infrastructures)).isEqualTo(infrastructures)
    }

    @Test
    fun updateBudgetUnitCosts() {
        val unitCosts = listOf(
            BudgetUnitCostEntryDTO(
                id = 1,
                numberOfUnits = BigDecimal.ONE,
                rowSum = BigDecimal.TEN,
                budgetPeriods = emptySet(),
                unitCostId = 1
            )
        )
        every {
            updateBudgetUnitCosts.updateBudgetUnitCosts(
                PARTNER_ID,
                any()
            )
        } returns unitCosts.toBudgetUnitCostEntryList()
        assertThat(controller.updateBudgetUnitCosts(PARTNER_ID, unitCosts)).isEqualTo(unitCosts)
    }

    @Test
    fun updateBudgetSpfCosts() {
        val spfCosts = listOf(
            BudgetSpfCostEntryDTO(
                id = 1,
                numberOfUnits = BigDecimal.ONE,
                pricePerUnit = BigDecimal.TEN,
                rowSum = BigDecimal.TEN,
                budgetPeriods = emptySet(),
                unitCostId = 1
            )
        )
        every {
            updateBudgetSpfCosts.updateBudgetSpfCosts(PARTNER_ID, any())
        } returns spfCosts.toBudgetSpfCostEntryList()
        assertThat(controller.updateBudgetSpfCosts(PARTNER_ID, spfCosts)).isEqualTo(spfCosts)
    }

    @Test
    fun updateBudgetOptions() {
        val options = ProjectPartnerBudgetOptionsDto(
            officeAndAdministrationOnStaffCostsFlatRate = 1,
            travelAndAccommodationOnStaffCostsFlatRate = 1
        )
        every { updateBudgetOptions.updateBudgetOptions(PARTNER_ID, options.toProjectPartnerBudgetOptions(PARTNER_ID)) } answers {}
        controller.updateBudgetOptions(PARTNER_ID, options)
        verify(exactly = 1) { updateBudgetOptions.updateBudgetOptions(PARTNER_ID, options.toProjectPartnerBudgetOptions(PARTNER_ID)) }
    }

    @Test
    fun getTotal() {
        val total = BigDecimal.ONE
        every { getBudgetTotalCost.getBudgetTotalCost(PARTNER_ID) } returns total
        assertThat(controller.getTotal(PARTNER_ID)).isEqualTo(total)
    }

    @Test
    fun getProjectPartnerCoFinancing() {
        every { getCoFinancing.getCoFinancing(PARTNER_ID, null) } returns modelMock

        assertThat(controller.getProjectPartnerCoFinancing(PARTNER_ID))
            .isEqualTo(expectedDto)
    }

    @Test
    fun `getProjectPartnerCoFinancing - empty contributions`() {
        every { getCoFinancing.getCoFinancing(PARTNER_ID, null) } returns ProjectPartnerCoFinancingAndContribution(
            finances = emptyList(),
            partnerContributions = emptyList(),
            partnerAbbreviation = "test abbr"
        )

        val result = controller.getProjectPartnerCoFinancing(PARTNER_ID)

        assertThat(result.finances).isEmpty()
        assertThat(result.partnerContributions).containsExactly(
            ProjectPartnerContributionDTO(
                id = null,
                name = "test abbr",
                status = null,
                partner = true,
                amount = null
            )
        )
    }

    @Test
    fun updateProjectPartnerCoFinancing() {
        val inputFinances = listOf(
            ProjectPartnerCoFinancingInputDTO(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fundId = 2,
                percentage = BigDecimal.valueOf(30.5)
            ),
            ProjectPartnerCoFinancingInputDTO(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fundId = 10,
                percentage = BigDecimal.valueOf(20.5)
            ),
            ProjectPartnerCoFinancingInputDTO(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fundId = null,
                percentage = BigDecimal.valueOf(50.5)
            )
        )
        val inputPartnerContributions = expectedDto.partnerContributions

        val slotFinances = slot<List<UpdateProjectPartnerCoFinancing>>()
        val slotPartnerContributions = slot<List<ProjectPartnerContribution>>()

        every {
            updateCoFinancing.updateCoFinancing(
                PARTNER_ID,
                capture(slotFinances),
                capture(slotPartnerContributions)
            )
        } returns modelMock

        val result = controller.updateProjectPartnerCoFinancing(
            PARTNER_ID,
            ProjectPartnerCoFinancingAndContributionInputDTO(inputFinances, inputPartnerContributions)
        )

        assertThat(result).isEqualTo(expectedDto)

        // no matter the order
        assertThat(slotFinances.captured).containsExactlyInAnyOrder(
            UpdateProjectPartnerCoFinancing(
                fundId = 2,
                percentage = BigDecimal.valueOf(30.5)
            ),
            UpdateProjectPartnerCoFinancing(
                fundId = 10,
                percentage = BigDecimal.valueOf(20.5)
            ),
            UpdateProjectPartnerCoFinancing(
                fundId = null,
                percentage = BigDecimal.valueOf(50.5)
            )
        )
        // order matters (to not mix lines after save)
        assertThat(slotPartnerContributions.captured).containsExactly(
            contribution1, contribution2, contribution3
        )
    }
}
