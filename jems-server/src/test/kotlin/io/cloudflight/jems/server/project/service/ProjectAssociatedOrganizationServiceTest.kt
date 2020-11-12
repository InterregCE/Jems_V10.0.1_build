package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectContactType
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationUpdate
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.project.entity.Address
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganization
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationAddress
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationContact
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationContactId
import io.cloudflight.jems.server.project.repository.ProjectAssociatedOrganizationRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationServiceImpl
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.Optional

internal class ProjectAssociatedOrganizationServiceTest {

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var projectAssociatedOrganizationRepository: ProjectAssociatedOrganizationRepository

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

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        legalStatus = ProgrammeLegalStatus(1, "test")
    )

    private val outputProjectPartner = OutputProjectPartner(
        id = 1,
        abbreviation = projectPartner.abbreviation,
        role = ProjectPartnerRole.LEAD_PARTNER)

    private fun organization(id: Long, partner: ProjectPartnerEntity, name: String, sortNr: Int? = null) = ProjectAssociatedOrganization(
        id = id,
        project = partner.project,
        partner = partner,
        nameInOriginalLanguage = name,
        nameInEnglish = name,
        sortNumber = sortNr
    )

    private fun outputOrganization(id: Long, partnerAbbr: String, name: String, sortNr: Int? = null) = OutputProjectAssociatedOrganization(
        id = id,
        partnerAbbreviation = partnerAbbr,
        nameInOriginalLanguage = name,
        nameInEnglish = name,
        sortNumber = sortNr
    )

    private fun outputOrganizationDetail(id: Long, partner: OutputProjectPartner, name: String, sortNr: Int? = null) = OutputProjectAssociatedOrganizationDetail(
        id = id,
        partner = partner,
        nameInOriginalLanguage = name,
        nameInEnglish = name,
        sortNumber = sortNr
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectAssociatedOrganizationService = ProjectAssociatedOrganizationServiceImpl(projectPartnerRepository, projectAssociatedOrganizationRepository)
    }

    @Test
    fun getById() {
        val org = organization(1, projectPartner, "test", 1)
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(org)

        assertThat(projectAssociatedOrganizationService.getById(1, 1))
            .isEqualTo(outputOrganizationDetail(1, outputProjectPartner, "test", 1))
    }

    @Test
    fun `getById not-existing`() {
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(1, -1) } returns Optional.empty()

        val ex = assertThrows<ResourceNotFoundException> { projectAssociatedOrganizationService.getById(1,-1) }
        assertThat(ex.entity).isEqualTo("projectAssociatedOrganisation")
    }

    @Test
    fun findAllByProjectId() {
        every { projectAssociatedOrganizationRepository.findAllByProjectId(1, UNPAGED) } returns
            PageImpl(listOf(organization(1, projectPartner, "test", 1)))

        assertThat(projectAssociatedOrganizationService.findAllByProjectId(1, UNPAGED))
            .containsExactly(outputOrganization(1, projectPartner.abbreviation, "test", 1))
    }

    @Test
    fun `create associated organization`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)

        // mock 2 repo.save() method calls
        val toBePersistedContact = ProjectAssociatedOrganizationContact(
            contactId = ProjectAssociatedOrganizationContactId(10, ProjectContactType.ContactPerson),
            contact = Contact(firstName = "test contact")
        )
        val toBePersistedAddress = ProjectAssociatedOrganizationAddress(
            organizationId = 10,
            address = Address(country = "AT")
        )
        val toFirstSave = organization(10, projectPartner, "to create")
        val toSecondSave = toFirstSave.copy(contacts = mutableSetOf(toBePersistedContact), addresses = mutableSetOf(toBePersistedAddress))
        every { projectAssociatedOrganizationRepository.save(any<ProjectAssociatedOrganization>()) } returnsMany
            listOf(
                // first save just persist AssociatedOrganization without contacts and address
                toFirstSave,
                // second one will add contacts and address once we have organizationId
                toSecondSave
            )

        // mock updating sort
        val alreadyExistingOrganization = organization(
            id = 9,
            name = "old",
            partner = projectPartner,
            sortNr = 5 // old, to be replaced by 1
        )
        every { projectAssociatedOrganizationRepository.findAllByProjectId(eq(projectPartner.project.id), eq(Sort.by("id"))) } returns listOf(alreadyExistingOrganization, toSecondSave)
        every { projectAssociatedOrganizationRepository.saveAll(any<Iterable<ProjectAssociatedOrganization>>()) } returnsArgument 0

        // test create
        val toCreate = InputProjectAssociatedOrganizationCreate(
            partnerId = projectPartner.id,
            nameInOriginalLanguage = "to create",
            nameInEnglish = "to create",
            address = InputProjectAssociatedOrganizationAddress(country = "AT"),
            contacts = setOf(InputProjectContact(type = ProjectContactType.ContactPerson, firstName = "test contact"))
        )
        val result = projectAssociatedOrganizationService.create(projectPartner.id, toCreate)
        assertThat(result).isEqualTo(outputOrganizationDetail(
            id = 10,
            partner = outputProjectPartner,
            name = "to create",
            sortNr = null // this will be updated to 2 during updateSort, but for unit test we mock hibernate so object is not tightly connected to entity
        ).copy(
            address = OutputProjectAssociatedOrganizationAddress(country = "AT"),
            contacts = listOf(OutputProjectPartnerContact(type = toBePersistedContact.contactId.type, firstName = toBePersistedContact.contact!!.firstName))
        ))

        val slotOrganizations = mutableListOf<ProjectAssociatedOrganization>()
        verify { projectAssociatedOrganizationRepository.save(capture(slotOrganizations)) }

        // assert that address and contacts are persisted on second repo.save() call
        with(slotOrganizations[0]) {
            assertThat(id).isEqualTo(0)
            assertThat(nameInOriginalLanguage).isEqualTo("to create")
            assertThat(nameInEnglish).isEqualTo("to create")
            assertThat(addresses).isEmpty()
            assertThat(contacts).isEmpty()
        }
        with(slotOrganizations[1]) {
            assertThat(id).isEqualTo(10)
            assertThat(addresses).containsExactly(toBePersistedAddress)
            assertThat(contacts).containsExactly(toBePersistedContact)
        }

        val updatedSortNrs = slot<List<ProjectAssociatedOrganization>>()
        verify { projectAssociatedOrganizationRepository.saveAll(capture(updatedSortNrs)) }

        // assert that sortNumbers are correctly recalculated
        with (updatedSortNrs.captured) {
            assertThat(get(0).id).isEqualTo(9)
            assertThat(get(0).sortNumber).isEqualTo(1)
            assertThat(get(1).id).isEqualTo(10)
            assertThat(get(1).sortNumber).isEqualTo(2)
        }
    }

    @Test
    fun `create associated organization not-existing partner`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.empty()

        val toCreate = InputProjectAssociatedOrganizationCreate(partnerId = projectPartner.id)
        val ex = assertThrows<ResourceNotFoundException> { projectAssociatedOrganizationService.create(1, toCreate) }
        assertThat(ex.entity).isEqualTo("projectPartner")
    }

    @Test
    fun `update associated organization remove address and contacts`() {
        val oldOrganization = organization(
            id = 1,
            partner = projectPartner,
            name = "old name",
            sortNr = 13
        )
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(oldOrganization)
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(oldOrganization.partner)
        every { projectAssociatedOrganizationRepository.save(any<ProjectAssociatedOrganization>()) } returnsArgument 0

        val newValues = InputProjectAssociatedOrganizationUpdate(
            id = oldOrganization.id,
            partnerId = oldOrganization.partner.id,
            nameInOriginalLanguage = "new name",
            nameInEnglish = "new name",
            address = null,
            contacts = emptySet()
        )

        // test update
        val result = projectAssociatedOrganizationService.update(1, newValues)
        assertThat(result).isEqualTo(outputOrganizationDetail(
            id = 1,
            partner = outputProjectPartner,
            name = "new name",
            sortNr = 13 // this will be updated to 1, but for unit test we mock hibernate so object is not tightly connected to entity
        ))
    }

    @Test
    fun deleteAssociatedOrganization() {
        val orgToBeRemoved = organization(id = 1, partner = projectPartner, name = "test name")
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(orgToBeRemoved.project.id, orgToBeRemoved.id) } returns Optional.of(orgToBeRemoved)
        every { projectAssociatedOrganizationRepository.delete(eq(orgToBeRemoved)) } answers {}

        // mock updating sort after entity updated
        every { projectAssociatedOrganizationRepository.findAllByProjectId(eq(projectPartner.project.id), eq(Sort.by("id"))) } returns emptyList()
        every { projectAssociatedOrganizationRepository.saveAll(any<Iterable<ProjectAssociatedOrganization>>()) } returnsArgument 0

        projectAssociatedOrganizationService.delete(orgToBeRemoved.project.id, orgToBeRemoved.id)
    }

    @Test
    fun `deleteAssociatedOrganization not existing`() {
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(1, -1) } returns Optional.empty()

        val ex = assertThrows<ResourceNotFoundException> { projectAssociatedOrganizationService.delete(1,-1) }
        assertThat(ex.entity).isEqualTo("projectAssociatedOrganisation")
    }

}
