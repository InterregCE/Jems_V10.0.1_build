package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.programme.dto.ProgrammeFundOutputDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetGeneralCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetStaffCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetTravelAndAccommodationCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.AutomaticPublic
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.Private
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.Public
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs.GetBudgetCosts
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs.UpdateBudgetStaffCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_equipment_costs.UpdateBudgetEquipmentCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_external_expertise_and_services.UpdateBudgetExternalExpertiseAndServicesCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_infrastructure_and_works_costs.UpdateBudgetInfrastructureAndWorksCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_options.UpdateBudgetOptions
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_travel_and_accommodation_costs.UpdateBudgetTravelAndAccommodationCosts
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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectPartnerBudgetControllerTest : UnitTest() {

    private val PARTNER_ID = 1L

    private val expectedDto = ProjectPartnerCoFinancingAndContributionOutputDTO(
        finances = listOf(
            ProjectPartnerCoFinancingOutputDTO(
                id = 10,
                percentage = 20,
                fund = ProgrammeFundOutputDTO(
                    id = 10,
                    selected = true,
                    abbreviation = "fund abbreviation",
                    description = "fund description"
                )
            ),
            ProjectPartnerCoFinancingOutputDTO(
                id = 11,
                percentage = 30,
                fund = ProgrammeFundOutputDTO(id = 2, selected = true /* abbreviation missing for ids 1..9 */)
            ),
            ProjectPartnerCoFinancingOutputDTO(id = 12, percentage = 50, fund = null)
        ),
        partnerContributions = listOf(
            ProjectPartnerContributionDTO(
                id = 21,
                name = "PartnerName",
                status = Public,
                isPartner = true,
                amount = BigDecimal.ONE
            ),
            ProjectPartnerContributionDTO(
                id = 22,
                name = "supporter 1",
                status = Private,
                isPartner = false,
                amount = BigDecimal.TEN
            ),
            ProjectPartnerContributionDTO(
                id = 23,
                name = "supporter 2",
                status = AutomaticPublic,
                isPartner = false,
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
            ProjectPartnerCoFinancing(id = 12, percentage = 50, fund = null),
            ProjectPartnerCoFinancing(
                id = 10,
                percentage = 20,
                fund = ProgrammeFund(
                    id = 10,
                    selected = true,
                    abbreviation = "fund abbreviation",
                    description = "fund description"
                )
            ),
            ProjectPartnerCoFinancing(
                id = 11,
                percentage = 30,
                fund = ProgrammeFund(id = 2, selected = true, abbreviation = "abbr 2")
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
    lateinit var getBudgetTotalCost: GetBudgetTotalCost

    @InjectMockKs
    private lateinit var controller: ProjectPartnerBudgetController


    @Test
    fun getBudgetCosts() {
        val budgetCosts = BudgetCosts(emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        every { getBudgetCosts.getBudgetCosts(PARTNER_ID) } returns budgetCosts
        assertThat(controller.getBudgetCosts(PARTNER_ID)).isEqualTo(budgetCosts.toBudgetCostsDTO())
    }

    @Test
    fun updateBudgetStaffCost() {
        val budgetCosts = emptyList<BudgetStaffCostEntryDTO>()
        every { updateBudgetStaffCosts.updateBudgetStaffCosts(PARTNER_ID, budgetCosts.toBudgetStaffCostEntryList()) } returns budgetCosts.toBudgetStaffCostEntryList()
        assertThat(controller.updateBudgetStaffCosts(PARTNER_ID, budgetCosts)).isEqualTo(budgetCosts)
    }

    @Test
    fun updateBudgetTravel() {
        val travels = emptyList<BudgetTravelAndAccommodationCostEntryDTO>()
        every { updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(PARTNER_ID, travels.toBudgetTravelAndAccommodationCostEntryList()) } returns travels.toBudgetTravelAndAccommodationCostEntryList()
        assertThat(controller.updateBudgetTravel(PARTNER_ID, travels)).isEqualTo(travels)
    }

    @Test
    fun updateBudgetExternal() {
        val externals = emptyList<BudgetGeneralCostEntryDTO>()
        every { updateBudgetExternalExpertiseAndServicesCosts.updateBudgetGeneralCosts(PARTNER_ID, externals.toBudgetGeneralCostEntryList()) } returns externals.toBudgetGeneralCostEntryList()
        assertThat(controller.updateBudgetExternal(PARTNER_ID, externals)).isEqualTo(externals)
    }

    @Test
    fun updateBudgetEquipment() {
        val equipments = emptyList<BudgetGeneralCostEntryDTO>()
        every { updateBudgetEquipmentCosts.updateBudgetGeneralCosts(PARTNER_ID, equipments.toBudgetGeneralCostEntryList()) } returns equipments.toBudgetGeneralCostEntryList()
        assertThat(controller.updateBudgetEquipment(PARTNER_ID, equipments)).isEqualTo(equipments)
    }

    @Test
    fun updateBudgetInfrastructure() {
        val infrastructures = emptyList<BudgetGeneralCostEntryDTO>()
        every { updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(PARTNER_ID, infrastructures.toBudgetGeneralCostEntryList()) } returns infrastructures.toBudgetGeneralCostEntryList()
        assertThat(controller.updateBudgetInfrastructure(PARTNER_ID, infrastructures)).isEqualTo(infrastructures)
    }

    @Test
    fun getTotal() {
        val total = BigDecimal.ONE
        every { getBudgetTotalCost.getBudgetTotalCost(PARTNER_ID) } returns total
        assertThat(controller.getTotal(PARTNER_ID)).isEqualTo(total)
    }

    @Test
    fun getProjectPartnerCoFinancing() {
        every { getCoFinancing.getCoFinancing(PARTNER_ID) } returns modelMock

        assertThat(controller.getProjectPartnerCoFinancing(PARTNER_ID))
            .isEqualTo(expectedDto)
    }

    @Test
    fun `getProjectPartnerCoFinancing - empty contributions`() {
        every { getCoFinancing.getCoFinancing(PARTNER_ID) } returns ProjectPartnerCoFinancingAndContribution(
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
                isPartner = true,
                amount = null
            )
        )
    }

    @Test
    fun updateProjectPartnerCoFinancing() {
        val inputFinances = listOf(
            ProjectPartnerCoFinancingInputDTO(id = 11, fundId = 2, percentage = 30),
            ProjectPartnerCoFinancingInputDTO(id = 10, fundId = 10, percentage = 20),
            ProjectPartnerCoFinancingInputDTO(id = 12, fundId = null, percentage = 50)
        )
        val inputPartnerContributions = expectedDto.partnerContributions

        val slotFinances = slot<Collection<UpdateProjectPartnerCoFinancing>>()
        val slotPartnerContributions = slot<List<ProjectPartnerContribution>>()

        every { updateCoFinancing.updateCoFinancing(PARTNER_ID, capture(slotFinances), capture(slotPartnerContributions)) } returns modelMock

        val result = controller.updateProjectPartnerCoFinancing(
            PARTNER_ID,
            ProjectPartnerCoFinancingAndContributionInputDTO(inputFinances, inputPartnerContributions)
        )

        assertThat(result).isEqualTo(expectedDto)

        // no matter the order
        assertThat(slotFinances.captured).containsExactlyInAnyOrder(
            UpdateProjectPartnerCoFinancing(id = 11, fundId = 2, percentage = 30),
            UpdateProjectPartnerCoFinancing(id = 10, fundId = 10, percentage = 20),
            UpdateProjectPartnerCoFinancing(id = 12, fundId = null, percentage = 50)
        )
        // order matters (to not mix lines after save)
        assertThat(slotPartnerContributions.captured).containsExactly(
            contribution1, contribution2, contribution3
        )
    }
}
