package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOfficeAdministration
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetOfficeAdministrationRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class ProjectPartnerBudgetOfficeAdministrationServiceTest {

    @RelaxedMockK
    lateinit var projectPartnerBudgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository

    @RelaxedMockK
    lateinit var projectPartnerBudgetTravelRepository: ProjectPartnerBudgetTravelRepository

    @RelaxedMockK
    lateinit var projectPartnerBudgetExternalRepository: ProjectPartnerBudgetExternalRepository

    @RelaxedMockK
    lateinit var projectPartnerBudgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository

    @RelaxedMockK
    lateinit var projectPartnerBudgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository

    @MockK
    lateinit var repository: ProjectPartnerBudgetOfficeAdministrationRepository

    lateinit var projectPartnerBudgetService: ProjectPartnerBudgetService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectPartnerBudgetService = ProjectPartnerBudgetServiceImpl(
            projectPartnerBudgetStaffCostRepository,
            projectPartnerBudgetTravelRepository,
            projectPartnerBudgetExternalRepository,
            projectPartnerBudgetEquipmentRepository,
            projectPartnerBudgetInfrastructureRepository,
            repository
        )
    }

    @Test
    fun `get Office Administration flat rate`() {
        every { repository.findById(1) } returns Optional.of(ProjectPartnerBudgetOfficeAdministration(1, 10))

        assertThat(projectPartnerBudgetService.getOfficeAdministrationFlatRate(1))
            .isEqualTo(10)
    }

    @Test
    fun `get Office Administration flat rate not existing`() {
        every { repository.findById(2) } returns Optional.empty()

        assertThat(projectPartnerBudgetService.getOfficeAdministrationFlatRate(2)).isNull()
    }

    @Test
    fun `save Office Administration flat rate`() {
        every { repository.save(any<ProjectPartnerBudgetOfficeAdministration>()) } returnsArgument 0

        assertThat(projectPartnerBudgetService.updateOfficeAdministrationFlatRate(1 ,7)).isEqualTo(7)
    }

    @Test
    fun `save Office Administration flat rate null`() {
        every { repository.deleteById(any<Long>()) } returnsArgument 0

        assertThat(projectPartnerBudgetService.updateOfficeAdministrationFlatRate(1 , null)).isNull()

        val capturedId = slot<Long>()
        verify { repository.deleteById(capture(capturedId)) }
        assertThat(capturedId.captured).isEqualTo(1)
    }

}
