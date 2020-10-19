package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.audit.entity.Audit
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOfficeAdministration
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetOfficeAdministrationRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetServiceTest.Companion.projectPartner
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

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

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
            projectPartnerRepository,
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
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { repository.findById(1) } returns Optional.of(ProjectPartnerBudgetOfficeAdministration(1, 10))

        assertThat(projectPartnerBudgetService.getOfficeAdministrationFlatRate(1, 1))
            .isEqualTo(10)
    }

    @Test
    fun `get Office Administration flat rate not existing`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 2) } returns Optional.of(projectPartner)
        every { repository.findById(2) } returns Optional.empty()

        assertThat(projectPartnerBudgetService.getOfficeAdministrationFlatRate(1, 2)).isNull()
    }

    @Test
    fun `getOrSave Office Administration flat rate not visible project`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(2, 1) } returns Optional.empty()

        var exception = assertThrows<ResourceNotFoundException> {
            projectPartnerBudgetService.getOfficeAdministrationFlatRate(2, 1)
        }
        assertThat(exception.entity).isEqualTo("projectPartner")

        exception = assertThrows {
            projectPartnerBudgetService.updateOfficeAdministrationFlatRate(2, 1, null)
        }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun `save Office Administration flat rate`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { repository.save(any<ProjectPartnerBudgetOfficeAdministration>()) } returnsArgument 0

        assertThat(projectPartnerBudgetService.updateOfficeAdministrationFlatRate(1, 1 ,7)).isEqualTo(7)
    }

    @Test
    fun `save Office Administration flat rate null`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { repository.deleteById(any<Long>()) } returnsArgument 0

        assertThat(projectPartnerBudgetService.updateOfficeAdministrationFlatRate(1, 1 , null)).isNull()

        val capturedId = slot<Long>()
        verify { repository.deleteById(capture(capturedId)) }
        assertThat(capturedId.captured).isEqualTo(1)
    }

}
