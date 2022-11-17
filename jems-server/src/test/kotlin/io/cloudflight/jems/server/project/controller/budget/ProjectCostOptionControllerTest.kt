package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostListDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.createProjectUnitCost.CreateProjectUnitCostInteractor
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.deleteProjectUnitCost.DeleteProjectUnitCostInteractor
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectAvailableUnitCost.GetProjectAvailableUnitCostInteractor
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectUnitCostList.GetProjectUnitCostListInteractor
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.updateProjectUnitCost.UpdateProjectUnitCostInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ProjectCostOptionControllerTest : UnitTest() {

    companion object {
        private val unitCost = ProgrammeUnitCost(
            id = 84L,
            projectId = null,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "test type 1")),
            costPerUnit = BigDecimal.ONE,
            costPerUnitForeignCurrency = BigDecimal.TEN,
            foreignCurrencyCode = "PLN",
            isOneCostCategory = false,
            categories = setOf(BudgetCategory.OfficeAndAdministrationCosts, BudgetCategory.StaffCosts),
        )

        private val unitCostDto = ProgrammeUnitCostDTO(
            id = 84L,
            projectDefined = true,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "test type 1")),
            costPerUnit = BigDecimal.ONE,
            costPerUnitForeignCurrency = BigDecimal.TEN,
            foreignCurrencyCode = "PLN",
            oneCostCategory = false,
            categories = setOf(BudgetCategory.OfficeAndAdministrationCosts, BudgetCategory.StaffCosts),
        )

        private val unitCostListDto = ProgrammeUnitCostListDTO(
            id = 84L,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "test type 1")),
            costPerUnit = BigDecimal.ONE,
            categories = setOf(BudgetCategory.OfficeAndAdministrationCosts, BudgetCategory.StaffCosts),
        )
    }

    @MockK
    private lateinit var getProjectUnitCostListInteractor: GetProjectUnitCostListInteractor
    @MockK
    private lateinit var createProjectUnitCostInteractor: CreateProjectUnitCostInteractor
    @MockK
    private lateinit var updateProjectUnitCostInteractor: UpdateProjectUnitCostInteractor
    @MockK
    private lateinit var deleteProjectUnitCostInteractor: DeleteProjectUnitCostInteractor
    @MockK
    private lateinit var getProjectAvailableUnitCostInteractor: GetProjectAvailableUnitCostInteractor

    @InjectMockKs
    private lateinit var controller: ProjectCostOptionController

    @Test
    fun getProjectAvailableUnitCosts() {
        every { getProjectAvailableUnitCostInteractor.getAvailableUnitCost(14L, "2.0") } returns
            listOf(unitCost.copy(projectId = 14L))
        assertThat(controller.getProjectAvailableUnitCosts(14L, "2.0")).containsExactly(unitCostDto)
    }

    @Test
    fun getProjectUnitCostList() {
        every { getProjectUnitCostListInteractor.getUnitCostList(22L) } returns
            listOf(unitCost.copy(projectId = 22L))
        assertThat(controller.getProjectUnitCostList(22L)).containsExactly(unitCostListDto)
    }

    @Test
    fun getProjectUnitCost() {
        every { getProjectUnitCostListInteractor.getUnitCost(25L, 999L) } returns unitCost.copy(projectId = 25L)
        assertThat(controller.getProjectUnitCost(25L, 999L)).isEqualTo(unitCostDto)
    }

    @Test
    fun createProjectUnitCost() {
        val slotUnitCost = slot<ProgrammeUnitCost>()
        every { createProjectUnitCostInteractor.createProjectUnitCost(29L, capture(slotUnitCost)) } returnsArgument 1

        // we do not care about projectDefined flag when creating UnitCost
        assertThat(controller.createProjectUnitCost(29L, unitCostDto)).isEqualTo(unitCostDto.copy(projectDefined = false))
        assertThat(slotUnitCost.captured).isEqualTo(unitCost.copy())
    }

    @Test
    fun updateProjectUnitCost() {
        val slotUnitCost = slot<ProgrammeUnitCost>()
        every { updateProjectUnitCostInteractor.updateProjectUnitCost(31L, capture(slotUnitCost)) } returnsArgument 1

        // we do not care about projectDefined flag when updating UnitCost
        assertThat(controller.updateProjectUnitCost(31L, unitCostDto)).isEqualTo(unitCostDto.copy(projectDefined = false))
        assertThat(slotUnitCost.captured).isEqualTo(unitCost)
    }

    @Test
    fun deleteProjectUnitCost() {
        every { deleteProjectUnitCostInteractor.deleteProjectUnitCost(35L, unitCostId = 978L) } answers { }
        controller.deleteProjectUnitCost(35L, unitCostId = 978L)
        verify(exactly = 1) { deleteProjectUnitCostInteractor.deleteProjectUnitCost(35L, 978L) }
    }

}
