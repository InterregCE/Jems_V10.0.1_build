package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProjectEligibilityAssessment
import io.cloudflight.ems.api.dto.InputProjectQualityAssessment
import io.cloudflight.ems.api.dto.InputProjectStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.api.dto.ProjectEligibilityAssessmentResult
import io.cloudflight.ems.api.dto.ProjectQualityAssessmentResult
import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.entity.AuditAction
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
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal class ProjectStatusServiceImplTest {

    companion object {
        const val NOTE_DENIED = "denied"
        val SUBMIT_TIME = ZonedDateTime.of(LocalDate.of(2020, 7, 14), LocalTime.of(17, 0), ZoneId.of("Europe/Bratislava"))
        val DRAFT_TIME = ZonedDateTime.of(LocalDate.of(2020, 7, 13), LocalTime.of(12, 0), ZoneId.of("Europe/Bratislava"))
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
    private val userProgramme = OutputUserWithRole(16, "programme@email", "", "", OutputUserRole(7, "programme"))

    private val projectDraft = createProject(ProjectApplicationStatus.DRAFT)
    private val projectSubmitted = createProject(ProjectApplicationStatus.SUBMITTED, NOTE_DENIED)
    private val projectReturned = createProject(ProjectApplicationStatus.RETURNED_TO_APPLICANT)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectStatusService = ProjectStatusServiceImpl(
            projectRepository, projectStatusRepository, userRepository, auditService, securityService
        )
    }

    @Test
    fun `project status submitted`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectDraft
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result = projectStatusService.setProjectStatus(1, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, NOTE_DENIED))

        assertThat(result.id).isEqualTo(1)
        assertThat(result.submissionDate).isNotNull()
        assertThat(result.resubmissionDate).isNull()
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)
        assertThat(result.projectStatus.note).isEqualTo(NOTE_DENIED)
    }

    @Test
    fun `project status resubmitted`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectReturned
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result = projectStatusService.setProjectStatus(1, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, null))

        assertThat(result.id).isEqualTo(1)
        assertThat(result.submissionDate).isNotNull()
        assertThat(result.resubmissionDate).isNotNull()
        assertThat(result.submissionDate).isNotEqualTo(result.resubmissionDate)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)
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
        val time = if (ProjectApplicationStatus.SUBMITTED == status || ProjectApplicationStatus.RETURNED_TO_APPLICANT == status) SUBMIT_TIME else null
        return Project(
            id = 1,
            acronym = "acronym",
            applicant = user,
            submissionDate = time,
            projectStatus = ProjectStatus(1, null, status, user, time ?: DRAFT_TIME, note)
        )
    }

    @Test
    fun `set quality assessment`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(any()) } returns User(1, "programme@email", "", "", UserRole(7, "programme"), "hash_pass")
        every { projectRepository.findOneById(16) } returns projectSubmitted.copy(id = 16)
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val inputData = InputProjectQualityAssessment(
            result = ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING,
            note = "example note"
        )

        val result = projectStatusService.setQualityAssessment(16, inputData)
        assertThat(result.qualityAssessment!!.result).isEqualTo(ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)

        verify {
            auditService.logEvent(
                withArg {
                    assertThat(it.action).isEqualTo(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
                    assertThat(it.projectId).isEqualTo(16.toString())
                    assertThat(it.description).isEqualTo("Project application quality assessment concluded as RECOMMENDED_FOR_FUNDING")
                })
        }
    }

    @Test
    fun `set QA no user`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(eq(userProgramme.id!!)) } returns null

        val data = InputProjectQualityAssessment(ProjectQualityAssessmentResult.NOT_RECOMMENDED)
        assertThrows<ResourceNotFoundException> { projectStatusService.setQualityAssessment(-9, data) }
    }

    @Test
    fun `set QA no project`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(any()) } returns User(1, "programme@email", "", "", UserRole(7, "programme"), "hash_pass")
        every { projectRepository.findOneById(-51) } returns null

        val data = InputProjectQualityAssessment(ProjectQualityAssessmentResult.RECOMMENDED_WITH_CONDITIONS)
        assertThrows<ResourceNotFoundException> { projectStatusService.setQualityAssessment(-51, data) }
    }

    @Test
    fun `set eligibility assessment`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(any()) } returns User(1, "programme@email", "", "", UserRole(7, "programme"), "hash_pass")
        every { projectRepository.findOneById(79) } returns projectSubmitted.copy(id = 79)
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val inputData = InputProjectEligibilityAssessment(
            result = ProjectEligibilityAssessmentResult.PASSED,
            note = "example note"
        )

        val result = projectStatusService.setEligibilityAssessment(79, inputData)
        assertThat(result.eligibilityAssessment!!.result).isEqualTo(ProjectEligibilityAssessmentResult.PASSED)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)

        verify {
            auditService.logEvent(
                withArg {
                    assertThat(it.action).isEqualTo(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
                    assertThat(it.projectId).isEqualTo(79.toString())
                    assertThat(it.description).isEqualTo("Project application eligibility assessment concluded as PASSED")
                })
        }
    }

    @Test
    fun `set EA no user`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(eq(userProgramme.id!!)) } returns null

        val data = InputProjectEligibilityAssessment(ProjectEligibilityAssessmentResult.PASSED)
        assertThrows<ResourceNotFoundException> { projectStatusService.setEligibilityAssessment(-3, data) }
    }

    @Test
    fun `set EA no project`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(any()) } returns User(1, "programme@email", "", "", UserRole(7, "programme"), "hash_pass")
        every { projectRepository.findOneById(-22) } returns null

        val data = InputProjectEligibilityAssessment(ProjectEligibilityAssessmentResult.FAILED)
        assertThrows<ResourceNotFoundException> { projectStatusService.setEligibilityAssessment(-22, data) }
    }

}
