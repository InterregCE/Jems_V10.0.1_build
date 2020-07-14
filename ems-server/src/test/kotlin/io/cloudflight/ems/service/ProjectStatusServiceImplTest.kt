package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProjectStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.entity.ProjectStatus
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.ProjectRepository
import io.cloudflight.ems.repository.ProjectStatusRepository
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.ZonedDateTime

internal class ProjectStatusServiceImplTest {

    companion object {
        const val NOTE_DENIED = "denied"
    }

    @MockK
    lateinit var projectRepository: ProjectRepository
    @MockK
    lateinit var userRepository: UserRepository
    @RelaxedMockK
    lateinit var auditService: AuditService
    @MockK
    lateinit var securityService: SecurityService
    @MockK
    lateinit var projectStatusRepository: ProjectStatusRepository

    lateinit var projectStatusService: ProjectStatusService

    private val user = User(
        id = 1,
        email = "applicant@programme.dev",
        name = "applicant",
        surname = "",
        userRole = UserRole(id = 3, name = "applicant user"),
        password = "hash_pass"
    )

    private val userApplicant = OutputUserWithRole(user.id, user.email, user.name, user.surname, OutputUserRole(user.userRole.id, user.userRole.name))

    private val projectDraft = createProject(ProjectApplicationStatus.DRAFT)
    private val projectSubmitted = createProject(ProjectApplicationStatus.SUBMITTED, NOTE_DENIED)

    private val projectStatus = ProjectStatus(
        id = 1,
        project = projectSubmitted,
        status = ProjectApplicationStatus.SUBMITTED,
        user = user,
        updated = ZonedDateTime.now(),
        note = null
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectStatusService = ProjectStatusServiceImpl(
            projectRepository, projectStatusRepository, userRepository, auditService, securityService
        )
    }

    @Test
    fun `project status submitted and note set successfully`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectDraft
        every { projectStatusRepository.save(any<ProjectStatus>()) } returns projectStatus
        every { projectRepository.save(any<Project>()) } returns projectSubmitted

        val result = projectStatusService.setProjectStatus(1, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, NOTE_DENIED))

        assertThat(result.id).isEqualTo(1)
        assertThat(result.submissionDate).isNotNull()
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)
        assertThat(result.projectStatus.note).isEqualTo(NOTE_DENIED)
    }

    @Test
    fun `project status set successfully`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectDraft
        every { projectStatusRepository.save(any<ProjectStatus>()) } returns projectStatus
        every { projectRepository.save(any<Project>()) } returns projectDraft

        val result = projectStatusService.setProjectStatus(1, InputProjectStatus(ProjectApplicationStatus.DRAFT, null))

        assertThat(result.id).isEqualTo(1)
        assertThat(result.submissionDate).isNull()
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.DRAFT)
        assertThat(result.projectStatus.note).isNull()
    }

    @Test
    fun `project status setting failed successfully`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(2) } returns null
        assertThrows<ResourceNotFoundException> {
            projectStatusService.setProjectStatus(2, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, null))
        }
    }

    private fun createProject(status: ProjectApplicationStatus, note: String? = null): Project {
        return Project(
            id = 1,
            acronym = "acronym",
            applicant = user,
            submissionDate = if (ProjectApplicationStatus.SUBMITTED == status) ZonedDateTime.now() else null,
            projectStatus = ProjectStatus(1, null, status, user, ZonedDateTime.now(), note)
        )
    }
}
