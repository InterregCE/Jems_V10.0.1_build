package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Healthcare
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.IndustrialTransition
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.SocialInfrastructure
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.defaultAllowedRealCostsByCallType
import io.cloudflight.jems.server.call.entity.CallEntity
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
import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.project.entity.result.ProjectResultId
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputId
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.workpackage.WorkPackageRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.WorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Optional

class ProjectServiceTest : UnitTest() {

    private val TEST_DATE: LocalDate = LocalDate.now()
    private val TEST_DATE_TIME = ZonedDateTime.of(TEST_DATE, LocalTime.of(10, 0), ZoneId.of("Europe/Bratislava"))

    private val account = UserEntity(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRoleEntity(id = 1, name = "ADMIN"),
        password = "hash_pass",
        userStatus = UserStatus.ACTIVE
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
        prioritySpecificObjectives = mutableSetOf(ProgrammeSpecificObjectiveEntity(Healthcare, "HAB")),
        strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.MediterraneanSeaBasin, true)),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        startDate = ZonedDateTime.now().minusDays(1),
        endDateStep1 = null,
        endDate = ZonedDateTime.now().plusDays(1),
        status = CallStatus.PUBLISHED,
        type = CallType.STANDARD,
        lengthOfPeriod = 1,
        allowedRealCosts = defaultAllowedRealCostsByCallType(CallType.STANDARD),
        preSubmissionCheckPluginKey = null,
        firstStepPreSubmissionCheckPluginKey = null
    )

    private fun wpWithActivity(id: Long, project: ProjectEntity, activityStartPeriod: Int, activityEndPeriod: Int, deliverablePeriod: Int) =
        WorkPackageEntity(id = id, project = project)
            .apply {
                activities.add(
                    WorkPackageActivityEntity(
                        workPackage = this,
                        activityNumber = 1,
                        startPeriod = activityStartPeriod,
                        endPeriod = activityEndPeriod,
                    ).apply {
                        deliverables.add(
                            WorkPackageActivityDeliverableEntity(
                                deliverableNumber = 1,
                                startPeriod = deliverablePeriod,
                                workPackageActivity = this
                            )
                        )
                    }
                )
            }

    private fun callWithDurationAndSos(lengthOfPeriod: Int, sos: Iterable<ProgrammeObjectivePolicy>) = CallEntity(
        id = 5,
        creator = account,
        name = "call",
        prioritySpecificObjectives = sos.map { ProgrammeSpecificObjectiveEntity(it, "$it code") }.toMutableSet(),
        strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.MediterraneanSeaBasin, true)),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        startDate = ZonedDateTime.now(),
        endDateStep1 = null,
        endDate = ZonedDateTime.now(),
        status = CallStatus.PUBLISHED,
        type = CallType.STANDARD,
        lengthOfPeriod = lengthOfPeriod,
        allowedRealCosts = defaultAllowedRealCostsByCallType(CallType.STANDARD),
        preSubmissionCheckPluginKey = null,
        firstStepPreSubmissionCheckPluginKey = null
    )

    private fun project(call: CallEntity, status: ProjectStatusHistoryEntity, acronym: String, resultPeriodNumber: Int) = ProjectEntity(
        id = 10,
        call = call,
        acronym = acronym,
        applicant = account,
        currentStatus = status,
        results = setOf(ProjectResultEntity(ProjectResultId(10, 1), periodNumber = resultPeriodNumber))
    )

    private fun mockLatestProjectFormRetrieval(projectId: Long) {
        val resultProject = ProjectFull(
            id = projectId,
            customIdentifier = "",
            callSettings = mockk(),
            acronym = "",
            applicant = mockk(),
            projectStatus = mockk(),
            duration = 13,
        )
        every { persistence.getProject(projectId) } returns resultProject
    }

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var workPackageRepository: WorkPackageRepository

    @MockK
    lateinit var workPackageOutputRepository: WorkPackageOutputRepository

    @MockK
    lateinit var persistence: ProjectPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var projectService: ProjectServiceImpl

    @BeforeEach
    fun reset() {
        clearMocks(workPackageRepository)
        clearMocks(persistence)
    }

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
    fun `projectUpdate successful - no change in period nor specific objective`() {
        val projectToReturn = ProjectEntity(
            id = 1,
            call = dummyCall,
            priorityPolicy = ProgrammeSpecificObjectiveEntity(Healthcare, "H", mockk()),
            acronym = "test acronym",
            applicant = account,
            currentStatus = statusSubmitted,
            firstSubmission = statusSubmitted,
        )
        every { projectRepository.findById(eq(1)) } returns Optional.of(projectToReturn)
        val slot = slot<ProjectEntity>()
        every { projectRepository.save(capture(slot)) } returnsArgument 0
        mockLatestProjectFormRetrieval(projectId = 1L)

        projectService.update(1, projectData.copy(specificObjective = Healthcare, duration = null))

        assertThat(slot.captured.projectData).isEqualTo(
            ProjectData(
                duration = null,
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
            priorityPolicy = ProgrammeSpecificObjectiveEntity(SocialInfrastructure, "SI", mockk()),
            acronym = "test acronym",
            applicant = account,
            currentStatus = statusSubmitted,
            firstSubmission = statusSubmitted,
        )
        every { projectRepository.findById(eq(1)) } returns Optional.of(projectToReturn)

        val ex = assertThrows<ResourceNotFoundException> {
            projectService.update(1, projectData.copy(specificObjective = SocialInfrastructure, duration = null))
        }
        assertThat(ex.entity).isEqualTo("programmeSpecificObjective")
    }

    @Test
    fun `update with restricted contracted fields`() {
        val projectToReturn = ProjectEntity(
            id = 1,
            call = dummyCall,
            acronym = "test acronym",
            applicant = account,
            currentStatus = ProjectStatusHistoryEntity(
                id = 11,
                status = ApplicationStatus.CONTRACTED,
                user = account,
                updated = TEST_DATE_TIME
            ),
            firstSubmission = statusSubmitted,
        )
        every { projectRepository.findById(eq(1)) } returns Optional.of(projectToReturn)

        assertThrows<UpdateRestrictedFieldsWhenProjectContracted> {
            projectService.update(1, projectData.copy(specificObjective = SocialInfrastructure, duration = null))
        }
    }

    @Test
    fun `projectUpdate with periods change and specific objective change`() {
        val call = callWithDurationAndSos(lengthOfPeriod = 6, sos = setOf(Healthcare, IndustrialTransition))
        val project = project(call, statusDraft, "old acronym", resultPeriodNumber = 14)
        val workPackage = wpWithActivity(id = 500L, project, activityStartPeriod = 3, activityEndPeriod = 12, deliverablePeriod = 16)

        val toBeChanged = InputProjectData(acronym = "new acronym", duration = 13, specificObjective = IndustrialTransition)

        every { projectRepository.findById(eq(10L)) } returns Optional.of(project)
        every { workPackageRepository.findAllByProjectId(project.id) } returns listOf(workPackage)
        val output = WorkPackageOutputEntity(WorkPackageOutputId(500, 1), periodNumber = 18, programmeOutputIndicatorEntity = mockk())
        every { workPackageOutputRepository.findAllByOutputIdWorkPackageIdIn(setOf(500L)) } returns listOf(output)

        val slot = slot<ProjectEntity>()
        every { projectRepository.save(capture(slot)) } returnsArgument 0

        mockLatestProjectFormRetrieval(projectId = 10L)

        projectService.update(10L, toBeChanged)

        assertThat(workPackage.activities.first().startPeriod).isEqualTo(3)
        assertThat(workPackage.activities.first().endPeriod).isNull()
        assertThat(workPackage.activities.first().deliverables.first().startPeriod).isNull()
        assertThat(output.periodNumber).isNull()
        assertThat(project.results.first().periodNumber).isNull()

        assertThat(project.results.first().programmeResultIndicatorEntity).isNull()
        assertThat(output.programmeOutputIndicatorEntity).isNull()

        // additional
        assertThat(slot.captured.projectData).isEqualTo(ProjectData(duration = 13))
        assertThat(slot.captured.acronym).isEqualTo("new acronym")
        assertThat(slot.captured.periods).containsExactly(
            ProjectPeriodEntity(ProjectPeriodId(10, 1), 1, 6),
            ProjectPeriodEntity(ProjectPeriodId(10, 2), 7, 12),
            ProjectPeriodEntity(ProjectPeriodId(10, 3), 13, 13),
        )
    }
}
