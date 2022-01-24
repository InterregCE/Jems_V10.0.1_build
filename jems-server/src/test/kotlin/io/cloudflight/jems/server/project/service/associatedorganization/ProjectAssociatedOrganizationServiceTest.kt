package io.cloudflight.jems.server.project.service.associatedorganization

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.project.dto.ProjectContactDTO
import io.cloudflight.jems.api.project.dto.ProjectContactTypeDTO
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerContactDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.defaultAllowedRealCostsByCallType
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganization
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationAddress
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationContact
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationContactId
import io.cloudflight.jems.server.project.repository.partner.associated_organization.ProjectAssociatedOrganizationRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
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
import org.springframework.data.domain.Sort
import java.util.Optional

internal class ProjectAssociatedOrganizationServiceTest {

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var projectAssociatedOrganizationRepository: ProjectAssociatedOrganizationRepository

    lateinit var generalValidator: GeneralValidatorService

    lateinit var projectAssociatedOrganizationService: ProjectAssociatedOrganizationService

    private val userRole = UserRoleEntity(1, "ADMIN")
    private val user = UserEntity(
        id = 1,
        name = "Name",
        password = "hash",
        email = "admin@admin.dev",
        surname = "Surname",
        userRole = userRole,
        userStatus = UserStatus.ACTIVE
    )

    private val call = CallEntity(
        id = 1,
        creator = user,
        name = "call",
        status = CallStatus.DRAFT,
        type = CallType.STANDARD,
        startDate = ZonedDateTime.now(),
        endDateStep1 = null,
        endDate = ZonedDateTime.now(),
        prioritySpecificObjectives = mutableSetOf(),
        strategies = mutableSetOf(),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        lengthOfPeriod = 1,
        allowedRealCosts = defaultAllowedRealCostsByCallType(CallType.STANDARD),
        preSubmissionCheckPluginKey = null
    )
    private val projectStatus = ProjectStatusHistoryEntity(
        status = ApplicationStatus.APPROVED,
        user = user,
        updated = ZonedDateTime.now()
    )
    private val project = ProjectEntity(
        id = 1,
        acronym = "acronym",
        call = call,
        applicant = user,
        currentStatus = projectStatus,
    )

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        legalStatus = ProgrammeLegalStatusEntity(id = 1),
        sortNumber = 1,
    )

    private val projectPartnerDTO = ProjectPartnerSummaryDTO(
        id = 1,
        active = true,
        abbreviation = projectPartner.abbreviation,
        role = ProjectPartnerRoleDTO.LEAD_PARTNER,
        sortNumber = 1,
    )

    private fun organization(id: Long, partner: ProjectPartnerEntity, name: String, sortNr: Int? = null) =
        ProjectAssociatedOrganization(
            id = id,
            project = partner.project,
            partner = partner,
            nameInOriginalLanguage = name,
            nameInEnglish = name,
            sortNumber = sortNr
        )

    private fun outputOrganizationDetail(
        id: Long,
        partner: ProjectPartnerSummaryDTO,
        name: String,
        sortNr: Int? = null
    ) =
        OutputProjectAssociatedOrganizationDetail(
            id = id,
            active = true,
            partner = partner,
            nameInOriginalLanguage = name,
            nameInEnglish = name,
            sortNumber = sortNr
        )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        generalValidator = GeneralValidatorDefaultImpl()
        projectAssociatedOrganizationService =
            ProjectAssociatedOrganizationServiceImpl(
                projectPartnerRepository,
                projectAssociatedOrganizationRepository,
                generalValidator
            )
    }

    @Test
    fun `create associated organization`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)

        // mock 2 repo.save() method calls
        val toBePersistedContact = ProjectAssociatedOrganizationContact(
            contactId = ProjectAssociatedOrganizationContactId(10, ProjectContactTypeDTO.ContactPerson),
            contact = Contact(firstName = "test contact")
        )
        val toBePersistedAddress = ProjectAssociatedOrganizationAddress(
            organizationId = 10,
            address = AddressEntity(country = "AT")
        )
        val toFirstSave = organization(10, projectPartner, "to create")
        val toSecondSave = toFirstSave.copy(
            contacts = mutableSetOf(toBePersistedContact),
            addresses = mutableSetOf(toBePersistedAddress)
        )
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
        every {
            projectAssociatedOrganizationRepository.findAllByProjectId(
                eq(projectPartner.project.id),
                eq(Sort.by("id"))
            )
        } returns listOf(alreadyExistingOrganization, toSecondSave)
        every { projectAssociatedOrganizationRepository.saveAll(any<Iterable<ProjectAssociatedOrganization>>()) } returnsArgument 0

        // test create
        val toCreate = InputProjectAssociatedOrganization(
            id = null,
            partnerId = projectPartner.id,
            nameInOriginalLanguage = "to create",
            nameInEnglish = "to create",
            address = InputProjectAssociatedOrganizationAddress(country = "AT"),
            contacts = setOf(ProjectContactDTO(type = ProjectContactTypeDTO.ContactPerson, firstName = "test contact"))
        )
        val result = projectAssociatedOrganizationService.create(projectPartner.id, toCreate)
        assertThat(result).isEqualTo(
            outputOrganizationDetail(
                id = 10,
                partner = projectPartnerDTO,
                name = "to create",
                sortNr = null // this will be updated to 2 during updateSort, but for unit test we mock hibernate so object is not tightly connected to entity
            ).copy(
                address = OutputProjectAssociatedOrganizationAddress(country = "AT"),
                contacts = listOf(
                    ProjectPartnerContactDTO(
                        type = toBePersistedContact.contactId.type,
                        firstName = toBePersistedContact.contact!!.firstName
                    )
                )
            )
        )

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
        with(updatedSortNrs.captured) {
            assertThat(get(0).id).isEqualTo(9)
            assertThat(get(0).sortNumber).isEqualTo(1)
            assertThat(get(1).id).isEqualTo(10)
            assertThat(get(1).sortNumber).isEqualTo(2)
        }
    }

    @Test
    fun `create associated organization not-existing partner`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.empty()

        val toCreate = InputProjectAssociatedOrganization(id = null, partnerId = projectPartner.id)
        val ex = assertThrows<AppInputValidationException> { projectAssociatedOrganizationService.create(1, toCreate) }
        assertThat(ex.i18nMessage.i18nKey).isEqualTo("common.error.input.invalid")
    }

    @Test
    fun `update associated organization remove address and contacts`() {
        val oldOrganization = organization(
            id = 1,
            partner = projectPartner,
            name = "old name",
            sortNr = 13
        )
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(
            oldOrganization
        )
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(oldOrganization.partner)
        every { projectAssociatedOrganizationRepository.save(any<ProjectAssociatedOrganization>()) } returnsArgument 0

        val newValues = InputProjectAssociatedOrganization(
            id = oldOrganization.id,
            partnerId = oldOrganization.partner.id,
            nameInOriginalLanguage = "new name",
            nameInEnglish = "new name",
            address = null,
            contacts = emptySet()
        )

        // test update
        val result = projectAssociatedOrganizationService.update(1, newValues)
        assertThat(result).isEqualTo(
            outputOrganizationDetail(
                id = 1,
                partner = projectPartnerDTO,
                name = "new name",
                sortNr = 13 // this will be updated to 1, but for unit test we mock hibernate so object is not tightly connected to entity
            )
        )
    }

    @Test
    fun deleteAssociatedOrganization() {
        val orgToBeRemoved = organization(id = 1, partner = projectPartner, name = "test name")
        every {
            projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(
                orgToBeRemoved.project.id,
                orgToBeRemoved.id
            )
        } returns Optional.of(orgToBeRemoved)
        every { projectAssociatedOrganizationRepository.delete(eq(orgToBeRemoved)) } answers {}

        // mock updating sort after entity updated
        every {
            projectAssociatedOrganizationRepository.findAllByProjectId(
                eq(projectPartner.project.id),
                eq(Sort.by("id"))
            )
        } returns emptyList()
        every { projectAssociatedOrganizationRepository.saveAll(any<Iterable<ProjectAssociatedOrganization>>()) } returnsArgument 0

        projectAssociatedOrganizationService.delete(orgToBeRemoved.project.id, orgToBeRemoved.id)
    }

    @Test
    fun `deleteAssociatedOrganization not existing`() {
        every { projectAssociatedOrganizationRepository.findFirstByProjectIdAndId(1, -1) } returns Optional.empty()

        val ex = assertThrows<ResourceNotFoundException> { projectAssociatedOrganizationService.delete(1, -1) }
        assertThat(ex.entity).isEqualTo("projectAssociatedOrganisation")
    }

}
