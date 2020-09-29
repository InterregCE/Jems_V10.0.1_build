package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.project.dto.InputProjectPartnerContact
import io.cloudflight.ems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.ems.api.project.dto.InputProjectPartnerCreate
import io.cloudflight.ems.api.project.dto.InputProjectPartnerOrganization
import io.cloudflight.ems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.ems.api.project.dto.PartnerContactPersonType
import io.cloudflight.ems.api.project.dto.ProjectPartnerRole
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.project.entity.PartnerContactPerson
import io.cloudflight.ems.project.entity.Project
import io.cloudflight.ems.project.entity.ProjectPartner
import io.cloudflight.ems.project.entity.ProjectPartnerOrganization
import io.cloudflight.ems.project.entity.ProjectStatus
import io.cloudflight.ems.project.repository.ProjectPartnerOrganizationRepository
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import java.time.ZonedDateTime
import java.util.Optional
import kotlin.collections.HashSet

internal class ProjectPartnerServiceTest {

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var projectPartnerOrganizationRepository: ProjectPartnerOrganizationRepository

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
        priorityPolicies = emptySet(),
        strategies = emptySet(),
        funds = emptySet(),
        lengthOfPeriod = 1
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
    private val organization = ProjectPartnerOrganization(
        1,
        "test",
        "test",
        "test")
    private val projectPartnerWithOrganization = ProjectPartner(
        id = 1,
        project = project,
        name = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        organization = organization)

    private val outputProjectPartner = projectPartner.toOutputProjectPartner()
    private val outputProjectPartnerDetail = projectPartner.toOutputProjectPartnerDetail()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(outputUser, user.password, emptyList())
        projectPartnerService = ProjectPartnerServiceImpl(projectPartnerRepository, projectRepository, projectPartnerOrganizationRepository)
    }

    @Test
    fun getById() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 0) } returns Optional.empty()
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)

        assertThrows<ResourceNotFoundException> { projectPartnerService.getById(1,0) }
        assertThat(projectPartnerService.getById(1, 1)).isEqualTo(outputProjectPartnerDetail)
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
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER)
        val projectPartnerWithProject = ProjectPartner(null, project, inputProjectPartner.name!!, inputProjectPartner.role!!)
        every { projectRepository.findById(0) } returns Optional.empty()
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner

        assertThrows<ResourceNotFoundException> { projectPartnerService.create(0, inputProjectPartner) }
        assertThat(projectPartnerService.create(1, inputProjectPartner)).isEqualTo(outputProjectPartnerDetail)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun `error on multiple LEAD_PARTNER partner creation attempt`() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.PARTNER)
        val inputProjectPartnerLead = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER)
        val projectPartnerWithProject = ProjectPartner(null, project, inputProjectPartner.name!!, inputProjectPartner.role!!)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.of(projectPartnerWithProject)
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner

        // new with Partner role creation will work
        assertThat(projectPartnerService.create(1, inputProjectPartner)).isEqualTo(outputProjectPartnerDetail)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
        // but new Lead should fail
        assertThrows<I18nValidationException> { projectPartnerService.create(1, inputProjectPartnerLead) }
    }

    @Test
    fun updateProjectPartner() {
        val projectPartnerUpdate = InputProjectPartnerUpdate(1, "updated", ProjectPartnerRole.PARTNER)
        val updatedProjectPartner = ProjectPartner(1, project, projectPartnerUpdate.name!!, projectPartnerUpdate.role!!)
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner

        assertThat(projectPartnerService.update(1, projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun `update sort order of partners`() {
        val projectPartnerNonLead = ProjectPartner(id = 0, name = "p", role = ProjectPartnerRole.PARTNER, project = project)
        val projectPartners = listOf(projectPartner, projectPartnerNonLead)
        every { projectPartnerRepository.findAllByProjectId(1, any<Sort>()) } returns projectPartners
        val projectPartnerUpdated = projectPartner.copy(sortNumber = 1)
        val projectPartnerNonLeadUpdated = projectPartnerNonLead.copy(sortNumber = 2)
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartner>>()) } returnsArgument 0

        projectPartnerService.updateSortByRole(1)
        verify {
            projectPartnerRepository.saveAll(listOf(projectPartnerUpdated, projectPartnerNonLeadUpdated))
        }
    }

    @Test
    fun updatePartnerContact() {
        val projectPartnerContactUpdate = InputProjectPartnerContact(
            "test",
            PartnerContactPersonType.ContactPerson,
            "test",
            "test",
            "test@ems.eu",
            "test")
        val projectPartner = ProjectPartner(1, project, "updated", ProjectPartnerRole.PARTNER)
        val contactPersonsEntity = HashSet<PartnerContactPerson>()
            contactPersonsEntity.add(projectPartnerContactUpdate.toEntity(projectPartner))
        val updatedProjectPartner = ProjectPartner(1, project, "updated", ProjectPartnerRole.PARTNER,
            null, contactPersonsEntity)
        val contactPersonsDto = HashSet<InputProjectPartnerContact>()
            contactPersonsDto.add(projectPartnerContactUpdate)

        every { projectPartnerRepository.findFirstByProjectIdAndId(1,1) } returns Optional.of(projectPartner)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner

        assertThat(projectPartnerService.updatePartnerContact(1,1, contactPersonsDto))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun updatePartnerContact_notExisting() {
        val projectPartnerContactUpdate = InputProjectPartnerContact(
            "test",
            PartnerContactPersonType.LegalRepresentative,
            "test",
            "test",
            "test@ems.eu",
            "test")
        val contactPersonsDto = HashSet<InputProjectPartnerContact>()
            contactPersonsDto.add(projectPartnerContactUpdate)
        every { projectPartnerRepository.findFirstByProjectIdAndId(1,eq(-1)) } returns Optional.empty()
        val exception = assertThrows<ResourceNotFoundException> { projectPartnerService.updatePartnerContact(1,-1, contactPersonsDto) }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun updatePartnerContribution() {
        val projectPartnerContributionUpdate = InputProjectPartnerContribution(
            "test",
            "test",
            "test")
        val projectPartner = ProjectPartner(1, project, "updated", ProjectPartnerRole.PARTNER)
        val updatedProjectPartner = ProjectPartner(1, project, "updated", ProjectPartnerRole.PARTNER,
            null, emptySet(), projectPartnerContributionUpdate.toEntity(projectPartner))

        every { projectPartnerRepository.findFirstByProjectIdAndId(1,1) } returns Optional.of(projectPartner)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner

        assertThat(projectPartnerService.updatePartnerContribution(1,1, projectPartnerContributionUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun updatePartnerContribution_notExisting() {
        val projectPartnerContributionUpdate = InputProjectPartnerContribution(
            "test",
            "test",
            "test")
        every { projectPartnerRepository.findFirstByProjectIdAndId(1,eq(-1)) } returns Optional.empty()
        val exception = assertThrows<ResourceNotFoundException> { projectPartnerService.updatePartnerContribution(1,-1, projectPartnerContributionUpdate) }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun createProjectPartnerWithOrganization() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, null,
            InputProjectPartnerOrganization(null, "test", "test", "test"))
        val projectPartnerWithProject = ProjectPartner(null, project, inputProjectPartner.name!!, inputProjectPartner.role!!, null,
            emptySet(), null, organization)
        every { projectRepository.findById(0) } returns Optional.empty()
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartnerWithOrganization
        every { projectPartnerOrganizationRepository.save(inputProjectPartner.organization!!.toEntity()) } returns organization

        assertThrows<ResourceNotFoundException> { projectPartnerService.create(0, inputProjectPartner) }
        assertThat(projectPartnerService.create(1, inputProjectPartner)).isEqualTo(projectPartnerWithOrganization.toOutputProjectPartnerDetail())
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun updateProjectPartnerWithOrganization() {
        val projectPartnerUpdate =  InputProjectPartnerUpdate(1, "updated", ProjectPartnerRole.PARTNER, null,
            InputProjectPartnerOrganization(null, "test", "test", "test"))
        val updatedProjectPartner = ProjectPartner(1, project, projectPartnerUpdate.name!!, projectPartnerUpdate.role!!, null,
            emptySet(), null, organization)
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { projectPartnerOrganizationRepository.findByIdOrNull(1) } returns organization
        every { projectPartnerOrganizationRepository.save(projectPartnerUpdate.organization!!.toEntity()) } returns organization

        assertThat(projectPartnerService.update(1, projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

}
