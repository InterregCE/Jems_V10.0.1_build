package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.ems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.ems.api.project.dto.InputProjectStatus
import io.cloudflight.ems.api.project.dto.InputRevertProjectStatus
import io.cloudflight.ems.api.project.dto.status.OutputRevertProjectStatus
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.api.project.dto.status.ProjectEligibilityAssessmentResult
import io.cloudflight.ems.api.project.dto.status.ProjectQualityAssessmentResult
import io.cloudflight.ems.api.user.dto.OutputUserRole
import io.cloudflight.ems.api.user.dto.OutputUserWithRole
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy.DisadvantagedGroups
import io.cloudflight.ems.api.strategy.ProgrammeStrategy
import io.cloudflight.ems.audit.entity.AuditAction
import io.cloudflight.ems.audit.service.AuditCandidate
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.project.entity.Project
import io.cloudflight.ems.project.entity.ProjectEligibilityAssessment
import io.cloudflight.ems.project.entity.ProjectStatus
import io.cloudflight.ems.user.entity.User
import io.cloudflight.ems.user.entity.UserRole
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.ems.project.repository.ProjectRepository
import io.cloudflight.ems.project.repository.ProjectStatusRepository
import io.cloudflight.ems.user.repository.UserRepository
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.audit.service.AuditService
import io.cloudflight.ems.strategy.entity.Strategy
import io.cloudflight.ems.workpackage.service.WorkPackageService
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.stream.Collectors

internal class ProjectStatusServiceTest {

    companion object {
        const val NOTE_DENIED = "denied"
        val DRAFT_TIME =
            ZonedDateTime.of(LocalDate.of(2020, 7, 13), LocalTime.of(12, 0), ZoneId.of("Europe/Bratislava"))
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

    @RelaxedMockK
    lateinit var projectPartnerService: ProjectPartnerService

    @RelaxedMockK
    lateinit var projectWorkPackageService: WorkPackageService

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

    private val userApplicant = OutputUserWithRole(
        user.id,
        user.email,
        user.name,
        user.surname,
        OutputUserRole(user.userRole.id, user.userRole.name)
    )
    private val userProgramme = OutputUserWithRole(16, "programme@email", "", "", OutputUserRole(7, "programme"))

    private val dummyCall = Call(
        id = 5,
        creator = user,
        name = "call",
        priorityPolicies = setOf(ProgrammePriorityPolicy(DisadvantagedGroups, "DG")),
        strategies = setOf(Strategy(ProgrammeStrategy.SeaBasinStrategyArcticOcean, true)),
        startDate = ZonedDateTime.now().minusDays(2),
        endDate = ZonedDateTime.now().plusDays(2),
        status = CallStatus.PUBLISHED,
        lengthOfPeriod = 1
    )

    private val projectDraft = createProject(ProjectApplicationStatus.DRAFT)
    private val projectSubmitted = createProject(ProjectApplicationStatus.SUBMITTED, NOTE_DENIED)
    private val projectReturned = createProject(ProjectApplicationStatus.RETURNED_TO_APPLICANT)
    private val projectEligible = createProject(ProjectApplicationStatus.ELIGIBLE)
    private val projectApprovedWithConditions =
        createAlreadyApprovedProject(ProjectApplicationStatus.APPROVED_WITH_CONDITIONS)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectStatusService = ProjectStatusServiceImpl(
            projectRepository, projectStatusRepository, userRepository, auditService, securityService, projectPartnerService, projectWorkPackageService
        )
    }

    @Test
    fun `project status submitted`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findById(1) } returns Optional.of(projectDraft)
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectPartnerService.updateSortByRole(1) } just Runs
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result = projectStatusService.setProjectStatus(
            1,
            InputProjectStatus(ProjectApplicationStatus.SUBMITTED, NOTE_DENIED, null)
        )

        assertThat(result.id).isEqualTo(1)
        assertThat(result.firstSubmission).isNotNull()
        assertThat(result.lastResubmission).isNull()
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)
        assertThat(result.projectStatus.note).isEqualTo(NOTE_DENIED)

        verify {
            projectPartnerService.updateSortByRole(1)
            projectWorkPackageService.updateSortOnNumber(1)
        }
    }

    @Test
    fun `project status re-submitted to SUBMITTED`() {
        val ignoreStatuses = setOf(ProjectApplicationStatus.DRAFT, ProjectApplicationStatus.RETURNED_TO_APPLICANT)
        val previousState = ProjectStatus(status = ProjectApplicationStatus.SUBMITTED, user = user)

        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findById(1) } returns Optional.of(projectReturned)
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every {
            projectStatusRepository.findFirstByProjectIdAndStatusNotInOrderByUpdatedDesc(
                eq(1),
                eq(ignoreStatuses)
            )
        } returns previousState
        every { projectPartnerService.updateSortByRole(1) } just Runs
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result =
            projectStatusService.setProjectStatus(1, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, null, null))

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
        every { projectRepository.findById(1) } returns Optional.of(projectReturned)
        every { projectPartnerService.updateSortByRole(1) } just Runs
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every {
            projectStatusRepository.findFirstByProjectIdAndStatusNotInOrderByUpdatedDesc(
                eq(1),
                eq(ignoreStatuses)
            )
        } returns previousState
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result =
            projectStatusService.setProjectStatus(1, InputProjectStatus(ProjectApplicationStatus.SUBMITTED, null, null))

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
        every { projectRepository.findById(1) } returns Optional.of(projectSubmitted.copy(eligibilityAssessment = eligibilityAssessment))
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result = projectStatusService.setProjectStatus(
            projectId = 1,
            statusChange = InputProjectStatus(
                ProjectApplicationStatus.ELIGIBLE,
                "some note",
                LocalDate.now().plusDays(1)
            )
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
        every { projectRepository.findById(1) } returns Optional.of(projectSubmitted.copy(eligibilityAssessment = eligibilityAssessment))
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
    fun `test allowed funding transitions`() {
        val projectEligibleWithDate = projectEligible
            .copy(eligibilityDecision = ProjectStatus(
                status = ProjectApplicationStatus.ELIGIBLE,
                decisionDate = LocalDate.now().minusDays(1),
                user = user
            ))
        val allowedTransitions = setOf(
            projectEligibleWithDate to ProjectApplicationStatus.APPROVED,
            projectEligibleWithDate to ProjectApplicationStatus.NOT_APPROVED,
            projectEligibleWithDate to ProjectApplicationStatus.APPROVED_WITH_CONDITIONS
        )

        allowedTransitions.forEach { testAllowedFundingTransitions(it) }
    }

    private fun testAllowedFundingTransitions(pair: Pair<Project, ProjectApplicationStatus>): Unit {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findById(1) } returns Optional.of(pair.first)
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val result = projectStatusService.setProjectStatus(
            projectId = 1,
            statusChange = InputProjectStatus(pair.second, "some note", LocalDate.now().plusDays(1))
        )

        assertThat(result.id).isEqualTo(1)
        assertThat(result.projectStatus.status).isEqualTo(pair.second)
        assertThat(result.projectStatus.note).isEqualTo("some note")
        assertThat(result.projectStatus).isEqualTo(result.fundingDecision)
    }

    @Test
    fun `funding decision before eligibility decision`() {
        val projectEligibleWithDate = projectEligible
            .copy(eligibilityDecision = ProjectStatus(
                status = ProjectApplicationStatus.ELIGIBLE,
                decisionDate = LocalDate.now().minusDays(1),
                user = user
            ))

        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findById(1) } returns Optional.of(projectEligibleWithDate)
        every { projectStatusRepository.save(any<ProjectStatus>()) } returnsArgument 0
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val exception = assertThrows<I18nValidationException> {
            projectStatusService.setProjectStatus(
                projectId = 1,
                statusChange = InputProjectStatus(
                    ProjectApplicationStatus.APPROVED,
                    "some note",
                    LocalDate.now().minusDays(2))
            )
        }
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(exception.i18nKey).isEqualTo("project.funding.decision.is.before.eligibility.decision")
    }

    @Test
    fun `project status setting failed successfully`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(1) } returns user
        every { projectRepository.findById(2) } returns Optional.empty()
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
            call = dummyCall,
            acronym = "acronym",
            applicant = user,
            projectStatus = ProjectStatus(1, null, status, user, statusTime, null, note),
            firstSubmission = if (submitTime != null) ProjectStatus(
                2,
                null,
                ProjectApplicationStatus.SUBMITTED,
                user,
                submitTime,
                null,
                note
            ) else null
        )
    }

    private fun createAlreadyApprovedProject(appStatus: ProjectApplicationStatus): Project {
        val alreadyApprovedStatuses = setOf(
            ProjectApplicationStatus.APPROVED,
            ProjectApplicationStatus.APPROVED_WITH_CONDITIONS,
            ProjectApplicationStatus.NOT_APPROVED
        )
        if (!alreadyApprovedStatuses.contains(appStatus))
            throw IllegalStateException()

        val status = ProjectStatus(1, null, appStatus, user, ZonedDateTime.now(), null, null)
        return Project(
            id = 1,
            call = dummyCall,
            acronym = "acronym",
            applicant = user,
            projectStatus = status,
            fundingDecision = status
        )
    }

    @Test
    fun `set quality assessment`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(any()) } returns User(
            1,
            "programme@email",
            "",
            "",
            UserRole(7, "programme"),
            "hash_pass"
        )
        every { projectRepository.findById(16) } returns Optional.of(projectSubmitted.copy(id = 16))
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val inputData = InputProjectQualityAssessment(
            result = ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING,
            note = "example note"
        )

        val result = projectStatusService.setQualityAssessment(16, inputData)
        assertThat(result.qualityAssessment!!.result).isEqualTo(ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        assertThat(event.captured.action).isEqualTo(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
        assertThat(event.captured.projectId).isEqualTo(16.toString())
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
        every { userRepository.findByIdOrNull(any()) } returns User(
                1,
                "programme@email",
                "",
                "",
            UserRole(7, "programme"),
                "hash_pass"
        )
        every { projectRepository.findById(-51) } returns Optional.empty()

        val data = InputProjectQualityAssessment(ProjectQualityAssessmentResult.RECOMMENDED_WITH_CONDITIONS)
        assertThrows<ResourceNotFoundException> { projectStatusService.setQualityAssessment(-51, data) }
    }

    @Test
    fun `set eligibility assessment`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        every { userRepository.findByIdOrNull(any()) } returns User(
                1,
                "programme@email",
                "",
                "",
            UserRole(7, "programme"),
                "hash_pass"
        )
        every { projectRepository.findById(79) } returns Optional.of(projectSubmitted.copy(id = 79))
        every { projectRepository.save(any<Project>()) } returnsArgument 0

        val inputData = InputProjectEligibilityAssessment(
            result = ProjectEligibilityAssessmentResult.PASSED,
            note = "example note"
        )

        val result = projectStatusService.setEligibilityAssessment(79, inputData)
        assertThat(result.eligibilityAssessment!!.result).isEqualTo(ProjectEligibilityAssessmentResult.PASSED)
        assertThat(result.projectStatus.status).isEqualTo(ProjectApplicationStatus.SUBMITTED)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        assertThat(event.captured.action).isEqualTo(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
        assertThat(event.captured.projectId).isEqualTo(79.toString())
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
        every { userRepository.findByIdOrNull(any()) } returns User(
            1,
            "programme@email",
            "",
            "",
            UserRole(7, "programme"),
            "hash_pass"
        )
        every { projectRepository.findById(-22) } returns Optional.empty()

        val data = InputProjectEligibilityAssessment(ProjectEligibilityAssessmentResult.FAILED)
        assertThrows<ResourceNotFoundException> { projectStatusService.setEligibilityAssessment(-22, data) }
    }

    private val statusSubmitted = ProjectStatus(status = ProjectApplicationStatus.SUBMITTED, id = 10, user = user)
    private val statusEligible = ProjectStatus(status = ProjectApplicationStatus.ELIGIBLE, id = 20, user = user)
    private val statusIneligible = ProjectStatus(status = ProjectApplicationStatus.INELIGIBLE, id = 21, user = user)
    private val statusApproved = ProjectStatus(status = ProjectApplicationStatus.APPROVED, id = 30, user = user)
    private val statusApprovedWithConditions = ProjectStatus(status = ProjectApplicationStatus.APPROVED_WITH_CONDITIONS, id = 31, user = user)
    private val statusNotApproved = ProjectStatus(status = ProjectApplicationStatus.NOT_APPROVED, id = 32, user = user)

    @Test
    fun `can find funding reversion if possible`() {
        val projectId = 15L
        listOf(
            // deletion of ELIGIBILITY decision:
            listOf(statusEligible, statusSubmitted),
            listOf(statusIneligible, statusSubmitted),
            // deletion of FUNDING decision:
            listOf(statusApproved, statusApprovedWithConditions),
            listOf(statusNotApproved, statusApprovedWithConditions),
            listOf(statusApproved, statusEligible),
            listOf(statusNotApproved, statusEligible),
            listOf(statusApprovedWithConditions, statusEligible)
        ).forEach {
            every { projectStatusRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns it
            val message = "Decision-Reversion from ${it[0].status} back to ${it[1].status} should be possible"

            val result: OutputRevertProjectStatus?
            try {
                result = projectStatusService.findPossibleDecisionRevertStatusOutput(projectId)
            } catch (e: Exception) {
                fail(message, e)
            }

            assertThat(result).isNotNull
            assertThat(listOf(result!!.from.status, result.to.status))
                .overridingErrorMessage(message)
                .containsExactlyElementsOf(it.stream().map { outputStatus -> outputStatus.status }.collect(Collectors.toList()))
        }
    }

    @Test
    fun `cannot find funding reversion statuses`() {
        val projectId = 15L

        every { projectStatusRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns listOf(statusEligible)
        assertThat(projectStatusService.findPossibleDecisionRevertStatusOutput(projectId))
            .overridingErrorMessage("When statuses cannot be found, there should be no possibility to revert")
            .isNull()
    }

    @Test
    fun `cannot find funding reversion for wrong statuses`() {
        val projectId = 15L

        every { projectStatusRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns listOf(statusEligible, statusIneligible)
        assertThat(projectStatusService.findPossibleDecisionRevertStatusOutput(projectId))
            .overridingErrorMessage("When statuses are not allowed to be reverted, there should be no possibility returned")
            .isNull()
    }

    @Test
    fun `cannot revert when wrong statuses specified in request, although possible`() {
        val projectId = 16L
        every { projectStatusRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns
            listOf(statusApproved, statusEligible)

        val revertRequest = InputRevertProjectStatus(
            projectStatusFromId = statusApproved.id,
            projectStatusToId = statusIneligible.id
        )
        val exception = assertThrows<I18nValidationException>(
            "Statuses provided differ from those possible, so it should throw an exception"
        ) { projectStatusService.revertLastDecision(projectId, revertRequest) }
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(exception.i18nKey).isEqualTo("project.decision.revert.not.possible")
    }

    @Test
    fun `cannot revert when project not exists`() {
        val projectId = 17L
        every { projectStatusRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns
            listOf(statusApproved, statusEligible)
        every { projectRepository.findById(eq(projectId)) } returns Optional.empty()

        val revertRequest = InputRevertProjectStatus(
            projectStatusFromId = statusApproved.id,
            projectStatusToId = statusEligible.id
        )
        val exception = assertThrows<ResourceNotFoundException> {
            projectStatusService.revertLastDecision(projectId, revertRequest)
        }
        assertThat(exception.entity).isEqualTo("project")
    }

    @Test
    fun `can revert Eligibility decision`() {
        val projectId = 18L
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        val projectCapture = slot<Project>()
        every { projectRepository.save(capture(projectCapture)) } returnsArgument 0
        every { projectStatusRepository.delete(any<ProjectStatus>()) } answers { }

        listOf(
            listOf(statusEligible, statusSubmitted),
            listOf(statusIneligible, statusSubmitted)
        ).forEach {
            every { projectStatusRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns it
            every { projectRepository.findById(eq(projectId)) } returns Optional.of(
                projectEligible.copy(
                    eligibilityDecision = it[0]
                ))

            val revertRequest = InputRevertProjectStatus(projectStatusFromId = it[0].id, projectStatusToId = it[1].id)
            projectStatusService.revertLastDecision(projectId, revertRequest)

            verify {
                projectStatusRepository.delete(it[0])
            }
            with (projectCapture.captured) {
                assertThat(eligibilityDecision).isNull()
                assertThat(projectStatus).isEqualTo(it[1])
            }
        }
    }

    @Test
    fun `can revert funding to APPROVED_WITH_CONDITIONS decision`() {
        val projectId = 19L
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        val projectCapture = slot<Project>()
        every { projectRepository.save(capture(projectCapture)) } returnsArgument 0
        every { projectStatusRepository.delete(any<ProjectStatus>()) } answers { }

        listOf(
            listOf(statusApproved, statusApprovedWithConditions),
            listOf(statusNotApproved, statusApprovedWithConditions)
        ).forEach {
            every { projectStatusRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns it
            every { projectRepository.findById(eq(projectId)) } returns
                Optional.of(createAlreadyApprovedProject(it[0].status))

            val revertRequest = InputRevertProjectStatus(projectStatusFromId = it[0].id, projectStatusToId = it[1].id)
            projectStatusService.revertLastDecision(projectId, revertRequest)

            verify {
                projectStatusRepository.delete(it[0])
            }
            with (projectCapture.captured) {
                assertThat(fundingDecision).isEqualTo(it[1])
                assertThat(projectStatus).isEqualTo(it[1])
            }
        }
    }

    @Test
    fun `can revert funding to ELIGIBLE decision`() {
        val projectId = 20L
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        val projectCapture = slot<Project>()
        every { projectRepository.save(capture(projectCapture)) } returnsArgument 0
        every { projectStatusRepository.delete(any<ProjectStatus>()) } answers { }

        listOf(
            listOf(statusApproved, statusEligible),
            listOf(statusNotApproved, statusEligible),
            listOf(statusApprovedWithConditions, statusEligible)
        ).forEach {
            every { projectStatusRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns it
            every { projectRepository.findById(eq(projectId)) } returns
                Optional.of(createAlreadyApprovedProject(it[0].status))

            val revertRequest = InputRevertProjectStatus(projectStatusFromId = it[0].id, projectStatusToId = it[1].id)
            projectStatusService.revertLastDecision(projectId, revertRequest)

            verify {
                projectStatusRepository.delete(it[0])
            }
            with (projectCapture.captured) {
                assertThat(fundingDecision).isNull()
                assertThat(projectStatus).isEqualTo(it[1])
            }
        }
    }

}
