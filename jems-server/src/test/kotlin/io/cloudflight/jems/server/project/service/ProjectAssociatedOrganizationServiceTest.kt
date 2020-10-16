package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationAddressDetails
import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationUpdate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerContactType
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.AssociatedOrganizationContact
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.ProjectAssociatedOrganization
import io.cloudflight.jems.server.project.entity.ProjectAssociatedOrganizationDetail
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.repository.ProjectAssociatedOrganizationRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.partner.toOutputProjectPartner
import io.cloudflight.jems.server.security.model.LocalCurrentUser
import io.cloudflight.jems.server.security.service.SecurityService
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.cloudflight.jems.server.user.service.toOutputUserWithRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.HashSet
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

internal class ProjectAssociatedOrganizationServiceTest {
    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var projectAssociatedOrganizationRepository: ProjectAssociatedOrganizationRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var securityService: SecurityService

    lateinit var projectAssociatedOrganizationService: ProjectAssociatedOrganizationService

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
    private val organization = ProjectAssociatedOrganization(
        1,
        "test",
        "test",
        organizationAddress = null,
        partner = projectPartner,
        project = project)

    private val outputProjectPartner = projectPartner.toOutputProjectPartner()
    private val outputOrganization = organization.toOutputProjectAssociatedOrganization()
    private val outputOrganizationDetail = organization.toOutputProjectAssociatedOrganizationDetail()
    private val inputOrganizationAddress = InputProjectAssociatedOrganizationAddressDetails("country", "", "", "", "" ,"" ,"", "")
    private val inputProjectPartnerContactCreateOne = InputProjectPartnerContact("test", ProjectPartnerContactType.LegalRepresentative, "test", "test", "", "")
    private val inputProjectPartnerContactCreateTwo = InputProjectPartnerContact("test", ProjectPartnerContactType.ContactPerson, "test", "test", "test@ems.eu", "test")
    private val inputAssociatedOrganization = InputProjectAssociatedOrganizationCreate(null, "associatedOrganization", "associatedOrganizationInEnglish", null, projectPartner.id!!, setOf())


    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(outputUser, user.password, emptyList())
        projectAssociatedOrganizationService = ProjectAssociatedOrganizationServiceImpl(projectPartnerRepository, projectRepository, projectAssociatedOrganizationRepository)
    }

    @Test
    fun getById() {
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(1, 0) } returns Optional.empty()
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(organization)

        assertThrows<ResourceNotFoundException> { projectAssociatedOrganizationService.getById(1,0) }
        Assertions.assertThat(projectAssociatedOrganizationService.getById(1, 1)).isEqualTo(outputOrganizationDetail)
    }

    @Test
    fun findAllByProjectId() {
        every { projectAssociatedOrganizationRepository.findAllByProjectId(0, UNPAGED) } returns PageImpl(emptyList())
        every { projectAssociatedOrganizationRepository.findAllByProjectId(1, UNPAGED) } returns PageImpl(listOf(organization))

        Assertions.assertThat(projectAssociatedOrganizationService.findAllByProjectId(0, UNPAGED)).isEmpty()
        Assertions.assertThat(projectAssociatedOrganizationService.findAllByProjectId(1, UNPAGED)).containsExactly(outputOrganization)
    }

    @Test
    fun createProjectPartner() {
        val initialAssociatedOrganization = inputAssociatedOrganization.toEntity(project, projectPartner)
        val associatedOrganization = inputAssociatedOrganization.toEntity(project, projectPartner).copy(id = 1L)

        val organizationAddress = inputOrganizationAddress.toEntity(associatedOrganization)
        val contacts = HashSet<AssociatedOrganizationContact>();
        contacts.add(inputProjectPartnerContactCreateOne.toAssociatedOrganizationContact(associatedOrganization))
        contacts.add(inputProjectPartnerContactCreateTwo.toAssociatedOrganizationContact(associatedOrganization))
        val associatedOrganizationUpdated = associatedOrganization.copy(organizationAddress = organizationAddress, associatedOrganizationContacts = contacts)

        val outputOrganizationAddressDetails = organizationAddress.toOutputProjectAssociatedOrganizationDetails()
        val outputContacts = HashSet<OutputProjectPartnerContact>();
        outputContacts.add(inputProjectPartnerContactCreateOne.toAssociatedOrganizationContact(associatedOrganizationUpdated).toOutputProjectPartnerContact())
        outputContacts.add(inputProjectPartnerContactCreateTwo.toAssociatedOrganizationContact(associatedOrganizationUpdated).toOutputProjectPartnerContact())
        val outputAssociatedOrganizationUpdatedDetail = OutputProjectAssociatedOrganizationDetail(1, "associatedOrganization", "associatedOrganizationInEnglish", outputOrganizationAddressDetails, null,  outputProjectPartner, outputContacts)

        every { projectRepository.findById(0) } returns Optional.empty()
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectAssociatedOrganizationRepository.save(initialAssociatedOrganization) } returns initialAssociatedOrganization
        every { projectAssociatedOrganizationRepository.save(initialAssociatedOrganization.copy(
            organizationAddress = inputAssociatedOrganization.organizationAddress?.toEntity(initialAssociatedOrganization),
            associatedOrganizationContacts = inputAssociatedOrganization.associatedOrganizationContacts?.map { it.toAssociatedOrganizationContact(initialAssociatedOrganization) }?.toHashSet())
            ) } returns associatedOrganizationUpdated
        // also handle sorting
        val associatedOrganizations = listOf(organization, associatedOrganizationUpdated)
        every { projectAssociatedOrganizationRepository.findAllByProjectId(1, any<Sort>()) } returns associatedOrganizations
        every { projectAssociatedOrganizationRepository.saveAll(any<Iterable<ProjectAssociatedOrganization>>()) } returnsArgument 0

        assertThrows<ResourceNotFoundException> { projectAssociatedOrganizationService.create(0, inputAssociatedOrganization) }
        Assertions.assertThat(projectAssociatedOrganizationService.create(1, inputAssociatedOrganization)).isEqualTo(outputAssociatedOrganizationUpdatedDetail)
    }

    @Test
    fun updateProjectPartner() {

        val initialAssociatedOrganization = inputAssociatedOrganization.toEntity(project, projectPartner)
        val associatedOrganization = inputAssociatedOrganization.toEntity(project, projectPartner).copy(id = 1L)

        val organizationAddress = inputOrganizationAddress.toEntity(associatedOrganization)
        val contacts = HashSet<AssociatedOrganizationContact>();
        contacts.add(inputProjectPartnerContactCreateOne.toAssociatedOrganizationContact(associatedOrganization))
        contacts.add(inputProjectPartnerContactCreateTwo.toAssociatedOrganizationContact(associatedOrganization))
        val associatedOrganizationUpdated = associatedOrganization.copy(organizationAddress = organizationAddress, associatedOrganizationContacts = contacts)
        val inputAssociatedOrganizationUpdate = InputProjectAssociatedOrganizationUpdate(1, "updated", "updated", null, 1, setOf())
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(initialAssociatedOrganization)
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectAssociatedOrganizationRepository.save(initialAssociatedOrganization.copy(
            organizationAddress = inputAssociatedOrganization.organizationAddress?.toEntity(initialAssociatedOrganization),
            associatedOrganizationContacts = inputAssociatedOrganization.associatedOrganizationContacts?.map { it.toAssociatedOrganizationContact(initialAssociatedOrganization) }?.toHashSet())
        ) } returns associatedOrganizationUpdated

        // also handle sorting
        val associatedOrganizations = listOf(organization, associatedOrganizationUpdated)
        every { projectAssociatedOrganizationRepository.findAllByProjectId(1, any<Sort>()) } returns associatedOrganizations
        every { projectAssociatedOrganizationRepository.saveAll(any<Iterable<ProjectAssociatedOrganization>>()) } returnsArgument 0


        Assertions.assertThat(projectAssociatedOrganizationService.update(1, inputAssociatedOrganizationUpdate))
            .isEqualTo(associatedOrganizationUpdated.toOutputProjectAssociatedOrganizationDetail())
    }

    @Test
    fun deleteAssociatedOrganizationWithDetails() {
        val associatedOrganizationWithoutDetail = ProjectAssociatedOrganization(
            id = 1,
            nameInOriginalLanguage = "",
            nameInEnglish = "",
            partner = projectPartner,
            project = project,
            organizationAddress = null,
            sortNumber = null,
            associatedOrganizationContacts = emptySet() )
        val organization = ProjectAssociatedOrganizationDetail(
            1,
            associatedOrganizationWithoutDetail,
            "test",
            "test",
            "",
            "",
            "",
            "",
            "",
            "")
        val associatedOrganizationWithDetails = associatedOrganizationWithoutDetail.copy(organizationAddress = organization)
        every { projectAssociatedOrganizationRepository.deleteById(associatedOrganizationWithDetails.id!!) } returns Unit
        every { projectAssociatedOrganizationRepository.findAllByProjectId(project.id!!, any<Sort>()) } returns emptySet()
        every { projectAssociatedOrganizationRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { projectAssociatedOrganizationService.delete(project.id!!, associatedOrganizationWithDetails.id!!) }
    }

    @Test
    fun deleteAssociatedOrganization_notExisting() {
        every { projectAssociatedOrganizationRepository.deleteById(100) } returns Unit
        every { projectAssociatedOrganizationRepository.findAllByProjectId(project.id!!, any<Sort>()) } returns emptySet()
        every { projectAssociatedOrganizationRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { projectAssociatedOrganizationService.delete(project.id!!, 100) }
    }

}
