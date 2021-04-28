package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.DisadvantagedGroups
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.ProjectEligibilityAssessmentResult
import io.cloudflight.jems.api.project.dto.status.ProjectQualityAssessmentResult
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.project.entity.ProjectDecisionEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.repository.ProjectDecisionRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
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
import java.util.Optional

internal class ProjectStatusServiceTest {

    companion object {
        const val NOTE_DENIED = "denied"
        val DRAFT_TIME =
            ZonedDateTime.of(LocalDate.of(2020, 7, 13), LocalTime.of(12, 0), ZoneId.of("Europe/Bratislava"))
        val SUBMIT_TIME = DRAFT_TIME.plusDays(1)
    }

    @MockK
    lateinit var projectRepository: ProjectRepository

    @RelaxedMockK
    lateinit var projectDecisionRepository: ProjectDecisionRepository

    @MockK
    lateinit var userRepository: UserRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectStatusHistoryRepository: ProjectStatusHistoryRepository

    lateinit var projectStatusService: ProjectStatusService

    private val user = UserEntity(
        id = 1,
        email = "applicant@programme.dev",
        name = "applicant",
        surname = "",
        userRole = UserRoleEntity(id = 3, name = "applicant user"),
        password = "hash_pass"
    )

    private val userProgramme = User(16, "programme@email", "", "", UserRole(7, "programme", emptySet()))

    private val dummyCall = CallEntity(
        id = 5,
        creator = user,
        name = "call",
        prioritySpecificObjectives = mutableSetOf(ProgrammeSpecificObjectiveEntity(DisadvantagedGroups, "DG")),
        strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.SeaBasinStrategyArcticOcean, true)),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        startDate = ZonedDateTime.now().minusDays(2),
        endDateStep1 = null,
        endDate = ZonedDateTime.now().plusDays(2),
        status = CallStatus.PUBLISHED,
        lengthOfPeriod = 1
    )

    private val projectSubmitted = createProject(ApplicationStatus.SUBMITTED, NOTE_DENIED)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectStatusService = ProjectStatusServiceImpl(
            projectRepository, userRepository, projectDecisionRepository, auditService, securityService
        )
    }

    private fun createProject(status: ApplicationStatus, note: String? = null): ProjectEntity {
        val submitTime: ZonedDateTime?
        val statusTime: ZonedDateTime
        if (status == ApplicationStatus.DRAFT) {
            submitTime = null
            statusTime = DRAFT_TIME
        } else if (status == ApplicationStatus.SUBMITTED) {
            submitTime = SUBMIT_TIME
            statusTime = SUBMIT_TIME
        } else { // status RETURNED_TO_APPLICANT
            submitTime = SUBMIT_TIME
            statusTime = SUBMIT_TIME.plusDays(1)
        }
        return ProjectEntity(
            id = 1,
            call = dummyCall,
            acronym = "acronym",
            applicant = user,
            currentStatus = ProjectStatusHistoryEntity(1, null, status, user, statusTime, null, note),
            firstSubmission = if (submitTime != null) ProjectStatusHistoryEntity(
                2,
                null,
                ApplicationStatus.SUBMITTED,
                user,
                submitTime,
                null,
                note
            ) else null,
            step2Active = false
        )
    }

    @Test
    fun `set quality assessment`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(any()) } returns UserEntity(
            1,
            "programme@email",
            "",
            "",
            UserRoleEntity(7, "programme"),
            "hash_pass"
        )
        every { projectRepository.findById(16) } returns Optional.of(projectSubmitted.copy(id = 16))
        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
        every { projectDecisionRepository.save(any<ProjectDecisionEntity>()) } returnsArgument 0

        val inputData = InputProjectQualityAssessment(
            result = ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING,
            note = "example note"
        )

        val result = projectStatusService.setQualityAssessment(16, inputData)
        assertThat(result.firstStepDecision?.qualityAssessment!!.result).isEqualTo(ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING)
        assertThat(result.projectStatus.status).isEqualTo(ApplicationStatusDTO.SUBMITTED)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        assertThat(event.captured.action).isEqualTo(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
        assertThat(event.captured.project?.id).isEqualTo(16.toString())
        assertThat(event.captured.description).isEqualTo("Project application quality assessment concluded as RECOMMENDED_FOR_FUNDING")

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
        every { userRepository.findByIdOrNull(any()) } returns UserEntity(
            1,
            "programme@email",
            "",
            "",
            UserRoleEntity(7, "programme"),
            "hash_pass"
        )
        every { projectRepository.findById(-51) } returns Optional.empty()

        val data = InputProjectQualityAssessment(ProjectQualityAssessmentResult.RECOMMENDED_WITH_CONDITIONS)
        assertThrows<ResourceNotFoundException> { projectStatusService.setQualityAssessment(-51, data) }
    }

    @Test
    fun `set eligibility assessment`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(any()) } returns UserEntity(
            1,
            "programme@email",
            "",
            "",
            UserRoleEntity(7, "programme"),
            "hash_pass"
        )
        every { projectRepository.findById(79) } returns Optional.of(projectSubmitted.copy(id = 79))
        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
        every { projectDecisionRepository.save(any<ProjectDecisionEntity>()) } returnsArgument 0

        val inputData = InputProjectEligibilityAssessment(
            result = ProjectEligibilityAssessmentResult.PASSED,
            note = "example note"
        )

        val result = projectStatusService.setEligibilityAssessment(79, inputData)
        assertThat(result.firstStepDecision?.eligibilityAssessment!!.result).isEqualTo(
            ProjectEligibilityAssessmentResult.PASSED
        )
        assertThat(result.projectStatus.status).isEqualTo(ApplicationStatusDTO.SUBMITTED)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        assertThat(event.captured.action).isEqualTo(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
        assertThat(event.captured.project?.id).isEqualTo(79.toString())
        assertThat(event.captured.description).isEqualTo("Project application eligibility assessment concluded as PASSED")
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
        every { userRepository.findByIdOrNull(any()) } returns UserEntity(
            1,
            "programme@email",
            "",
            "",
            UserRoleEntity(7, "programme"),
            "hash_pass"
        )
        every { projectRepository.findById(-22) } returns Optional.empty()

        val data = InputProjectEligibilityAssessment(ProjectEligibilityAssessmentResult.FAILED)
        assertThrows<ResourceNotFoundException> { projectStatusService.setEligibilityAssessment(-22, data) }
    }

}
