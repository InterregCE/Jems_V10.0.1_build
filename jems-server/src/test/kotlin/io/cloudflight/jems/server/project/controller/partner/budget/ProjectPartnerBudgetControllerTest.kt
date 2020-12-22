package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.programme.dto.ProgrammeFundOutputDTO
import io.cloudflight.jems.api.project.dto.partner.budget.InputGeneralBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputStaffCostBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputTravelBudget
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.AutomaticPublic
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.Private
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.Public
import io.cloudflight.jems.server.programme.service.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetService
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_options.UpdateBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing.GetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing.UpdateCoFinancingInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectPartnerBudgetControllerTest {

    companion object {
        private const val PARTNER_ID = 1L

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
    }

    @RelaxedMockK
    lateinit var projectPartnerBudgetService: ProjectPartnerBudgetService

    @RelaxedMockK
    lateinit var getBudgetOptionsInteractor: GetBudgetOptionsInteractor

    @RelaxedMockK
    lateinit var updateBudgetOptionsInteractor: UpdateBudgetOptionsInteractor

    @MockK
    lateinit var getCoFinancing: GetCoFinancingInteractor

    @MockK
    lateinit var updateCoFinancing: UpdateCoFinancingInteractor

    private lateinit var controller: ProjectPartnerBudgetController

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        controller = ProjectPartnerBudgetController(
            projectPartnerBudgetService,
            getBudgetOptionsInteractor,
            updateBudgetOptionsInteractor,
            getCoFinancing,
            updateCoFinancing
        )
    }

    @Test
    fun getBudgetStaffCosts() {
        val budgetCosts = emptyList<InputStaffCostBudget>()
        every { projectPartnerBudgetService.getStaffCosts(PARTNER_ID) } returns budgetCosts
        assertThat(controller.getBudgetStaffCosts(PARTNER_ID)).isEqualTo(budgetCosts)
    }

    @Test
    fun updateBudgetStaffCost() {
        val budgetCosts = emptyList<InputStaffCostBudget>()
        every { projectPartnerBudgetService.updateStaffCosts(PARTNER_ID, budgetCosts) } returns budgetCosts
        assertThat(controller.updateBudgetStaffCosts(PARTNER_ID, budgetCosts)).isEqualTo(budgetCosts)
    }

    @Test
    fun getBudgetTravel() {
        val travels = emptyList<InputTravelBudget>()
        every { projectPartnerBudgetService.getTravel(PARTNER_ID) } returns travels
        assertThat(controller.getBudgetTravel(PARTNER_ID)).isEqualTo(travels)
    }

    @Test
    fun updateBudgetTravel() {
        val travels = emptyList<InputTravelBudget>()
        every { projectPartnerBudgetService.updateTravel(PARTNER_ID, travels) } returns travels
        assertThat(controller.updateBudgetTravel(PARTNER_ID, travels)).isEqualTo(travels)
    }

    @Test
    fun getBudgetExternal() {
        val externals = emptyList<InputGeneralBudget>()
        every { projectPartnerBudgetService.getExternal(PARTNER_ID) } returns externals
        assertThat(controller.getBudgetExternal(PARTNER_ID)).isEqualTo(externals)
    }

    @Test
    fun updateBudgetExternal() {
        val externals = emptyList<InputGeneralBudget>()
        every { projectPartnerBudgetService.updateExternal(PARTNER_ID, externals) } returns externals
        assertThat(controller.updateBudgetExternal(PARTNER_ID, externals)).isEqualTo(externals)
    }

    @Test
    fun getBudgetEquipment() {
        val equipments = emptyList<InputGeneralBudget>()
        every { projectPartnerBudgetService.getEquipment(PARTNER_ID) } returns equipments
        assertThat(controller.getBudgetEquipment(PARTNER_ID)).isEqualTo(equipments)
    }

    @Test
    fun updateBudgetEquipment() {
        val equipments = emptyList<InputGeneralBudget>()
        every { projectPartnerBudgetService.updateEquipment(PARTNER_ID, equipments) } returns equipments
        assertThat(controller.updateBudgetEquipment(PARTNER_ID, equipments)).isEqualTo(equipments)
    }

    @Test
    fun getBudgetInfrastructure() {
        val infrastructures = emptyList<InputGeneralBudget>()
        every { projectPartnerBudgetService.getInfrastructure(PARTNER_ID) } returns infrastructures
        assertThat(controller.getBudgetInfrastructure(PARTNER_ID)).isEqualTo(infrastructures)
    }

    @Test
    fun updateBudgetInfrastructure() {
        val infrastructures = emptyList<InputGeneralBudget>()
        every { projectPartnerBudgetService.updateInfrastructure(PARTNER_ID, infrastructures) } returns infrastructures
        assertThat(controller.updateBudgetInfrastructure(PARTNER_ID, infrastructures)).isEqualTo(infrastructures)
    }

    @Test
    fun getTotal() {
        val total = BigDecimal.ONE
        every { projectPartnerBudgetService.getTotal(PARTNER_ID) } returns total
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
