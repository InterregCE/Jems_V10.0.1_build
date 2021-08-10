package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.ProjectContactDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectContactType
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.CreateProjectPartnerRequestDTO
import io.cloudflight.jems.api.project.dto.partner.UpdateProjectPartnerRequestDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.PartnerIdentityRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.repository.ApplicationVersionNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.PartnerPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerStateAidRepository
import io.cloudflight.jems.server.project.repository.partner.toEntity
import io.cloudflight.jems.server.project.repository.partner.toDto
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerDetailDTO
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.project
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Optional

internal class PartnerPersistenceTest {

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var legalStatusRepo: ProgrammeLegalStatusRepository

    @MockK
    lateinit var projectAssociatedOrganizationService: ProjectAssociatedOrganizationService

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    @MockK
    lateinit var projectPartnerStateAidRepository: ProjectPartnerStateAidRepository

    private lateinit var projectVersionUtils: ProjectVersionUtils

    lateinit var persistence: PartnerPersistence

    private val UNPAGED = Pageable.unpaged()

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = project,
        abbreviation = "partner",
        role = ProjectPartnerRoleDTO.LEAD_PARTNER,
        nameInOriginalLanguage = "nameInOriginalLanguage",
        nameInEnglish = "nameInEnglish",
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = ProgrammeLegalStatusEntity(id = 1),
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecoveryDTO.Yes
    )
    private val partnerTranslatedValues =
        mutableSetOf(ProjectPartnerTranslEntity(TranslationPartnerId(1, SystemLanguage.EN), "test"))
    private val laegalStatus = ProgrammeLegalStatusEntity(id = 1)
    private val projectPartnerInclTransl =
        projectPartner.copy(translatedValues = partnerTranslatedValues)
    private val projectPartnerWithOrganization = ProjectPartnerEntity(
        id = 1,
        project = project,
        abbreviation = "partner",
        role = ProjectPartnerRoleDTO.LEAD_PARTNER,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        translatedValues = partnerTranslatedValues,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = laegalStatus,
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecoveryDTO.Yes
    )
    private val legalStatus = ProgrammeLegalStatusEntity(id = 1)

    private fun partner(id: Long, role: ProjectPartnerRoleDTO) = projectPartnerWithOrganization
        .copy(
            id = id,
            role = role
        )

    private val projectPartnerDTO = projectPartner.toDto()
    private val projectPartnerDetailDTO = ProjectPartnerDetailDTO(
        id = projectPartner.id,
        abbreviation = projectPartner.abbreviation,
        role = projectPartner.role,
        sortNumber = projectPartner.sortNumber,
        nameInOriginalLanguage = projectPartner.nameInOriginalLanguage,
        nameInEnglish = projectPartner.nameInEnglish,
        department = emptySet(),
        partnerType = projectPartner.partnerType,
        legalStatusId = projectPartner.legalStatus.id,
        vat = projectPartner.vat,
        vatRecovery = projectPartner.vatRecovery,
        addresses = emptyList(),
        contacts = emptyList(),
        motivation = null
    )

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
        )
        //for all delete tests
        every { projectAssociatedOrganizationService.refreshSortNumbers(any()) } answers {}
    }

    @Test
    fun getById() {
        every { projectPartnerRepository.findById(-1) } returns Optional.empty()
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.getProjectIdForPartner(-1) } throws ResourceNotFoundException("partner")
        every { projectPartnerRepository.getProjectIdForPartner(1) } returns projectPartner.id

        assertThrows<ResourceNotFoundException> { persistence.getById(-1, null) }
        assertThat(persistence.getById(1, null)).isEqualTo(projectPartnerDetailDTO)
    }

    @Test
    fun getByIdAndVersion() {
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val version = "1.0"
        val mockPartnerIdentityRow: PartnerIdentityRow = mockk()
        every { mockPartnerIdentityRow.id } returns 1
        every { mockPartnerIdentityRow.projectId } returns 1
        every { mockPartnerIdentityRow.abbreviation } returns "partner"
        every { mockPartnerIdentityRow.role } returns ProjectPartnerRoleDTO.LEAD_PARTNER
        every { mockPartnerIdentityRow.sortNumber } returns 0
        every { mockPartnerIdentityRow.nameInOriginalLanguage } returns "nameInOriginalLanguage"
        every { mockPartnerIdentityRow.nameInEnglish } returns "nameInEnglish"
        every { mockPartnerIdentityRow.partnerType } returns ProjectTargetGroup.BusinessSupportOrganisation
        every { mockPartnerIdentityRow.vat } returns "test vat"
        every { mockPartnerIdentityRow.language } returns null
        every { mockPartnerIdentityRow.department } returns null
        every { mockPartnerIdentityRow.vatRecovery } returns ProjectPartnerVatRecoveryDTO.Yes
        every { mockPartnerIdentityRow.legalStatusId } returns 1

        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(1) } returns 2
        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(-1) } returns null
        every { projectVersionRepo.findTimestampByVersion(2, "404") } returns null
        every { projectVersionRepo.findTimestampByVersion(2, version) } returns timestamp
        every { projectPartnerRepository.findPartnerAddressesByIdAsOfTimestamp(1, timestamp) } returns emptyList()
        every { projectPartnerRepository.findPartnerContactsByIdAsOfTimestamp(1, timestamp) } returns emptyList()
        every { projectPartnerRepository.findPartnerMotivationByIdAsOfTimestamp(1, timestamp) } returns emptyList()
        every { projectPartnerRepository.findPartnerIdentityByIdAsOfTimestamp(1, timestamp) } returns listOf(mockPartnerIdentityRow)

        // partner does not exist in any version
        assertThrows<ResourceNotFoundException> { persistence.getById(-1, version) }
        // no timestamp can be found for the specified partner->project
        assertThrows<ApplicationVersionNotFoundException> { persistence.getById(1, "404") }
        // historic version of partner returned (version found)
        assertThat(persistence.getById(1, version)).isEqualTo(projectPartnerDetailDTO)
    }

    @Test
    fun findAllByProjectId() {
        every { projectPartnerRepository.findAllByProjectId(0, UNPAGED) } returns PageImpl(emptyList())
        every { projectPartnerRepository.findAllByProjectId(1, UNPAGED) } returns PageImpl(listOf(projectPartner))

        assertThat(persistence.findAllByProjectId(0, UNPAGED, null)).isEmpty()
        assertThat(persistence.findAllByProjectId(1, UNPAGED, null)).containsExactly(projectPartnerDTO)
    }

    @Test
    fun findAllByProjectIdUnpaged() {
        every { projectPartnerRepository.findAllByProjectId(0) } returns PageImpl(emptyList())
        every { projectPartnerRepository.findAllByProjectId(1) } returns PageImpl(listOf(projectPartner))

        assertThat(persistence.findAllByProjectId(0)).isEmpty()
        assertThat(persistence.findAllByProjectId(1)).containsExactly(projectPartnerDetailDTO)
    }

    @Test
    fun createProjectPartner() {
        val projectPartnerRequest =
            CreateProjectPartnerRequestDTO("partner", ProjectPartnerRoleDTO.LEAD_PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(
            0,
            project,
            projectPartnerRequest.abbreviation!!,
            projectPartnerRequest.role!!,
            legalStatus = legalStatus
        )
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 0
        every {
            projectPartnerRepository.findFirstByProjectIdAndRole(
                1,
                ProjectPartnerRoleDTO.LEAD_PARTNER
            )
        } returns Optional.empty()
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner
        every { projectPartnerRepository.save(projectPartner) } returns projectPartnerInclTransl
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findTop30ByProjectId(1, any()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        assertThat(persistence.create(1, projectPartnerRequest)).isEqualTo(projectPartnerDetailDTO)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun `createProjectPartner not existing`() {
        val projectPartnerRequest = CreateProjectPartnerRequestDTO(
            "partner", ProjectPartnerRoleDTO.LEAD_PARTNER, null, "test", "test", setOf(
                InputTranslation(
                    SystemLanguage.EN, "test"
                )
            ), ProjectTargetGroup.BusinessSupportOrganisation,
            1, "test vat", ProjectPartnerVatRecoveryDTO.Yes
        )
        every { projectRepository.findById(-1) } returns Optional.empty()
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        val ex = assertThrows<ResourceNotFoundException> { persistence.create(-1, projectPartnerRequest) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun updateProjectPartner() {
        val projectPartnerUpdate =
            UpdateProjectPartnerRequestDTO(1, "updated", ProjectPartnerRoleDTO.PARTNER, legalStatusId = 1)
        val updatedProjectPartner = ProjectPartnerEntity(
            1,
            project,
            projectPartnerUpdate.abbreviation!!,
            projectPartnerUpdate.role!!,
            legalStatus = legalStatus
        )
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "updated") } returns Optional.empty()
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(persistence.update(projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toProjectPartnerDetailDTO())
    }

    @Test
    fun `updateProjectPartner to lead when no other leads`() {
        val projectPartnerUpdate =
            UpdateProjectPartnerRequestDTO(3, "updated", ProjectPartnerRoleDTO.LEAD_PARTNER, legalStatusId = 1)
        val updatedProjectPartner = ProjectPartnerEntity(
            3,
            project,
            projectPartnerUpdate.abbreviation!!,
            projectPartnerUpdate.role!!,
            legalStatus = legalStatus
        )
        // we are changing partner to Lead Partner
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "updated") } returns Optional.empty()
        every { projectPartnerRepository.findById(3) } returns Optional.of(
            projectPartner.copy(
                id = 3,
                role = ProjectPartnerRoleDTO.PARTNER
            )
        )
        every {
            projectPartnerRepository.findFirstByProjectIdAndRole(
                1,
                ProjectPartnerRoleDTO.LEAD_PARTNER
            )
        } returns Optional.empty()
        // to update role of Partner (id=3):
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        // to update sort-numbers for both Partners:
        val projectPartners =
            listOf(partner(3, ProjectPartnerRoleDTO.LEAD_PARTNER), partner(2, ProjectPartnerRoleDTO.PARTNER))
        every { projectPartnerRepository.findTop30ByProjectId(1, any()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        persistence.update(projectPartnerUpdate)

        val updatedPartners = slot<Iterable<ProjectPartnerEntity>>()
        verify { projectPartnerRepository.saveAll(capture(updatedPartners)) }
        assertThat(updatedPartners.captured)
            .isEqualTo(
                listOf(
                    partner(3, ProjectPartnerRoleDTO.LEAD_PARTNER).copy(sortNumber = 1),
                    partner(2, ProjectPartnerRoleDTO.PARTNER).copy(sortNumber = 2)
                )
            )
    }

    @Test
    fun updatePartnerContact() {
        val projectPartnerContactUpdate = ProjectContactDTO(
            ProjectContactType.ContactPerson,
            "test",
            "test",
            "test",
            "test@ems.eu",
            "test"
        )
        val projectPartner = ProjectPartnerEntity(
            1,
            project,
            "updated",
            ProjectPartnerRoleDTO.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool
        )
        val contactPersonsEntity = setOf(projectPartnerContactUpdate.toEntity(projectPartner))
        val updatedProjectPartner = projectPartner.copy(contacts = contactPersonsEntity)

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(persistence.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)))
            .isEqualTo(updatedProjectPartner.toProjectPartnerDetailDTO())
    }

    @Test
    fun updatePartnerContact_notExisting() {
        val projectPartnerContactUpdate = ProjectContactDTO(
            ProjectContactType.LegalRepresentative,
            "test",
            "test",
            "test",
            "test@ems.eu",
            "test"
        )
        val contactPersonsDto = setOf(projectPartnerContactUpdate)
        every { projectPartnerRepository.findById(eq(-1)) } returns Optional.empty()
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
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
        val projectPartnerMotivationUpdate = ProjectPartnerMotivationDTO(
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test"))
        )
        val projectPartner = ProjectPartnerEntity(
            1,
            project,
            "updated",
            ProjectPartnerRoleDTO.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool
        )
        val updatedProjectPartner =
            projectPartner.copy(motivation = projectPartnerMotivationUpdate.toEntity(projectPartner.id))

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(persistence.updatePartnerMotivation(1, projectPartnerMotivationUpdate))
            .isEqualTo(updatedProjectPartner.toProjectPartnerDetailDTO())
    }

    @Test
    fun updatePartnerContribution_notExisting() {
        val projectPartnerContributionUpdate = ProjectPartnerMotivationDTO(
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test"))
        )
        every { projectPartnerRepository.findById(eq(-1)) } returns Optional.empty()
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        val exception = assertThrows<ResourceNotFoundException> {
            persistence.updatePartnerMotivation(
                -1,
                projectPartnerContributionUpdate
            )
        }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun createProjectPartnerWithOrganization() {
        val projectPartnerRequest = CreateProjectPartnerRequestDTO(
            "partner", ProjectPartnerRoleDTO.LEAD_PARTNER, null, "test", "test", setOf(
                InputTranslation(
                    SystemLanguage.EN, "test"
                )
            ), legalStatusId = 1
        )
        val projectPartnerWithProject = projectPartnerRequest.toEntity(project, legalStatus)

        every { projectRepository.findById(0) } returns Optional.empty()
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(any()) } returns 0
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.empty()
        every {
            projectPartnerRepository.findFirstByProjectIdAndRole(
                1,
                ProjectPartnerRoleDTO.LEAD_PARTNER
            )
        } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartnerWithOrganization
        every { projectPartnerRepository.save(projectPartnerWithOrganization) } returns projectPartnerWithOrganization
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findTop30ByProjectId(1, any()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThrows<ResourceNotFoundException> { persistence.create(0, projectPartnerRequest) }
        assertThat(
            persistence.create(
                1,
                projectPartnerRequest
            )
        ).isEqualTo(projectPartnerWithOrganization.toProjectPartnerDetailDTO())
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun updateProjectPartnerWithOrganization() {
        val projectPartnerUpdate = UpdateProjectPartnerRequestDTO(
            1, "updated", ProjectPartnerRoleDTO.PARTNER, null, "test", "test", setOf(
                InputTranslation(
                    SystemLanguage.EN, "test"
                )
            ), legalStatusId = 1
        )
        val updatedProjectPartner = ProjectPartnerEntity(
            id = 1,
            project = project,
            abbreviation = projectPartnerUpdate.abbreviation!!,
            role = projectPartnerUpdate.role!!,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            translatedValues = projectPartnerWithOrganization.translatedValues,
            legalStatus = legalStatus
        )
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "updated") } returns Optional.empty()
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(persistence.update(projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toProjectPartnerDetailDTO())
    }

    @Test
    fun deleteProjectPartnerWithOrganization() {
        val projectPartnerWithOrganization = ProjectPartnerEntity(
            id = 1,
            project = project,
            abbreviation = "partner",
            role = ProjectPartnerRoleDTO.LEAD_PARTNER,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            translatedValues = projectPartnerWithOrganization.translatedValues,
            legalStatus = ProgrammeLegalStatusEntity(id = 1)
        )
        every { projectPartnerRepository.findById(projectPartnerWithOrganization.id) } returns Optional.of(
            projectPartnerWithOrganization
        )
        every { projectPartnerRepository.deleteById(projectPartnerWithOrganization.id) } returns Unit
        every { projectPartnerRepository.findTop30ByProjectId(project.id, any()) } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptyList()

        assertDoesNotThrow { persistence.deletePartner(projectPartnerWithOrganization.id) }
        verify { projectAssociatedOrganizationService.refreshSortNumbers(project.id) }
    }

    @Test
    fun deleteProjectPartnerWithoutOrganization() {
        every { projectPartnerRepository.findById(projectPartner.id) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.deleteById(projectPartner.id) } returns Unit
        every { projectPartnerRepository.findTop30ByProjectId(project.id, any()) } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptyList()

        assertDoesNotThrow { persistence.deletePartner(projectPartner.id) }
        verify { projectAssociatedOrganizationService.refreshSortNumbers(project.id) }
    }

    @Test
    fun deleteProjectPartner_notExisting() {
        every { projectPartnerRepository.findById(-1) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { persistence.deletePartner(-1) }
    }

    @Test
    fun `get ProjectId for Partner`() {
        val entity: ProjectPartnerEntity = mockk()
        every { entity.project.id } returns 2
        every { projectPartnerRepository.findById(1) } returns Optional.of(entity)
        assertThat(persistence.getProjectIdForPartnerId(1)).isEqualTo(2)
    }

    @Test
    fun `get ProjectId for Partner - not existing`() {
        every { projectPartnerRepository.findById(1) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getProjectIdForPartnerId(1) }
        assertThat(ex.entity).isEqualTo("projectPartner")
    }

    @Test
    fun `get ProjectId for historic Partner`() {
        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(1) } returns 2
        assertThat(persistence.getProjectIdForPartnerId(1, "1.0")).isEqualTo(2)
    }

    @Test
    fun `get ProjectId for historic Partner - not existing`() {
        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(1) } returns null
        assertThrows<ResourceNotFoundException> { persistence.getProjectIdForPartnerId(1, "404") }
    }
}
