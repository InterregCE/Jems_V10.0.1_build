package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.HealthyAgeing
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.SocialInfrastructure
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationEntity
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.repository.ApplicationFormFieldConfigurationRepository
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.project.entity.ProjectData
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.ProjectTransl
import io.cloudflight.jems.server.project.entity.TranslationId
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.projectWithId
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class ProjectServiceTest : UnitTest() {

    private val TEST_DATE: LocalDate = LocalDate.now()
    private val TEST_DATE_TIME = ZonedDateTime.of(TEST_DATE, LocalTime.of(10, 0), ZoneId.of("Europe/Bratislava"))

    private val account = UserEntity(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRoleEntity(id = 1, name = "ADMIN"),
        password = "hash_pass"
    )

    private val statusDraft = ProjectStatusHistoryEntity(
        id = 10,
        status = ApplicationStatus.DRAFT,
        user = account,
        updated = TEST_DATE_TIME
    )

    private val statusSubmitted = ProjectStatusHistoryEntity(
        id = 11,
        status = ApplicationStatus.SUBMITTED,
        user = account,
        updated = TEST_DATE_TIME
    )

    private val dummyCall = CallEntity(
        id = 5,
        creator = account,
        name = "call",
        prioritySpecificObjectives = mutableSetOf(ProgrammeSpecificObjectiveEntity(HealthyAgeing, "HAB")),
        strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.MediterraneanSeaBasin, true)),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        startDate = ZonedDateTime.now().minusDays(1),
        endDateStep1 = null,
        endDate = ZonedDateTime.now().plusDays(1),
        status = CallStatus.PUBLISHED,
        lengthOfPeriod = 1,
        applicationFormFieldConfigurationEntities = mutableSetOf()
    )

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var getProjectInteractor: GetProjectInteractor

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var projectService: ProjectServiceImpl

    private val projectData = InputProjectData(
        acronym = "new acronym",
        title = setOf(InputTranslation(SystemLanguage.EN, "title")),
        duration = 15,
        intro = setOf(InputTranslation(SystemLanguage.EN, "intro")),
        specificObjective = AdvancedTechnologies
    )

    @Test
    fun projectUpdate_notExisting() {
        every { projectRepository.findById(eq(-1)) } returns Optional.empty()
        val exception = assertThrows<ResourceNotFoundException> { projectService.update(-1, projectData) }
        assertThat(exception.entity).isEqualTo("project")
    }

    @Test
    fun projectUpdate_successful() {
        val projectToReturn = ProjectEntity(
            id = 1,
            call = dummyCall,
            acronym = "test acronym",
            applicant = account,
            currentStatus = statusSubmitted,
            firstSubmission = statusSubmitted,
        )
        every { projectRepository.findById(eq(1)) } returns Optional.of(projectToReturn)
        val slot = slot<ProjectEntity>()
        every { projectRepository.save(capture(slot)) } returnsArgument 0

        projectService.update(1, projectData.copy(specificObjective = HealthyAgeing))

        assertThat(slot.captured.projectData).isEqualTo(
            ProjectData(
                duration = projectData.duration,
                translatedValues = setOf(
                    ProjectTransl(TranslationId(1, SystemLanguage.EN), title = "title", intro = "intro"),
                ),
            )
        )
        assertThat(slot.captured.acronym).isEqualTo(projectData.acronym)
    }

    @Test
    fun `projectUpdate not existing policy`() {
        val projectToReturn = ProjectEntity(
            id = 1,
            call = dummyCall,
            acronym = "test acronym",
            applicant = account,
            currentStatus = statusSubmitted,
            firstSubmission = statusSubmitted,
        )
        every { projectRepository.findById(eq(1)) } returns Optional.of(projectToReturn)

        val ex = assertThrows<ResourceNotFoundException> {
            projectService.update(1, projectData.copy(specificObjective = SocialInfrastructure))
        }
        assertThat(ex.entity).isEqualTo("programmeSpecificObjective")
    }

    @Test
    fun `projectUpdate with periods successful`() {
        val callWithDuration = CallEntity(
            id = 5,
            creator = account,
            name = "call",
            prioritySpecificObjectives = mutableSetOf(ProgrammeSpecificObjectiveEntity(HealthyAgeing, "HAB")),
            strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.MediterraneanSeaBasin, true)),
            isAdditionalFundAllowed = false,
            funds = mutableSetOf(),
            startDate = ZonedDateTime.now(),
            endDateStep1 = null,
            endDate = ZonedDateTime.now(),
            status = CallStatus.PUBLISHED,
            lengthOfPeriod = 6,
            applicationFormFieldConfigurationEntities = mutableSetOf()
        )
        val projectData = InputProjectData(acronym = "acronym", duration = 13)
        val projectToReturn = ProjectEntity(
            id = 1,
            call = callWithDuration,
            acronym = "acronym",
            applicant = account,
            currentStatus = statusDraft,
        )
        every { projectRepository.findById(eq(1)) } returns Optional.of(projectToReturn)
        val slot = slot<ProjectEntity>()
        every { projectRepository.save(capture(slot)) } returnsArgument 0
        every { getProjectInteractor.getProject(1L) } returns projectWithId(1L).copy(
            acronym = "acronym",
            title = emptySet(),
            duration = 13,
            intro = emptySet(),
            periods = listOf(

            )
        )

        projectService.update(1, projectData)

        assertThat(slot.captured.projectData).isEqualTo(ProjectData(duration = projectData.duration))
        assertThat(slot.captured.acronym).isEqualTo(projectData.acronym)
        assertThat(slot.captured.periods).containsExactly(
            ProjectPeriodEntity(ProjectPeriodId(1, 1), 1, 6),
            ProjectPeriodEntity(ProjectPeriodId(1, 2), 7, 12),
            ProjectPeriodEntity(ProjectPeriodId(1, 3), 13, 13),
        )
    }
}
