package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.ProjectContactType
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.repository.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.toEntity
import io.cloudflight.jems.server.project.repository.partner.toOutputProjectPartner
import io.cloudflight.jems.server.project.repository.partner.toOutputProjectPartnerDetail
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

internal class ProjectPartnerServiceTest {

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var legalStatusRepo: ProgrammeLegalStatusRepository

    @MockK
    lateinit var projectAssociatedOrganizationService: ProjectAssociatedOrganizationService

    lateinit var projectPartnerService: ProjectPartnerService

    private val UNPAGED = Pageable.unpaged()

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = ProgrammeLegalStatus(1,"description"),
        vat = "test vat",
        vatRecovery = true
        )
    private val projectPartnerWithOrganization = ProjectPartnerEntity(
        id = 1,
        project = project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        department = "test",
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = ProgrammeLegalStatus(1, "description"),
        vat = "test vat",
        vatRecovery = true
    )
    private val legalStatus = ProgrammeLegalStatus(
        id = 1,
        description = "description"
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
        projectPartnerService = ProjectPartnerServiceImpl(
            projectPartnerRepository,
            projectAssociatedOrganizationService,
            projectRepository,
            legalStatusRepo
        )
        //for all delete tests
        every { projectAssociatedOrganizationService.refreshSortNumbers(any()) } answers {}
    }

    @Test
    fun getById() {
        every { projectPartnerRepository.findById(-1) } returns Optional.empty()
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)

        assertThrows<ResourceNotFoundException> { projectPartnerService.getById(-1) }
        assertThat(projectPartnerService.getById(1)).isEqualTo(outputProjectPartnerDetail)
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
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(0, project, inputProjectPartner.abbreviation!!, inputProjectPartner.role!!, legalStatus = legalStatus)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 0
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findTop30ByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        assertThat(projectPartnerService.create(1, inputProjectPartner)).isEqualTo(outputProjectPartnerDetail)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun `createProjectPartner not existing`() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, null,"test","test", "test", ProjectTargetGroup.BusinessSupportOrganisation,
            1,"test vat", true)
        every { projectRepository.findById(-1) } returns Optional.empty()
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        val ex = assertThrows<ResourceNotFoundException> { projectPartnerService.create(-1, inputProjectPartner) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `error createProjectPartner when MAX count exceeded`() {
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 30
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        val ex = assertThrows<I18nValidationException> {
            projectPartnerService.create(1, InputProjectPartnerCreate("partner", ProjectPartnerRole.PARTNER, null,"test","test", "test", ProjectTargetGroup.BusinessSupportOrganisation,
                1,"test vat", true))
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.max.allowed.count.reached")
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `error on multiple LEAD_PARTNER partner creation attempt`() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.PARTNER, legalStatusId = 1)
        val inputProjectPartnerLead = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(0, project, inputProjectPartner.abbreviation!!, inputProjectPartner.role!!, legalStatus = legalStatus)

        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 2
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.of(projectPartnerWithProject)
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findTop30ByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        // new with Partner role creation will work
        assertThat(projectPartnerService.create(1, inputProjectPartner)).isEqualTo(outputProjectPartnerDetail)
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
        // but new Lead should fail
        assertThrows<I18nValidationException> { projectPartnerService.create(1, inputProjectPartnerLead) }
    }

    @Test
    fun updateProjectPartner() {
        val projectPartnerUpdate = InputProjectPartnerUpdate(1, "updated", ProjectPartnerRole.PARTNER, legalStatusId = 1)
        val updatedProjectPartner = ProjectPartnerEntity(1, project, projectPartnerUpdate.abbreviation!!, projectPartnerUpdate.role!!, legalStatus = legalStatus)
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(projectPartnerService.update(projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun `updateProjectPartner to lead when no other leads`() {
        val projectPartnerUpdate = InputProjectPartnerUpdate(3, "updated", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val updatedProjectPartner = ProjectPartnerEntity(3, project, projectPartnerUpdate.abbreviation!!, projectPartnerUpdate.role!!, legalStatus = legalStatus)
        // we are changing partner to Lead Partner
        every { projectPartnerRepository.findById(3) } returns Optional.of(projectPartner.copy(id = 3, role = ProjectPartnerRole.PARTNER))
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        // to update role of Partner (id=3):
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        // to update sort-numbers for both Partners:
        val projectPartners = listOf(partner(3, ProjectPartnerRole.LEAD_PARTNER), partner(2, ProjectPartnerRole.PARTNER))
        every { projectPartnerRepository.findTop30ByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        projectPartnerService.update(projectPartnerUpdate)

        val updatedPartners = slot<Iterable<ProjectPartnerEntity>>()
        verify { projectPartnerRepository.saveAll(capture(updatedPartners)) }
        assertThat(updatedPartners.captured)
            .isEqualTo(listOf(
                partner(3, ProjectPartnerRole.LEAD_PARTNER).copy(sortNumber = 1),
                partner(2, ProjectPartnerRole.PARTNER).copy(sortNumber = 2)
            ))
    }

    @Test
    fun updatePartnerContact() {
        val projectPartnerContactUpdate = InputProjectContact(
            ProjectContactType.ContactPerson,
            "test",
            "test",
            "test",
            "test@ems.eu",
            "test")
        val projectPartner = ProjectPartnerEntity(1, project, "updated", ProjectPartnerRole.PARTNER, legalStatus = ProgrammeLegalStatus(1, "test description"), partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool)
        val contactPersonsEntity = setOf(projectPartnerContactUpdate.toEntity(projectPartner))
        val updatedProjectPartner = ProjectPartnerEntity(id = 1, project =  project, abbreviation = "updated", role = ProjectPartnerRole.PARTNER,
            contacts = contactPersonsEntity, legalStatus = ProgrammeLegalStatus(1, "test description"), partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool)

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(projectPartnerService.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)))
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
            "test")
        val contactPersonsDto = setOf(projectPartnerContactUpdate)
        every { projectPartnerRepository.findById(eq(-1)) } returns Optional.empty()
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        val exception = assertThrows<ResourceNotFoundException> { projectPartnerService.updatePartnerContacts(-1, contactPersonsDto) }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun updatePartnerMotivation() {
        val projectPartnerMotivationUpdate = ProjectPartnerMotivationDTO(
            "test",
            "test",
            "test")
        val projectPartner = ProjectPartnerEntity(1, project, "updated", ProjectPartnerRole.PARTNER, legalStatus = ProgrammeLegalStatus(1, "test description"), partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool)
        val updatedProjectPartner = ProjectPartnerEntity(id = 1, project = project, abbreviation = "updated", role = ProjectPartnerRole.PARTNER,
            motivation = projectPartnerMotivationUpdate.toEntity(projectPartner.id), legalStatus = ProgrammeLegalStatus(1, "test description"), partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool)

        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(projectPartnerService.updatePartnerMotivation(1, projectPartnerMotivationUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun updatePartnerContribution_notExisting() {
        val projectPartnerContributionUpdate = ProjectPartnerMotivationDTO(
            "test",
            "test",
            "test")
        every { projectPartnerRepository.findById(eq(-1)) } returns Optional.empty()
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)
        val exception = assertThrows<ResourceNotFoundException> { projectPartnerService.updatePartnerMotivation(-1, projectPartnerContributionUpdate) }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun createProjectPartnerWithOrganization() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, null, "test", "test", "test", legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(
            project = project,
            abbreviation = inputProjectPartner.abbreviation!!,
            role =  inputProjectPartner.role!!,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            department = projectPartnerWithOrganization.department,
            legalStatus = ProgrammeLegalStatus(1,"description")
        )
        every { projectRepository.findById(0) } returns Optional.empty()
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectPartnerRepository.countByProjectId(any()) } returns 0
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        every { projectPartnerRepository.save(projectPartnerWithProject) } returns projectPartnerWithOrganization
        // also handle sorting
        val projectPartners = listOf(projectPartner, projectPartnerWithProject)
        every { projectPartnerRepository.findTop30ByProjectId(1, any<Sort>()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThrows<ResourceNotFoundException> { projectPartnerService.create(0, inputProjectPartner) }
        assertThat(projectPartnerService.create(1, inputProjectPartner)).isEqualTo(projectPartnerWithOrganization.toOutputProjectPartnerDetail())
        verify { projectPartnerRepository.save(projectPartnerWithProject) }
    }

    @Test
    fun updateProjectPartnerWithOrganization() {
        val projectPartnerUpdate =  InputProjectPartnerUpdate(1, "updated", ProjectPartnerRole.PARTNER, null, "test", "test", "test", legalStatusId = 1)
        val updatedProjectPartner = ProjectPartnerEntity(
            id = 1,
            project = project,
            abbreviation = projectPartnerUpdate.abbreviation!!,
            role = projectPartnerUpdate.role!!,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            department = projectPartnerWithOrganization.department,
            legalStatus = ProgrammeLegalStatus(1,"description")
        )
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(updatedProjectPartner) } returns updatedProjectPartner
        every { legalStatusRepo.findById(1) } returns Optional.of(legalStatus)

        assertThat(projectPartnerService.update(projectPartnerUpdate))
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
            department = projectPartnerWithOrganization.department,
            legalStatus = ProgrammeLegalStatus(1, "test description")
        )
        every { projectPartnerRepository.findById(projectPartnerWithOrganization.id) } returns Optional.of(projectPartnerWithOrganization)
        every { projectPartnerRepository.deleteById(projectPartnerWithOrganization.id) } returns Unit
        every { projectPartnerRepository.findTop30ByProjectId(project.id, any<Sort>()) } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { projectPartnerService.deletePartner(projectPartnerWithOrganization.id) }
        verify { projectAssociatedOrganizationService.refreshSortNumbers(project.id) }
    }

    @Test
    fun deleteProjectPartnerWithoutOrganization() {
        every { projectPartnerRepository.findById(projectPartner.id) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.deleteById(projectPartner.id) } returns Unit
        every { projectPartnerRepository.findTop30ByProjectId(project.id, any<Sort>()) } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { projectPartnerService.deletePartner(projectPartner.id) }
        verify { projectAssociatedOrganizationService.refreshSortNumbers(project.id) }
    }

    @Test
    fun deleteProjectPartner_notExisting() {
        every { projectPartnerRepository.findById(-1) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { projectPartnerService.deletePartner(-1) }
    }

}
