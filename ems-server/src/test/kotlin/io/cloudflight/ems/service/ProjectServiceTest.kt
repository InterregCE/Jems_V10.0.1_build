package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProjectSimple
import io.cloudflight.ems.api.dto.OutputProjectStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.AuditAction
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.entity.ProjectStatus
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.ProjectRepository
import io.cloudflight.ems.repository.ProjectStatusRepository
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.PROGRAMME_USER
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
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

val TEST_DATE: LocalDate = LocalDate.now()
val TEST_DATE_TIME = ZonedDateTime.of(TEST_DATE, LocalTime.of(10, 0), ZoneId.of("Europe/Bratislava"))

class ProjectServiceTest {

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

    private val statusDraft = ProjectStatus(
        id = 10,
        status = ProjectApplicationStatus.DRAFT,
        user = account,
        updated = TEST_DATE_TIME
    )

    private val statusSubmitted = ProjectStatus(
        id = 11,
        status = ProjectApplicationStatus.SUBMITTED,
        user = account,
        updated = TEST_DATE_TIME
    )

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectStatusRepository: ProjectStatusRepository

    @MockK
    lateinit var userRepository: UserRepository

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
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        projectService = ProjectServiceImpl(
            projectRepository,
            projectStatusRepository,
            userRepository,
            auditService,
            securityService
        )
    }

    @Test
    fun projectRetrieval_admin() {
        every { securityService.currentUser } returns
                LocalCurrentUser(user, "hash_pass", listOf(SimpleGrantedAuthority("ROLE_$ADMINISTRATOR")))

        val projectToReturn = Project(
            id = 25,
            acronym = "test acronym",
            applicant = account,
            projectStatus = statusSubmitted,
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
                acronym = "test acronym",
                firstSubmissionDate = TEST_DATE_TIME,
                lastResubmissionDate = null,
                projectStatus = OutputProjectStatus(
                    id = 11,
                    status = ProjectApplicationStatus.SUBMITTED,
                    user = userWithoutRole,
                    updated = TEST_DATE_TIME
                )
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

        projectService.findAll(UNPAGED);

        verify {
            projectRepository.findAllByProjectStatusStatusNot(
                withArg {
                    assertThat(it).isEqualTo(ProjectApplicationStatus.DRAFT)
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
        every { projectRepository.save(any<Project>()) } returns Project(
            612,
            "test",
            account,
            statusDraft
        )

        val result = projectService.createProject(InputProject("test"))

        assertEquals(result.acronym, "test")
        assertEquals(result.firstSubmission, null)
        assertEquals(result.lastResubmission, null)
        assertEquals(result.projectStatus.id, 10)
        assertEquals(result.projectStatus.status, ProjectApplicationStatus.DRAFT)
        assertEquals(result.projectStatus.updated, TEST_DATE_TIME)

        verifyAudit("612")
    }

    @Test
    fun projectCreation_withoutUser() {
        every { userRepository.findById(eq(user.id!!)) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { projectService.createProject(InputProject("test")) }
    }

    @Test
    fun projectGet_OK() {
        every { projectRepository.findOneById(eq(1)) } returns
                Project(1, "test", account, statusSubmitted, statusSubmitted)

        val result = projectService.getById(1);

        assertThat(result).isNotNull()
        assertThat(result.id).isEqualTo(1);
        assertThat(result.acronym).isEqualTo("test")
        assertThat(result.projectStatus.id).isEqualTo(11)
        assertThat(result.firstSubmission?.id).isEqualTo(11)
        assertThat(result.firstSubmission?.updated).isEqualTo(TEST_DATE_TIME)
    }

    @Test
    fun projectGet_notExisting() {
        every { projectRepository.findOneById(eq(-1)) } returns null
        assertThrows<ResourceNotFoundException> { projectService.getById(-1) }
    }

    private fun verifyAudit(projectIdExpected: String) {
        val event = slot<Audit>()

        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertEquals(projectIdExpected, captured.projectId)
            assertEquals(1, captured.user?.id)
            assertEquals("admin@admin.dev", captured.user?.email)
            assertEquals(AuditAction.APPLICATION_STATUS_CHANGED, captured.action)
        }
    }
}
