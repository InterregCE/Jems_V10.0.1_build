package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.HealthyAgeing
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.SocialInfrastructure
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputProject
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectDataDTO
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.model.ADMINISTRATOR
import io.cloudflight.jems.server.authentication.model.APPLICANT_USER
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.model.PROGRAMME_USER
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.cloudflight.jems.server.user.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Optional
import java.util.stream.Collectors

class ProjectServiceTest {

    private val TEST_DATE: LocalDate = LocalDate.now()
    private val TEST_DATE_TIME = ZonedDateTime.of(TEST_DATE, LocalTime.of(10, 0), ZoneId.of("Europe/Bratislava"))

    private val UNPAGED = Pageable.unpaged()

    private val user = OutputUserWithRole(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "ADMIN")
    )

    private val userWithoutRole = OutputUser(
        id = user.id,
        email = user.email,
        name = user.name,
        surname = user.surname
    )

    private val account = User(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRole(id = 1, name = "ADMIN"),
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
        lengthOfPeriod = 1
    )

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectStatusHistoryRepository: ProjectStatusHistoryRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var callRepository: CallRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    @MockK
    lateinit var securityService: SecurityService

    lateinit var projectService: ProjectService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(user, "hash_pass", emptyList())
        every { userRepository.findById(eq(user.id!!)) } returns Optional.of(account)
        every { projectStatusHistoryRepository.save(any<ProjectStatusHistoryEntity>()) } returnsArgument 0
        projectService = ProjectServiceImpl(
            projectRepository,
            projectStatusHistoryRepository,
            callRepository,
            userRepository,
            auditService,
            securityService
        )
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
            firstSubmission = statusSubmitted
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
    fun `programme user lists submitted projects`() {
        every { securityService.currentUser } returns
            LocalCurrentUser(
                user, "hash_pass",
                listOf(SimpleGrantedAuthority("ROLE_$PROGRAMME_USER"))
            )

        projectService.findAll(UNPAGED)

        verify {
            projectRepository.findAllByCurrentStatusStatusNot(
                withArg {
                    assertThat(it).isEqualTo(ApplicationStatus.DRAFT)
                }, UNPAGED
            )
        }
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
        every { callRepository.findById(eq(dummyCall.id)) } returns Optional.of(dummyCall)
        every { projectRepository.save(any()) } returns ProjectEntity(
            id = 612,
            call = dummyCall,
            acronym = "test",
            applicant = account,
            currentStatus = statusDraft
        )

        val result = projectService.createProject(InputProject("test", dummyCall.id))

        assertEquals(ProjectCallSettingsDTO(
            callId = dummyCall.id,
            callName = dummyCall.name,
            startDate = dummyCall.startDate,
            endDate = dummyCall.endDate,
            lengthOfPeriod = 1,
            isAdditionalFundAllowed = false,
            flatRates = FlatRateSetupDTO(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
        ), result.callSettings)
        assertEquals(result.acronym, "test")
        assertEquals(result.firstSubmission, null)
        assertEquals(result.lastResubmission, null)
        assertEquals(result.projectStatus.id, 10)
        assertEquals(result.projectStatus.status, ApplicationStatusDTO.DRAFT)
        assertEquals(result.projectStatus.updated, TEST_DATE_TIME)

        verifyAudit("612")
    }

    @Test
    fun projectCreation_withoutUser() {
        every { userRepository.findById(eq(user.id!!)) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { projectService.createProject(InputProject("test", dummyCall.id)) }
    }

    @Test
    fun projectGet_OK() {
        every { projectRepository.findById(eq(1)) } returns
            Optional.of(ProjectEntity(id = 1, call = dummyCall, acronym = "test", applicant = account, currentStatus = statusSubmitted, firstSubmission = statusSubmitted))

        val result = projectService.getById(1)

        assertThat(result).isNotNull()
        assertThat(result.id).isEqualTo(1)
        assertThat(result.acronym).isEqualTo("test")
        assertThat(result.projectStatus.id).isEqualTo(11)
        assertThat(result.firstSubmission?.id).isEqualTo(11)
        assertThat(result.firstSubmission?.updated).isEqualTo(TEST_DATE_TIME)
    }

    @Test
    fun projectGet_notExisting() {
        every { projectRepository.findById(eq(-1)) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { projectService.getById(-1) }
    }

    private fun verifyAudit(projectIdExpected: String) {
        val event = slot<AuditCandidate>()

        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertEquals(projectIdExpected, captured.project?.id)
            assertEquals(AuditAction.APPLICATION_STATUS_CHANGED, captured.action)
        }
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
            firstSubmission = statusSubmitted
        )
        every { projectRepository.findById(eq(1)) } returns Optional.of(projectToReturn)
        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0

        val result = projectService.update(1, projectData.copy(specificObjective = HealthyAgeing))

        val expectedData = ProjectDataDTO(
            title = projectData.title,
            duration = projectData.duration,
            intro = projectData.intro,
            specificObjective = OutputProgrammePriorityPolicySimpleDTO(programmeObjectivePolicy = HealthyAgeing, code = "HAB"),
            programmePriority = null
        )
        assertThat(result.projectData).isEqualTo(expectedData)
        assertThat(result.acronym).isEqualTo(projectData.acronym)
    }

    @Test
    fun `projectUpdate not existing policy`() {
        val projectToReturn = ProjectEntity(
            id = 1,
            call = dummyCall,
            acronym = "test acronym",
            applicant = account,
            currentStatus = statusSubmitted,
            firstSubmission = statusSubmitted
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
            currentStatus = statusDraft
        )
        every { projectRepository.findById(eq(1)) } returns Optional.of(projectToReturn)
        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0

        val result = projectService.update(1, projectData)

        val expectedData = ProjectDataDTO(
            title = projectData.title,
            duration = projectData.duration,
            intro = projectData.intro,
            specificObjective = null,
            programmePriority = null
        )
        assertThat(result.projectData).isEqualTo(expectedData)
        assertThat(result.acronym).isEqualTo(projectData.acronym)
        assertThat(result.periods).containsExactly(
            ProjectPeriodDTO(1, 1, 1, 6),
            ProjectPeriodDTO(1, 2, 7, 12),
            ProjectPeriodDTO(1, 3, 13, 13)
        )
    }
}
