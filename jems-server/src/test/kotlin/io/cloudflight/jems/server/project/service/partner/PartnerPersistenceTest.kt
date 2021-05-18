package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectContactType
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.PartnerPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.toEntity
import io.cloudflight.jems.server.project.repository.partner.toOutputProjectPartner
import io.cloudflight.jems.server.project.repository.partner.toOutputProjectPartnerDetail
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.project
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
import org.springframework.http.HttpStatus
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

    private lateinit var projectVersionUtils: ProjectVersionUtils

    lateinit var persistence: PartnerPersistence

    private val UNPAGED = Pageable.unpaged()

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = ProgrammeLegalStatusEntity(id = 1,),
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes
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
        role = ProjectPartnerRole.LEAD_PARTNER,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        translatedValues = partnerTranslatedValues,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = laegalStatus,
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes
    )
    private val legalStatus = ProgrammeLegalStatusEntity(id = 1)

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
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = PartnerPersistenceProvider(
            projectVersionUtils,
            projectPartnerRepository,
            projectPersistence,
            legalStatusRepo,
            projectRepository,
            projectAssociatedOrganizationService,
        )
        //for all delete tests
        every { projectAssociatedOrganizationService.refreshSortNumbers(any()) } answers {}
    }

    @Test
    fun getById() {
        every { projectPartnerRepository.findById(-1) } returns Optional.empty()
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPersistence.getProjectIdForPartner(-1) } throws ResourceNotFoundException("partner")
        every { projectPersistence.getProjectIdForPartner(1) } returns projectPartner.id

        assertThrows<ResourceNotFoundException> { persistence.getById(-1, null) }
        assertThat(persistence.getById(1, null)).isEqualTo(outputProjectPartnerDetail)
    }

    @Test
    fun findAllByProjectId() {
        every { projectPartnerRepository.findAllByProjectId(0, UNPAGED) } returns PageImpl(emptyList())
        every { projectPartnerRepository.findAllByProjectId(1, UNPAGED) } returns PageImpl(listOf(projectPartner))

        assertThat(persistence.findAllByProjectId(0, UNPAGED, null)).isEmpty()
        assertThat(persistence.findAllByProjectId(1, UNPAGED, null)).containsExactly(outputProjectPartner)
    }

    @Test
    fun findAllByProjectIdUnpaged() {
        every { projectPartnerRepository.findAllByProjectId(0) } returns PageImpl(emptyList())
        every { projectPartnerRepository.findAllByProjectId(1) } returns PageImpl(listOf(projectPartner))

        assertThat(persistence.findAllByProjectId(0)).isEmpty()
        assertThat(persistence.findAllByProjectId(1)).containsExactly(outputProjectPartnerDetail)
    }

    @Test
    fun createProjectPartner() {
        val inputProjectPartner =
            InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(
            0,
            project,
            inputProjectPartner.abbreviation!!,
            inputProjectPartner.role!!,
            legalStatus = legalStatus
        )
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 0
        every {
            projectPartnerRepository.findFirstByProjectIdAndRole(
                1,
                ProjectPartnerRole.LEAD_PARTNER
            )
        } returns Optional.empty()
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner
        every { projectPartnerRepository.save(projectPartner) } returns projectPartnerInclTransl
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findTop30ByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        assertThat(persistence.create(1, inputProjectPartner)).isEqualTo(outputProjectPartnerDetail)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun `createProjectPartner not existing`() {
        val inputProjectPartner = InputProjectPartnerCreate(
            "partner", ProjectPartnerRole.LEAD_PARTNER, null, "test", "test", setOf(
                InputTranslation(
                    SystemLanguage.EN, "test"
                )
            ), ProjectTargetGroup.BusinessSupportOrganisation,
            1, "test vat", ProjectPartnerVatRecovery.Yes
        )
        every { projectRepository.findById(-1) } returns Optional.empty()
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        val ex = assertThrows<ResourceNotFoundException> { persistence.create(-1, inputProjectPartner) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `error createProjectPartner when MAX count exceeded`() {
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 30
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        val ex = assertThrows<I18nValidationException> {
            persistence.create(
                1, InputProjectPartnerCreate(
                    "partner", ProjectPartnerRole.PARTNER, null, "test", "test", setOf(
                        InputTranslation(
                            SystemLanguage.EN, "test"
                        )
                    ), ProjectTargetGroup.BusinessSupportOrganisation,
                    1, "test vat", ProjectPartnerVatRecovery.Yes
                )
            )
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.max.allowed.count.reached")
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `error on multiple LEAD_PARTNER partner creation attempt`() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.PARTNER, legalStatusId = 1)
        val inputProjectPartnerLead =
            InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(
            0,
            project,
            inputProjectPartner.abbreviation!!,
            inputProjectPartner.role!!,
            legalStatus = legalStatus
        )

        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 2
        every {
            projectPartnerRepository.findFirstByProjectIdAndRole(
                1,
                ProjectPartnerRole.LEAD_PARTNER
            )
        } returns Optional.of(projectPartnerWithProject)
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner
        every { projectPartnerRepository.save(projectPartner) } returns projectPartnerInclTransl
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findTop30ByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        // new with Partner role creation will work
        assertThat(persistence.create(1, inputProjectPartner)).isEqualTo(outputProjectPartnerDetail)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
        // but new Lead should fail
        assertThrows<I18nValidationException> { persistence.create(1, inputProjectPartnerLead) }
    }

    @Test
    fun `error on already existing partner name when creating`() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val inputProjectPartner2 = InputProjectPartnerCreate("partner", ProjectPartnerRole.PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(0, project, inputProjectPartner.abbreviation!!, inputProjectPartner.role!!, legalStatus = legalStatus)

        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.of(projectPartnerWithProject)
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 0

        val ex = assertThrows<I18nValidationException> {
            persistence.create(1, inputProjectPartner2)
        }

        assertThat(ex.i18nKey).isEqualTo("project.partner.abbreviation.already.existing")
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun updateProjectPartner() {
        val projectPartnerUpdate =
            InputProjectPartnerUpdate(1, "updated", ProjectPartnerRole.PARTNER, legalStatusId = 1)
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
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun `updateProjectPartner to lead when no other leads`() {
        val projectPartnerUpdate =
            InputProjectPartnerUpdate(3, "updated", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
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
                role = ProjectPartnerRole.PARTNER
            )
        )
        every {
            projectPartnerRepository.findFirstByProjectIdAndRole(
                1,
                ProjectPartnerRole.LEAD_PARTNER
            )
        } returns Optional.empty()
        // to update role of Partner (id=3):
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        // to update sort-numbers for both Partners:
        val projectPartners =
            listOf(partner(3, ProjectPartnerRole.LEAD_PARTNER), partner(2, ProjectPartnerRole.PARTNER))
        every { projectPartnerRepository.findTop30ByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        persistence.update(projectPartnerUpdate)

        val updatedPartners = slot<Iterable<ProjectPartnerEntity>>()
        verify { projectPartnerRepository.saveAll(capture(updatedPartners)) }
        assertThat(updatedPartners.captured)
            .isEqualTo(
                listOf(
                    partner(3, ProjectPartnerRole.LEAD_PARTNER).copy(sortNumber = 1),
                    partner(2, ProjectPartnerRole.PARTNER).copy(sortNumber = 2)
                )
            )
    }

    @Test
    fun updatePartnerContact() {
        val projectPartnerContactUpdate = InputProjectContact(
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
            ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool
        )
        val contactPersonsEntity = setOf(projectPartnerContactUpdate.toEntity(projectPartner))
        val updatedProjectPartner = projectPartner.copy(contacts = contactPersonsEntity)

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(persistence.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun updatePartnerContact_notExisting() {
        val projectPartnerContactUpdate = InputProjectContact(
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
            ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool
        )
        val updatedProjectPartner =
            projectPartner.copy(motivation = projectPartnerMotivationUpdate.toEntity(projectPartner.id))

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(persistence.updatePartnerMotivation(1, projectPartnerMotivationUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
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
    fun `error on already existing partner name when updating`() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val updateProjectPartner = InputProjectPartnerUpdate(1, "partner", ProjectPartnerRole.PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(0, project, inputProjectPartner.abbreviation!!, inputProjectPartner.role!!, legalStatus = legalStatus)
        val oldProjectPartnerWithProject = ProjectPartnerEntity(0, project, "old partner", inputProjectPartner.role!!, legalStatus = legalStatus)

        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.findById(1) } returns Optional.of(oldProjectPartnerWithProject)
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.of(projectPartnerWithProject)
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 1

        val ex = assertThrows<I18nValidationException> {
            persistence.update(updateProjectPartner)
        }

        assertThat(ex.i18nKey).isEqualTo("project.partner.abbreviation.already.existing")
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun createProjectPartnerWithOrganization() {
        val inputProjectPartner = InputProjectPartnerCreate(
            "partner", ProjectPartnerRole.LEAD_PARTNER, null, "test", "test", setOf(
                InputTranslation(
                    SystemLanguage.EN, "test"
                )
            ), legalStatusId = 1
        )
        val projectPartnerWithProject = inputProjectPartner.toEntity(project, legalStatus)

        every { projectRepository.findById(0) } returns Optional.empty()
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(any()) } returns 0
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.empty()
        every {
            projectPartnerRepository.findFirstByProjectIdAndRole(
                1,
                ProjectPartnerRole.LEAD_PARTNER
            )
        } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartnerWithOrganization
        every { projectPartnerRepository.save(projectPartnerWithOrganization) } returns projectPartnerWithOrganization
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findTop30ByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThrows<ResourceNotFoundException> { persistence.create(0, inputProjectPartner) }
        assertThat(
            persistence.create(
                1,
                inputProjectPartner
            )
        ).isEqualTo(projectPartnerWithOrganization.toOutputProjectPartnerDetail())
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun updateProjectPartnerWithOrganization() {
        val projectPartnerUpdate = InputProjectPartnerUpdate(
            1, "updated", ProjectPartnerRole.PARTNER, null, "test", "test", setOf(
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
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun deleteProjectPartnerWithOrganization() {
        val projectPartnerWithOrganization = ProjectPartnerEntity(
            id = 1,
            project = project,
            abbreviation = "partner",
            role = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            translatedValues = projectPartnerWithOrganization.translatedValues,
            legalStatus = ProgrammeLegalStatusEntity(id = 1)
        )
        every { projectPartnerRepository.findById(projectPartnerWithOrganization.id) } returns Optional.of(
            projectPartnerWithOrganization
        )
        every { projectPartnerRepository.deleteById(projectPartnerWithOrganization.id) } returns Unit
        every { projectPartnerRepository.findTop30ByProjectId(project.id, any<Sort>()) } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptyList()

        assertDoesNotThrow { persistence.deletePartner(projectPartnerWithOrganization.id) }
        verify { projectAssociatedOrganizationService.refreshSortNumbers(project.id) }
    }

    @Test
    fun deleteProjectPartnerWithoutOrganization() {
        every { projectPartnerRepository.findById(projectPartner.id) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.deleteById(projectPartner.id) } returns Unit
        every { projectPartnerRepository.findTop30ByProjectId(project.id, any<Sort>()) } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptyList()

        assertDoesNotThrow { persistence.deletePartner(projectPartner.id) }
        verify { projectAssociatedOrganizationService.refreshSortNumbers(project.id) }
    }

    @Test
    fun deleteProjectPartner_notExisting() {
        every { projectPartnerRepository.findById(-1) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { persistence.deletePartner(-1) }
    }

}
