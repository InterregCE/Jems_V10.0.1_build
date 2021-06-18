package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.HealthyAgeing
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.SocialInfrastructure
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputProject
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.model.ADMINISTRATOR
import io.cloudflight.jems.server.authentication.model.APPLICANT_USER
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.model.PROGRAMME_USER
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.controller.toDTO
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.repository.CallPersistenceProvider
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
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
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.projectWithId
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.stream.Collectors

class ProjectServiceTest : UnitTest() {

    private val TEST_DATE: LocalDate = LocalDate.now()
    private val TEST_DATE_TIME = ZonedDateTime.of(TEST_DATE, LocalTime.of(10, 0), ZoneId.of("Europe/Bratislava"))

    private val UNPAGED = Pageable.unpaged()

    private val user = User(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRole(id = 1, name = "ADMIN", permissions = emptySet())
    )

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

    private val statusStep1Draft = ProjectStatusHistoryEntity(
        id = 10,
        status = ApplicationStatus.STEP1_DRAFT,
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
        lengthOfPeriod = 1
    )

    private val dummyCall2Step = CallEntity(
        id = 6,
        creator = account,
        name = "call",
        prioritySpecificObjectives = mutableSetOf(ProgrammeSpecificObjectiveEntity(HealthyAgeing, "HAB")),
        strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.MediterraneanSeaBasin, true)),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        startDate = ZonedDateTime.now().minusDays(1),
        endDateStep1 = ZonedDateTime.now().plusHours(2),
        endDate = ZonedDateTime.now().plusDays(1),
        status = CallStatus.PUBLISHED,
        lengthOfPeriod = 1
    )

    private val dummyCall2StepExpired = CallEntity(
        id = 8,
        creator = account,
        name = "call",
        prioritySpecificObjectives = mutableSetOf(ProgrammeSpecificObjectiveEntity(HealthyAgeing, "HAB")),
        strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.MediterraneanSeaBasin, true)),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        startDate = ZonedDateTime.now().minusDays(1),
        endDateStep1 = ZonedDateTime.now().minusHours(2),
        endDate = ZonedDateTime.now().plusDays(1),
        status = CallStatus.PUBLISHED,
        lengthOfPeriod = 1
    )

    private val dummyCallExpired = CallEntity(
        id = 9,
        creator = account,
        name = "call",
        prioritySpecificObjectives = mutableSetOf(ProgrammeSpecificObjectiveEntity(HealthyAgeing, "HAB")),
        strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.MediterraneanSeaBasin, true)),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        startDate = ZonedDateTime.now().minusDays(1),
        endDateStep1 = ZonedDateTime.now().minusHours(2),
        endDate = ZonedDateTime.now().minusHours(1),
        status = CallStatus.PUBLISHED,
        lengthOfPeriod = 1
    )
    private val applicationFormConfiguration = ApplicationFormConfiguration(1, "test configuration", mutableSetOf())

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var getProjectInteractor: GetProjectInteractor

    @MockK
    lateinit var projectStatusHistoryRepository: ProjectStatusHistoryRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var callRepository: CallRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var  callPersistenceProvider: CallPersistenceProvider

    @InjectMockKs
    lateinit var projectService: ProjectServiceImpl

    @BeforeAll
    fun setup() {
        every { securityService.currentUser } returns LocalCurrentUser(user, "hash_pass", emptyList())
        every { projectStatusHistoryRepository.save(any<ProjectStatusHistoryEntity>()) } returnsArgument 0
        every { callPersistenceProvider.getApplicationFormConfiguration(1L) } returns ApplicationFormConfiguration(1,"test configuration", mutableSetOf())
    }

    @Test
    fun projectRetrieval_admin() {
        every { securityService.currentUser } returns
            LocalCurrentUser(user, "hash_pass", listOf(SimpleGrantedAuthority("ROLE_$ADMINISTRATOR")))

        val projectToReturn = ProjectEntity(
            id = 25,
            call = dummyCall,
            acronym = "test acronym",
            applicant = account,
            currentStatus = statusSubmitted,
            firstSubmission = statusSubmitted,
        )
        every { projectRepository.findAll(UNPAGED) } returns PageImpl(listOf(projectToReturn))

        // test start
        val result = projectService.findAll(UNPAGED)

        // assertions:
        assertEquals(1, result.totalElements)

        val expectedProjects = listOf(
            OutputProjectSimple(
                id = 25,
                callName = dummyCall.name,
                acronym = "test acronym",
                firstSubmissionDate = TEST_DATE_TIME,
                lastResubmissionDate = null,
                projectStatus = ApplicationStatusDTO.SUBMITTED,
                specificObjectiveCode = null,
                programmePriorityCode = null
            )
        )
        assertIterableEquals(expectedProjects, result.get().collect(Collectors.toList()))
    }

    @Test
    fun projectRetrieval_programme_user() {
        every { securityService.currentUser } returns
            LocalCurrentUser(user, "hash_pass", listOf(SimpleGrantedAuthority("ROLE_$PROGRAMME_USER")))

        val projectToReturn = ProjectEntity(
            id = 25,
            call = dummyCall,
            acronym = "test acronym",
            applicant = account,
            currentStatus = statusSubmitted,
            firstSubmission = statusSubmitted,
        )
        every { projectRepository.findAll(UNPAGED) } returns PageImpl(listOf(projectToReturn))

        // test start
        val result = projectService.findAll(UNPAGED)

        // assertions:
        assertEquals(1, result.totalElements)

        val expectedProjects = listOf(
            OutputProjectSimple(
                id = 25,
                callName = dummyCall.name,
                acronym = "test acronym",
                firstSubmissionDate = TEST_DATE_TIME,
                lastResubmissionDate = null,
                projectStatus = ApplicationStatusDTO.SUBMITTED,
                specificObjectiveCode = null,
                programmePriorityCode = null
            )
        )
        assertIterableEquals(expectedProjects, result.get().collect(Collectors.toList()))
    }

    @Test
    fun projectRetrieval_applicant() {
        every { securityService.currentUser } returns
            LocalCurrentUser(user, "hash_pass", listOf(SimpleGrantedAuthority("ROLE_$APPLICANT_USER")))
        every { projectRepository.findAllByApplicantId(eq(user.id!!), UNPAGED) } returns PageImpl(emptyList())

        assertEquals(0, projectService.findAll(UNPAGED).totalElements)
    }

    @Test
    fun projectCreation_OK() {
        every { userRepository.findByIdOrNull(eq(user.id)) } returns account
        every { callRepository.findById(eq(dummyCall.id)) } returns Optional.of(dummyCall)
        every { projectRepository.save(any()) } returns ProjectEntity(
            id = 612,
            call = dummyCall,
            acronym = "test",
            applicant = account,
            currentStatus = statusDraft,
        )
        val slotAudit = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } answers {}

        val result = projectService.createProject(InputProject("test", dummyCall.id))

        assertEquals(
            ProjectCallSettingsDTO(
                callId = dummyCall.id,
                callName = dummyCall.name,
                startDate = dummyCall.startDate,
                endDate = dummyCall.endDate,
                endDateStep1 = dummyCall.endDateStep1,
                lengthOfPeriod = 1,
                isAdditionalFundAllowed = false,
                flatRates = FlatRateSetupDTO(),
                lumpSums = emptyList(),
                unitCosts = emptyList(),
                applicationFormConfiguration = applicationFormConfiguration.toDTO()
            ), result.callSettings
        )
        assertEquals(result.acronym, "test")
        assertEquals(result.firstSubmission, null)
        assertEquals(result.lastResubmission, null)
        assertEquals(result.projectStatus.id, 10)
        assertEquals(result.projectStatus.status, ApplicationStatusDTO.DRAFT)
        assertEquals(result.projectStatus.updated, TEST_DATE_TIME)

        assertThat(slotAudit).hasSize(2)
        with(slotAudit[0]) {
            assertEquals("612", auditCandidate.project?.id)
            assertEquals(AuditAction.APPLICATION_STATUS_CHANGED, auditCandidate.action)
            assertEquals("Project application created with status DRAFT", auditCandidate.description)
        }
        with(slotAudit[1]) {
            assertEquals("612", auditCandidate.project?.id)
            assertEquals(AuditAction.APPLICATION_VERSION_RECORDED, auditCandidate.action)
            assertThat(auditCandidate.description).startsWith("New project version \"V.1.0\" is recorded by user: admin@admin.dev on")
        }
    }

    @Test
    fun projectCreation_withoutUser() {
        every { userRepository.findByIdOrNull(eq(user.id)) } returns null
        assertThrows<ResourceNotFoundException> { projectService.createProject(InputProject("test", dummyCall.id)) }
    }

    @Test
    fun projectCreation_beforeEndDateStep1() {
        every { userRepository.findByIdOrNull(eq(user.id)) } returns account
        every { callRepository.findById(eq(dummyCall2Step.id)) } returns Optional.of(dummyCall2Step)
        every { projectRepository.save(any()) } returns ProjectEntity(
            id = 613,
            call = dummyCall2Step,
            acronym = "test",
            applicant = account,
            currentStatus = statusStep1Draft,
        )

        val slotAudit = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } answers {}

        val result = projectService.createProject(InputProject("test", dummyCall2Step.id))

        assertEquals(
            ProjectCallSettingsDTO(
                callId = dummyCall2Step.id,
                callName = dummyCall2Step.name,
                startDate = dummyCall2Step.startDate,
                endDate = dummyCall2Step.endDate,
                endDateStep1 = dummyCall2Step.endDateStep1,
                lengthOfPeriod = 1,
                isAdditionalFundAllowed = false,
                flatRates = FlatRateSetupDTO(),
                lumpSums = emptyList(),
                unitCosts = emptyList(),
                applicationFormConfiguration = applicationFormConfiguration.toDTO()
            ), result.callSettings
        )
        assertEquals(result.acronym, "test")
        assertEquals(result.firstSubmission, null)
        assertEquals(result.lastResubmission, null)
        assertEquals(result.projectStatus.id, 10)
        assertEquals(result.projectStatus.status, ApplicationStatusDTO.STEP1_DRAFT)
        assertEquals(result.projectStatus.updated, TEST_DATE_TIME)

        assertThat(slotAudit).hasSize(2)
        with(slotAudit[0]) {
            assertEquals("613", auditCandidate.project?.id)
            assertEquals(AuditAction.APPLICATION_STATUS_CHANGED, auditCandidate.action)
            assertEquals("Project application created with status STEP1_DRAFT", auditCandidate.description)
        }
        with(slotAudit[1]) {
            assertEquals("613", auditCandidate.project?.id)
            assertEquals(AuditAction.APPLICATION_VERSION_RECORDED, auditCandidate.action)
            assertThat(auditCandidate.description).startsWith("New project version \"V.1.0\" is recorded by user: admin@admin.dev on")
        }

    }

    @Test
    fun projectCreation_afterEndDateStep1() {
        every { userRepository.findByIdOrNull(eq(user.id)) } returns account
        every { callRepository.findById(eq(dummyCall2StepExpired.id)) } returns Optional.of(dummyCall2StepExpired)

        val ex = assertThrows<I18nValidationException> {
            projectService.createProject(
                InputProject(
                    "test",
                    dummyCall2StepExpired.id
                )
            )
        }
        assertThat(ex).isEqualTo(
            I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "call.not.open"
            )
        )
    }

    @Test
    fun projectCreation_afterEndDate() {
        every { userRepository.findByIdOrNull(eq(user.id)) } returns account
        every { callRepository.findById(eq(dummyCallExpired.id)) } returns Optional.of(dummyCallExpired)

        val ex = assertThrows<I18nValidationException> {
            projectService.createProject(
                InputProject(
                    "test",
                    dummyCallExpired.id
                )
            )
        }
        assertThat(ex).isEqualTo(
            I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "call.not.open"
            )
        )
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
            lengthOfPeriod = 6
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
