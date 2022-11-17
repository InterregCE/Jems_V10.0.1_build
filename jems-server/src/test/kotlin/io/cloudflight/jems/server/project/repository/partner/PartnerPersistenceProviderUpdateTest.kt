package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.programme.repository.stateaid.ProgrammeStateAidRepository
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContactEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContactId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.model.ProjectContactType
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.utils.partner.PARTNER_ID
import io.cloudflight.jems.server.utils.partner.PROJECT_ID
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil.Companion.project
import io.cloudflight.jems.server.utils.partner.legalStatusEntity
import io.cloudflight.jems.server.utils.partner.projectPartner
import io.cloudflight.jems.server.utils.partner.projectPartnerDetail
import io.cloudflight.jems.server.utils.partner.projectPartnerEntity
import io.cloudflight.jems.server.utils.partner.projectPartnerWithOrganizationEntity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class PartnerPersistenceProviderUpdateTest {

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var legalStatusRepo: ProgrammeLegalStatusRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectPartnerStateAidRepository: ProjectPartnerStateAidRepository

    @MockK
    lateinit var projectAssociatedOrganizationService: ProjectAssociatedOrganizationService

    @MockK
    lateinit var workPackageActivityRepository: WorkPackageActivityRepository

    @MockK
    lateinit var programmeStateAidRepository: ProgrammeStateAidRepository

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    @MockK
    lateinit var partner: ProjectPartnerEntity

    lateinit var persistence: PartnerPersistenceProvider

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = PartnerPersistenceProvider(
            projectVersionUtils,
            projectPartnerRepository,
            legalStatusRepo,
            projectRepository,
            projectPartnerStateAidRepository,
            projectAssociatedOrganizationService,
            workPackageActivityRepository,
            programmeStateAidRepository
        )
    }


    @Test
    fun updateProjectPartner() {
        val projectPartnerUpdate =
            projectPartner(PARTNER_ID, "updated", ProjectPartnerRole.LEAD_PARTNER)
        val updatedProjectPartnerEntity =
            projectPartnerEntity(abbreviation = projectPartnerUpdate.abbreviation!!, role = projectPartnerUpdate.role!!)
        val projectPartners = listOf(projectPartnerEntity(), projectPartnerWithOrganizationEntity())
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(projectPartnerEntity())
        every { projectPartnerRepository.save(any()) } returns updatedProjectPartnerEntity
        every { projectPartnerRepository.findTop30ByProjectId(PROJECT_ID, any()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0
        every { legalStatusRepo.getById(1) } returns legalStatusEntity

        assertThat(persistence.update(projectPartnerUpdate, true))
            .isEqualTo(
                projectPartnerDetail(
                    abbreviation = projectPartnerUpdate.abbreviation!!,
                    role = ProjectPartnerRole.LEAD_PARTNER
                )
            )
    }

    @Test
    fun `should not resort partners on update when resortByRole in false `() {
        val projectPartnerUpdate =
            projectPartner(PARTNER_ID, "updated", ProjectPartnerRole.PARTNER)
        val updatedProjectPartnerEntity =
            projectPartnerEntity(abbreviation = projectPartnerUpdate.abbreviation!!, role = projectPartnerUpdate.role!!)
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(projectPartnerEntity())
        every { projectPartnerRepository.save(any()) } returns updatedProjectPartnerEntity
        every { legalStatusRepo.getById(1) } returns legalStatusEntity

        persistence.update(projectPartnerUpdate, false)
        verify (atLeast = 0, atMost = 0) {projectPartnerRepository.findTop30ByProjectId(PROJECT_ID)}
    }

    @Test
    fun `updateProjectPartner to lead when no other leads`() {
        every { projectPartnerRepository.findById(3) } returns Optional.of(
            projectPartnerEntity(id = 3, role = ProjectPartnerRole.PARTNER)
        )
        every { legalStatusRepo.getById(1) } returns legalStatusEntity
        every { projectPartnerRepository.save(any()) } returns projectPartnerEntity(3, abbreviation = "updated")
        every {
            projectPartnerRepository.findTop30ByProjectId(PROJECT_ID, any())
        } returns listOf(projectPartnerEntity(id = 3), projectPartnerEntity(id = 2, role = ProjectPartnerRole.PARTNER))

        assertThat(persistence.update(projectPartner(3, "updated"), true).role)
            .isEqualTo(ProjectPartnerRole.LEAD_PARTNER)
    }

    @Test
    fun updatePartnerContact() {
        val projectPartnerContactUpdate = ProjectPartnerContact(
            ProjectContactType.ContactPerson,
            "test",
            "test",
            "test",
            "test@ems.eu",
            "test"
        )
        val projectPartner = ProjectPartnerEntity(
            1,
            active = true,
            project,
            "updated",
            ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool
        )

        val updatedProjectPartner = projectPartner.copy(newContacts = setOf(projectPartnerContactUpdate))

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(any()) } returns updatedProjectPartner

        assertThat(persistence.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)))
            .isEqualTo(updatedProjectPartner.toProjectPartnerDetail())
    }

    @Test
    fun `updatePartnerContacts leaving Partner data unchanged`() {
        val projectPartnerContactUpdate = ProjectPartnerContactEntity(
            ProjectPartnerContactId(1, ProjectContactType.ContactPerson),
            Contact(
                title = "test",
                firstName = "test",
                lastName = "test",
                email = "test@ems.eu",
                telephone = "test"
            )
        )
        val projectPartner = getProjectPartner(PartnerSubType.MICRO_ENTERPRISE)
        val updatedProjectPartner = getProjectPartner(PartnerSubType.MICRO_ENTERPRISE, setOf(projectPartnerContactUpdate))

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        val slotEntity = slot<ProjectPartnerEntity>()
        every { projectPartnerRepository.save(capture(slotEntity)) } returns updatedProjectPartner

        assertThat(persistence.updatePartnerContacts(1, setOf(projectPartnerContactUpdate.toProjectPartnerContact())))
            .isEqualTo(updatedProjectPartner.toProjectPartnerDetail())

        assertThat(slotEntity.captured.partnerSubType).isEqualTo(updatedProjectPartner.partnerSubType)
        assertThat(slotEntity.captured.contacts).isEqualTo(setOf(projectPartnerContactUpdate))
    }
    
    @Test
    fun `updatePartner changing partnerType to empty`() {
        val projectPartner = ProjectPartnerEntity(
            id = 2L,
            project = project,
            abbreviation = "updated",
            role = ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool,
            partnerSubType = PartnerSubType.MICRO_ENTERPRISE
        )
        val updatedProjectPartner = ProjectPartnerEntity(
            id = 2L,
            project = project,
            abbreviation = "updated",
            role = ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = null,
            partnerSubType = PartnerSubType.MICRO_ENTERPRISE
        )

        every { projectPartnerRepository.findById(2) } returns Optional.of(projectPartner)
        every { legalStatusRepo.getById(1) } returns legalStatusEntity
        val slotEntity = slot<ProjectPartnerEntity>()
        every { projectPartnerRepository.save(capture(slotEntity)) } returns updatedProjectPartner

        assertThat(persistence.update(
            projectPartner = ProjectPartner(
                id = projectPartner.id,
                abbreviation = projectPartner.abbreviation,
                role = ProjectPartnerRole.PARTNER,
                partnerType = null,
                partnerSubType = PartnerSubType.MICRO_ENTERPRISE,
                legalStatusId = 1
            ),
            resortByRole = false
        )).isEqualTo(updatedProjectPartner.toProjectPartnerDetail())

        assertThat(slotEntity.captured.id).isEqualTo(projectPartner.id)
        assertThat(slotEntity.captured.partnerType).isNull()
    }


    @Test
    fun `updatePartner changing partnerSubType to empty`() {
        val projectPartner = getProjectPartner(partnerSubType = PartnerSubType.MICRO_ENTERPRISE)
        val updatedProjectPartner = getProjectPartner(partnerSubType = null)

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { legalStatusRepo.getById(1) } returns legalStatusEntity
        val slotEntity = slot<ProjectPartnerEntity>()
        every { projectPartnerRepository.save(capture(slotEntity)) } returns updatedProjectPartner

        assertThat(persistence.update(
            projectPartner = ProjectPartner(
                id = projectPartner.id,
                abbreviation = projectPartner.abbreviation,
                role = ProjectPartnerRole.PARTNER,
                partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool,
                partnerSubType = null,
                legalStatusId = 1
            ),
            resortByRole = false
        )).isEqualTo(updatedProjectPartner.toProjectPartnerDetail())

        assertThat(slotEntity.captured.id).isEqualTo(projectPartner.id)
        assertThat(slotEntity.captured.partnerSubType).isNull()
    }

    @Test
    fun `updatePartner changing addresses`() {
        val projectId = 1L
        val projectPartner = getProjectPartner(partnerSubType = PartnerSubType.SMALL_ENTERPRISE)
        val partnerId = projectPartner.id
        val address = ProjectPartnerAddress(
            type = ProjectPartnerAddressType.Organization,
            country ="Österreich (AT)",
            countryCode = "AT",
            nutsRegion2 = "Wien (AT01)",
            nutsRegion2Code = "AT01",
            nutsRegion3 = "Wien (AT011)",
            nutsRegion3Code = "AT011",
            street = "street",
            houseNumber = "100",
            postalCode = "1010",
            city = "Vienna",
            homepage = "www"
        )

        every { projectPartnerRepository.findById(projectId) } returns Optional.of(projectPartner)
        every { legalStatusRepo.getById(1) } returns legalStatusEntity
        val slotEntity = slot<ProjectPartnerEntity>()
        every { projectPartnerRepository.save(capture(slotEntity)) } returns projectPartner.copy(newAddresses = setOf(address))

        assertThat(persistence.updatePartnerAddresses(partnerId, setOf(address)))
            .isEqualTo(ProjectPartnerDetail(
                projectId = projectId,
                id = partnerId,
                active = true,
                abbreviation = "updated",
                role = ProjectPartnerRole.PARTNER,
                createdAt = projectPartner.createdAt,
                sortNumber = 0,
                nameInOriginalLanguage = null,
                nameInEnglish = null,
                department = emptySet(),
                partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool,
                partnerSubType = PartnerSubType.SMALL_ENTERPRISE,
                nace = null,
                otherIdentifierNumber = null,
                otherIdentifierDescription = emptySet(),
                pic = null,
                legalStatusId= 1,
                vat = null,
                vatRecovery = null,
                addresses = listOf(ProjectPartnerAddress(
                    type = ProjectPartnerAddressType.Organization,
                    country="Österreich (AT)",
                    countryCode="AT",
                    nutsRegion2 = "Wien (AT01)",
                    nutsRegion2Code = "AT01",
                    nutsRegion3 = "Wien (AT011)",
                    nutsRegion3Code = "AT011",
                    street = "street",
                    houseNumber = "100",
                    postalCode = "1010",
                    city = "Vienna",
                    homepage = "www"
                )),
                contacts = emptyList(),
                motivation = null
            ))
        assertThat(slotEntity.captured.id).isEqualTo(projectPartner.id)
    }

    private fun getProjectPartner(
        partnerSubType: PartnerSubType? = null,
        contacts: Set<ProjectPartnerContactEntity>? = emptySet()
    ): ProjectPartnerEntity =
        ProjectPartnerEntity(
            id = 1,
            active = true,
            project = project,
            abbreviation = "updated",
            role = ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool,
            partnerSubType = partnerSubType,
            contacts = contacts
        )

    @Test
    fun updatePartnerContact_notExisting() {
        val projectPartnerContactUpdate = ProjectPartnerContact(
            ProjectContactType.LegalRepresentative,
            "test",
            "test",
            "test",
            "test@ems.eu",
            "test"
        )
        val contactPersonsDto = setOf(projectPartnerContactUpdate)
        every { projectPartnerRepository.findById(eq(-1)) } returns Optional.empty()
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatusEntity)
        val exception = assertThrows<ResourceNotFoundException> {
            persistence.updatePartnerContacts(
                -1,
                contactPersonsDto
            )
        }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun updatePartnerMotivation() {
        val projectPartnerMotivationUpdate = ProjectPartnerMotivation(
            setOf(InputTranslation(EN, "test")),
            setOf(InputTranslation(EN, "test")),
            setOf(InputTranslation(EN, "test"))
        )
        val projectPartner = ProjectPartnerEntity(
            1,
            active = true,
            project,
            "updated",
            ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool
        )
        val updatedProjectPartner =
            projectPartner.copy(newMotivation = projectPartnerMotivationUpdate)

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(any()) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatusEntity)

        assertThat(persistence.updatePartnerMotivation(1, projectPartnerMotivationUpdate))
            .isEqualTo(updatedProjectPartner.toProjectPartnerDetail())
    }

    @Test
    fun updatePartnerContribution_notExisting() {
        val projectPartnerContributionUpdate = ProjectPartnerMotivation(
            setOf(InputTranslation(EN, "test")),
            setOf(InputTranslation(EN, "test")),
            setOf(InputTranslation(EN, "test"))
        )
        every { projectPartnerRepository.findById(eq(-1)) } returns Optional.empty()
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatusEntity)
        val exception = assertThrows<ResourceNotFoundException> {
            persistence.updatePartnerMotivation(
                -1,
                projectPartnerContributionUpdate
            )
        }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun updateProjectPartnerWithOrganization() {
        val projectPartnerUpdate = projectPartner(
            abbreviation = "updated",
            role = ProjectPartnerRole.PARTNER,
            department = setOf(InputTranslation(EN, "test"))
        )
        val projectPartners = listOf(projectPartnerEntity(id = 2), projectPartnerWithOrganizationEntity())

        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(projectPartnerEntity())
        every { legalStatusRepo.getById(legalStatusEntity.id) } returns legalStatusEntity
        every { projectRepository.getById(PROJECT_ID) } returns project
        every { projectPartnerRepository.findTop30ByProjectId(PROJECT_ID, any()) } returns projectPartners
        every { projectPartnerRepository.save(any()) } returnsArgument 0
        assertThat(persistence.update(projectPartnerUpdate, true))
            .isEqualTo(
                projectPartnerDetail(
                    abbreviation = projectPartnerUpdate.abbreviation!!,
                    role = ProjectPartnerRole.PARTNER,
                    department = setOf(InputTranslation(EN, "test"))
                )
            )
    }

}
