package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerContactType
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.exception.I18nValidationException
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerService
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerServiceImpl
import io.cloudflight.jems.server.project.service.partner.toEntity
import io.cloudflight.jems.server.project.service.partner.toOutputProjectPartner
import io.cloudflight.jems.server.project.service.partner.toOutputProjectPartnerDetail
import io.cloudflight.jems.server.security.model.LocalCurrentUser
import io.cloudflight.jems.server.security.service.SecurityService
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.cloudflight.jems.server.user.service.toOutputUserWithRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER)
    private val projectPartnerWithOrganization = ProjectPartner(
        id = 1,
        project = project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        department = "test"
    )
    private fun partner(id: Long, role: ProjectPartnerRole) = projectPartnerWithOrganization
        .copy(
            id = id,
            role = role
        )

    private val outputProjectPartner = projectPartner.toOutputProjectPartner()
    private val outputProjectPartnerDetail = projectPartner.toOutputProjectPartnerDetail()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(outputUser, user.password, emptyList())
        projectPartnerService = ProjectPartnerServiceImpl(projectPartnerRepository, projectRepository)
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
        val projectPartnerWithProject = ProjectPartner(null, project, inputProjectPartner.abbreviation!!, inputProjectPartner.role!!)
        every { projectRepository.findById(0) } returns Optional.empty()
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findAllByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartner>>()) } returnsArgument 0

        assertThrows<ResourceNotFoundException> { projectPartnerService.create(0, inputProjectPartner) }
        assertThat(projectPartnerService.create(1, inputProjectPartner)).isEqualTo(outputProjectPartnerDetail)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun `error on multiple LEAD_PARTNER partner creation attempt`() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.PARTNER)
        val inputProjectPartnerLead = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER)
        val projectPartnerWithProject = ProjectPartner(null, project, inputProjectPartner.abbreviation!!, inputProjectPartner.role!!)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.of(projectPartnerWithProject)
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findAllByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartner>>()) } returnsArgument 0

        // new with Partner role creation will work
        assertThat(projectPartnerService.create(1, inputProjectPartner)).isEqualTo(outputProjectPartnerDetail)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
        // but new Lead should fail
        assertThrows<I18nValidationException> { projectPartnerService.create(1, inputProjectPartnerLead) }
    }

    @Test
    fun updateProjectPartner() {
        val projectPartnerUpdate = InputProjectPartnerUpdate(1, "updated", ProjectPartnerRole.PARTNER)
        val updatedProjectPartner = ProjectPartner(1, project, projectPartnerUpdate.abbreviation!!, projectPartnerUpdate.role!!)
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner

        assertThat(projectPartnerService.update(1, projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun `updateProjectPartner to lead when no other leads`() {
        val projectPartnerUpdate = InputProjectPartnerUpdate(3, "updated", ProjectPartnerRole.LEAD_PARTNER)
        val updatedProjectPartner = ProjectPartner(3, project, projectPartnerUpdate.abbreviation!!, projectPartnerUpdate.role!!)
        // we are changing partner to Lead Partner
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 3) } returns Optional.of(projectPartner.copy(id = 3, role = ProjectPartnerRole.PARTNER))
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        // to update role of Partner (id=3):
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        // to update sort-numbers for both Partners:
        val projectPartners = listOf(partner(3, ProjectPartnerRole.LEAD_PARTNER), partner(2, ProjectPartnerRole.PARTNER))
        every { projectPartnerRepository.findAllByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartner>>()) } returnsArgument 0

        projectPartnerService.update(1, projectPartnerUpdate)

        val updatedPartners = slot<Iterable<ProjectPartner>>()
        verify { projectPartnerRepository.saveAll(capture(updatedPartners)) }
        assertThat(updatedPartners.captured)
            .isEqualTo(listOf(
                partner(3, ProjectPartnerRole.LEAD_PARTNER).copy(sortNumber = 1),
                partner(2, ProjectPartnerRole.PARTNER).copy(sortNumber = 2)
            ))
    }

    @Test
    fun updatePartnerContact() {
        val projectPartnerContactUpdate = InputProjectPartnerContact(
            "test",
            ProjectPartnerContactType.ContactPerson,
            "test",
            "test",
            "test@ems.eu",
            "test")
        val projectPartner = ProjectPartner(1, project, "updated", ProjectPartnerRole.PARTNER)
        val contactPersonsEntity = setOf(projectPartnerContactUpdate.toEntity(projectPartner))
        val updatedProjectPartner = ProjectPartner(id = 1, project =  project, abbreviation = "updated", role = ProjectPartnerRole.PARTNER,
            contacts = contactPersonsEntity)

        every { projectPartnerRepository.findFirstByProjectIdAndId(1,1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner

        assertThat(projectPartnerService.updatePartnerContacts(1,1, setOf(projectPartnerContactUpdate)))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun updatePartnerContact_notExisting() {
        val projectPartnerContactUpdate = InputProjectPartnerContact(
            "test",
            ProjectPartnerContactType.LegalRepresentative,
            "test",
            "test",
            "test@ems.eu",
            "test")
        val contactPersonsDto = setOf(projectPartnerContactUpdate)
        every { projectPartnerRepository.findFirstByProjectIdAndId(1,eq(-1)) } returns Optional.empty()
        val exception = assertThrows<ResourceNotFoundException> { projectPartnerService.updatePartnerContacts(1,-1, contactPersonsDto) }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun updatePartnerContribution() {
        val projectPartnerContributionUpdate = InputProjectPartnerContribution(
            "test",
            "test",
            "test")
        val projectPartner = ProjectPartner(1, project, "updated", ProjectPartnerRole.PARTNER)
        val updatedProjectPartner = ProjectPartner(id = 1, project = project, abbreviation = "updated", role = ProjectPartnerRole.PARTNER,
            partnerContribution = projectPartnerContributionUpdate.toEntity(projectPartner))

        every { projectPartnerRepository.findFirstByProjectIdAndId(1,1) } returns Optional.of(projectPartner)
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
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, null, "test", "test", "test")
        val projectPartnerWithProject = ProjectPartner(
            project = project,
            abbreviation = inputProjectPartner.abbreviation!!,
            role =  inputProjectPartner.role!!,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            department = projectPartnerWithOrganization.department
        )
        every { projectRepository.findById(0) } returns Optional.empty()
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartnerWithOrganization
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findAllByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartner>>()) } returnsArgument 0

        assertThrows<ResourceNotFoundException> { projectPartnerService.create(0, inputProjectPartner) }
        assertThat(projectPartnerService.create(1, inputProjectPartner)).isEqualTo(projectPartnerWithOrganization.toOutputProjectPartnerDetail())
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun updateProjectPartnerWithOrganization() {
        val projectPartnerUpdate =  InputProjectPartnerUpdate(1, "updated", ProjectPartnerRole.PARTNER, null, "test", "test", "test")
        val updatedProjectPartner = ProjectPartner(
            id = 1,
            project = project,
            abbreviation = projectPartnerUpdate.abbreviation!!,
            role = projectPartnerUpdate.role!!,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            department = projectPartnerWithOrganization.department
        )
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner

        assertThat(projectPartnerService.update(1, projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun deleteProjectPartnerWithOrganization() {
        val projectPartnerWithOrganization = ProjectPartner(
            id = 1,
            project = project,
            abbreviation = "partner",
            role = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            department = projectPartnerWithOrganization.department
        )
        every { projectPartnerRepository.deleteById(projectPartnerWithOrganization.id!!) } returns Unit
        every { projectPartnerRepository.findAllByProjectId(project.id!!, any<Sort>()) } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { projectPartnerService.deletePartner(project.id!!, projectPartnerWithOrganization.id!!) }
    }

    @Test
    fun deleteProjectPartnerWithoutOrganization() {
        every { projectPartnerRepository.deleteById(projectPartner.id!!) } returns Unit
        every { projectPartnerRepository.findAllByProjectId(project.id!!, any<Sort>()) } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { projectPartnerService.deletePartner(project.id!!, projectPartner.id!!) }
    }

    @Test
    fun deleteProjectPartner_notExisting() {
        every { projectPartnerRepository.deleteById(100) } returns Unit
        every { projectPartnerRepository.findAllByProjectId(project.id!!, any<Sort>()) } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { projectPartnerService.deletePartner(project.id!!, 100) }
    }

}
