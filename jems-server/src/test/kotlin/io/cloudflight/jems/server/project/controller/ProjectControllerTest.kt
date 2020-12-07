package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudgetInteractor
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectControllerTest {

    companion object {
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

    private lateinit var controller: ProjectController

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        controller = ProjectController(
            projectService,
            getProjectBudgetInteractor,
        )
    }

    @Test
    fun `test various partner budget calculations and partners sorting 1`() {
        val projectBudget = listOf(
            PartnerBudget(
                partner = partner2,
                staffCostsFlatRate = 15,
                officeOnStaffFlatRate = 7,
                travelOnStaffFlatRate = 12,
                staffCosts = toBd(4865), // should be ignored because of flat rate
                travelCosts = toBd(9004), // should be ignored because of flat rate
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
            ),
            PartnerBudget(
                partner = partner1,
                staffCostsFlatRate = 15,
                officeOnStaffFlatRate = 7,
                travelOnStaffFlatRate = null,
                staffCosts = toBd(4865), // should be ignored because of flat rate
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
                staffCosts = toBd(3300), // as 15% from total
                travelCosts = toBd(2000),
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
                officeAndAdministrationCosts = toBd(231), // as 7% from 15% from total
                totalSum = toBd(25531),
            ),
            ProjectPartnerBudgetDTO(
                partner = outputPartner2,
                staffCosts = toBd(3000), // as 15% from total
                travelCosts = toBd(360), // as 12% from 15% from total
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
                officeAndAdministrationCosts = toBd(210), // as 7% from 15% from total
                totalSum = toBd(23570),
            ),
        )
    }

    @Test
    fun `test various partner budget calculations and partners sorting 2`() {
        val projectBudget = listOf(
            PartnerBudget(
                partner = partner2,
                staffCostsFlatRate = null,
                officeOnStaffFlatRate = 7,
                travelOnStaffFlatRate = 12,
                staffCosts = toBd(6200),
                travelCosts = toBd(9004), // should be ignored because of flat rate
                externalCosts = toBd(10000),
                equipmentCosts = toBd(7500),
                infrastructureCosts = toBd(2500),
            ),
            PartnerBudget(
                partner = partner1,
                staffCostsFlatRate = null,
                officeOnStaffFlatRate = 7,
                travelOnStaffFlatRate = null,
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
