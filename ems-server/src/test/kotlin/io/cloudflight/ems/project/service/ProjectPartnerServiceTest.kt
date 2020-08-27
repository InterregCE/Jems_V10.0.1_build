package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.project.dto.InputProjectPartner
import io.cloudflight.ems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.ems.api.project.dto.ProjectPartnerRole
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.project.entity.Project
import io.cloudflight.ems.project.entity.ProjectPartner
import io.cloudflight.ems.project.entity.ProjectStatus
import io.cloudflight.ems.project.repository.ProjectPartnerRepository
import io.cloudflight.ems.project.repository.ProjectRepository
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.user.entity.User
import io.cloudflight.ems.user.entity.UserRole
import io.cloudflight.ems.user.service.toOutputUserWithRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime
import java.util.Optional

internal class ProjectPartnerServiceTest {

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var securityService: SecurityService

    lateinit var projectPartnerService: ProjectPartnerService

    private val UNPAGED = Pageable.unpaged()

    private val userRole = UserRole(1, "ADMIN")
    private val user = User(
        id = 1,
        name = "Name",
        password = "hash",
        email = "admin@admin.dev",
        surname = "Surname",
        userRole = userRole)
    private val outputUser = user.toOutputUserWithRole()

    private val call = Call(
        id = 1,
        creator = user,
        name = "call",
        status = CallStatus.DRAFT,
        startDate = ZonedDateTime.now(),
        endDate = ZonedDateTime.now(),
        priorityPolicies = emptySet()
    )
    private val projectStatus = ProjectStatus(
        status = ProjectApplicationStatus.APPROVED,
        user = user,
        updated = ZonedDateTime.now())
    private val project = Project(
        id = 1,
        acronym = "acronym",
        call = call,
        applicant = user,
        projectStatus = projectStatus)
    private val projectPartner = ProjectPartner(
        id = 1,
        project = project,
        name = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER)

    private val outputProjectPartner = projectPartner.toOutputProjectPartner()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(outputUser, user.password, emptyList())
        projectPartnerService = ProjectPartnerServiceImpl(projectPartnerRepository, projectRepository)
    }

    @Test
    fun getById() {
        every { projectPartnerRepository.findOneById(0) } returns null
        every { projectPartnerRepository.findOneById(1) } returns projectPartner

        assertThrows<ResourceNotFoundException> { projectPartnerService.getById(0) }
        assertThat(projectPartnerService.getById(1)).isEqualTo(outputProjectPartner)
    }

    @Test
    fun findAllByProjectId() {
        every { projectPartnerRepository.findAllByProjectId(0, UNPAGED) } returns PageImpl(emptyList())
        every { projectPartnerRepository.findAllByProjectId(1, UNPAGED) } returns PageImpl(listOf(projectPartner))

        assertThat(projectPartnerService.findAllByProjectId(0, UNPAGED)).isEmpty()
        assertThat(projectPartnerService.findAllByProjectId(1, UNPAGED)).containsExactly(outputProjectPartner)
    }

    @Test
    fun createProjectPartner() {
        val inputProjectPartner = InputProjectPartner("partner", ProjectPartnerRole.LEAD_PARTNER)
        val projectPartnerWithProject = ProjectPartner(null, project, inputProjectPartner.name!!, inputProjectPartner.role!!)
        every { projectRepository.findOneById(0) } returns null
        every { projectRepository.findOneById(1) } returns project
        every { projectPartnerRepository.findAllByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER, UNPAGED) } returns Page.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner

        assertThrows<ResourceNotFoundException> { projectPartnerService.createProjectPartner(0, inputProjectPartner) }
        assertThat(projectPartnerService.createProjectPartner(1, inputProjectPartner)).isEqualTo(outputProjectPartner)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun `error on multiple LEAD_PARTNER partner creation attempt`() {
        val inputProjectPartner = InputProjectPartner("partner", ProjectPartnerRole.PARTNER)
        val inputProjectPartnerLead = InputProjectPartner("partner", ProjectPartnerRole.LEAD_PARTNER)
        val projectPartnerWithProject = ProjectPartner(null, project, inputProjectPartner.name!!, inputProjectPartner.role!!)
        every { projectRepository.findOneById(1) } returns project
        every { projectPartnerRepository.findAllByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER, UNPAGED) } returns PageImpl(listOf(projectPartnerWithProject))
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner

        // new with Partner role creation will work
        assertThat(projectPartnerService.createProjectPartner(1, inputProjectPartner)).isEqualTo(outputProjectPartner)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
        // but new Lead should fail
        assertThrows<I18nValidationException> { projectPartnerService.createProjectPartner(1, inputProjectPartnerLead) }
    }

    @Test
    fun update() {
        val projectPartnerUpdate = InputProjectPartnerUpdate(1, "updated", ProjectPartnerRole.PARTNER)
        val updatedProjectPartner = ProjectPartner(1, project, projectPartnerUpdate.name!!, projectPartnerUpdate.role!!)
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectRepository.findOneById(0) } returns null
        every { projectRepository.findOneById(1) } returns project
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner

        assertThrows<ResourceNotFoundException> { projectPartnerService.update(0, projectPartnerUpdate) }
        assertThat(projectPartnerService.update(1, projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartner())
    }

    @Test
    fun `update sort order of partners`() {
        val projectPartnerNonLead = ProjectPartner(id = 0, name = "p", role = ProjectPartnerRole.PARTNER, project = project)
        val projectPartners = listOf(projectPartner, projectPartnerNonLead)
        every { projectPartnerRepository.findAllByProjectId(1, any()) } returns PageImpl(projectPartners)
        val projectPartnerUpdated = projectPartner.copy(sortNumber = 1)
        val projectPartnerNonLeadUpdated = projectPartnerNonLead.copy(sortNumber = 2)
        every { projectPartnerRepository.save(projectPartnerUpdated) } returns projectPartnerUpdated
        every { projectPartnerRepository.save(projectPartnerNonLeadUpdated) } returns projectPartnerNonLeadUpdated

        projectPartnerService.updateSortByRole(1)
        verify {
            projectPartnerRepository.save(projectPartnerUpdated)
            projectPartnerRepository.save(projectPartnerNonLeadUpdated)
        }
    }

}
