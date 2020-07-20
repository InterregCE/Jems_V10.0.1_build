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
import io.cloudflight.ems.entity.ProjectEligibilityAssessment
import io.cloudflight.ems.entity.ProjectStatus
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.exception.I18nValidationException
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
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal class ProjectStatusServiceImplTest {

    companion object {
        const val NOTE_DENIED = "denied"
        val DRAFT_TIME = ZonedDateTime.of(LocalDate.of(2020, 7, 13), LocalTime.of(12, 0), ZoneId.of("Europe/Bratislava"))
        val SUBMIT_TIME = DRAFT_TIME.plusDays(1)
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
    private val projectEligible = createProject(ProjectApplicationStatus.ELIGIBLE)

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

        val result = projectStatusService.setProjectStatus(1, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, NOTE_DENIED, null))

        assertThat(result.id).isEqualTo(1)
        assertThat(result.firstSubmission).isNotNull()
        assertThat(result.lastResubmission).isNull()
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)
        assertThat(result.projectStatus.note).isEqualTo(NOTE_DENIED)
    }

    @Test
    fun `project status re-submitted to SUBMITTED`() {
        val ignoreStatuses = setOf(ProjectApplicationStatus.DRAFT, ProjectApplicationStatus.RETURNED_TO_APPLICANT)
        val previousState = ProjectStatus(status = ProjectApplicationStatus.SUBMITTED, user = user)

        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectReturned
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectStatusRepository.findFirstByProjectIdAndStatusNotInOrderByUpdatedDesc(eq(1), eq(ignoreStatuses)) } returns previousState
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result = projectStatusService.setProjectStatus(1, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, null, null))

        assertThat(result.id).isEqualTo(1)
        assertThat(result.firstSubmission).isNotNull()
        assertThat(result.lastResubmission).isNotNull()
        assertThat(result.firstSubmission?.updated).isNotEqualTo(result.lastResubmission?.updated)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)
        assertThat(result.projectStatus.note).isNull()
    }

    @Test
    fun `project status re-submitted to ELIGIBLE`() {
        val ignoreStatuses = setOf(ProjectApplicationStatus.RETURNED_TO_APPLICANT, ProjectApplicationStatus.DRAFT)
        val previousState = ProjectStatus(status = ProjectApplicationStatus.ELIGIBLE, user = user)

        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectReturned
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectStatusRepository.findFirstByProjectIdAndStatusNotInOrderByUpdatedDesc(eq(1), eq(ignoreStatuses)) } returns previousState
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result = projectStatusService.setProjectStatus(1, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, null, null))

        assertThat(result.id).isEqualTo(1)
        assertThat(result.firstSubmission).isNotNull()
        assertThat(result.lastResubmission).isNotNull()
        assertThat(result.firstSubmission?.updated).isNotEqualTo(result.lastResubmission?.updated)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.ELIGIBLE)
        assertThat(result.projectStatus.note).isNull()
    }

    @Test
    fun `project status SUBMITTED to ELIGIBLE`() {
        val eligibilityAssessment = ProjectEligibilityAssessment(
            id = 10,
            project = projectSubmitted,
            result = ProjectEligibilityAssessmentResult.PASSED,
            user = user
        )
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectSubmitted.copy(eligibilityAssessment = eligibilityAssessment)
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result = projectStatusService.setProjectStatus(
            projectId = 1,
            statusChange = InputProjectStatus(ProjectApplicationStatus.ELIGIBLE, "some note", LocalDate.now().plusDays(1))
        )

        assertThat(result.id).isEqualTo(1)
        assertThat(result.eligibilityDecision).isNotNull()
        assertThat(result.eligibilityDecision?.status).isEqualTo(ProjectApplicationStatus.ELIGIBLE)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.ELIGIBLE)
        assertThat(result.projectStatus.note).isEqualTo("some note")
        assertThat(result.projectStatus).isEqualTo(result.eligibilityDecision)
    }

    @Test
    fun `project status SUBMITTED to ELIGIBLE missing date`() {
        val eligibilityAssessment = ProjectEligibilityAssessment(
            id = 10,
            project = projectSubmitted,
            result = ProjectEligibilityAssessmentResult.PASSED,
            user = user
        )
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectSubmitted.copy(eligibilityAssessment = eligibilityAssessment)
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val exception = assertThrows<I18nValidationException> {
            projectStatusService.setProjectStatus(
                projectId = 1,
                statusChange = InputProjectStatus(ProjectApplicationStatus.ELIGIBLE, "some note", null)
            )
        }

        assertThat(exception.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(exception.i18nKey).isEqualTo("project.decision.date.unknown")
    }

    @Test
    fun `project status ELIGIBLE to APPROVED funding decision`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectEligible
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0


        val result = projectStatusService.setProjectStatus(
            projectId = 1,
            statusChange = InputProjectStatus(ProjectApplicationStatus.APPROVED, null, LocalDate.now().plusDays(1))
        )

        assertThat(result.id).isEqualTo(1)
        assertThat(result.fundingDecision).isNotNull()
        assertThat(result.fundingDecision?.status).isEqualTo(ProjectApplicationStatus.APPROVED)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.APPROVED)
        assertThat(result.projectStatus.note).isEqualTo(null)
        assertThat(result.projectStatus).isEqualTo(result.fundingDecision)
    }

    @Test
    fun `project status ELIGIBLE to APPROVED_WITH_CONDITIONS funding decision`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectEligible
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0


        val result = projectStatusService.setProjectStatus(
            projectId = 1,
            statusChange = InputProjectStatus(ProjectApplicationStatus.APPROVED_WITH_CONDITIONS, "some note", LocalDate.now().plusDays(1))
        )

        assertThat(result.id).isEqualTo(1)
        assertThat(result.fundingDecision).isNotNull()
        assertThat(result.fundingDecision?.status).isEqualTo(ProjectApplicationStatus.APPROVED_WITH_CONDITIONS)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.APPROVED_WITH_CONDITIONS)
        assertThat(result.projectStatus.note).isEqualTo("some note")
        assertThat(result.projectStatus).isEqualTo(result.fundingDecision)
    }

    @Test
    fun `project status ELIGIBLE to NOT_APPROVED funding decision`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(1) } returns projectEligible
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0


        val result = projectStatusService.setProjectStatus(
            projectId = 1,
            statusChange = InputProjectStatus(ProjectApplicationStatus.NOT_APPROVED, "some note", LocalDate.now().plusDays(1))
        )

        assertThat(result.id).isEqualTo(1)
        assertThat(result.fundingDecision).isNotNull()
        assertThat(result.fundingDecision?.status).isEqualTo(ProjectApplicationStatus.NOT_APPROVED)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.NOT_APPROVED)
        assertThat(result.projectStatus.note).isEqualTo("some note")
        assertThat(result.projectStatus).isEqualTo(result.fundingDecision)
    }


    @Test
    fun `project status setting failed successfully`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findOneById(2) } returns null
        assertThrows<ResourceNotFoundException> {
            projectStatusService.setProjectStatus(2, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, null, null))
        }
    }

    private fun createProject(status: ProjectApplicationStatus, note: String? = null): Project {
        var submitTime: ZonedDateTime?
        var statusTime: ZonedDateTime
        if (status == ProjectApplicationStatus.DRAFT) {
            submitTime = null
            statusTime = DRAFT_TIME
        } else if (status == ProjectApplicationStatus.SUBMITTED) {
            submitTime = SUBMIT_TIME
            statusTime = SUBMIT_TIME
        } else { // status RETURNED_TO_APPLICANT
            submitTime = SUBMIT_TIME
            statusTime = SUBMIT_TIME.plusDays(1)
        }
        return Project(
            id = 1,
            acronym = "acronym",
            applicant = user,
            projectStatus = ProjectStatus(1, null, status, user, statusTime, null, note),
            firstSubmission = if (submitTime != null) ProjectStatus(2, null, ProjectApplicationStatus.SUBMITTED, user, submitTime, null, note) else null
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
