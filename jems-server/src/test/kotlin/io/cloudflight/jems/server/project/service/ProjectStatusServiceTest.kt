package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.DisadvantagedGroups
import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ProjectEligibilityAssessmentResult
import io.cloudflight.jems.api.project.dto.status.ProjectQualityAssessmentResult
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
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

//    companion object {
//        const val NOTE_DENIED = "denied"
//        val DRAFT_TIME =
//            ZonedDateTime.of(LocalDate.of(2020, 7, 13), LocalTime.of(12, 0), ZoneId.of("Europe/Bratislava"))
//        val SUBMIT_TIME = DRAFT_TIME.plusDays(1)
//    }
//
//    @MockK
//    lateinit var projectRepository: ProjectRepository
//
//    @MockK
//    lateinit var userRepository: UserRepository
//
//    @RelaxedMockK
//    lateinit var auditService: AuditService
//
//    @MockK
//    lateinit var securityService: SecurityService
//
//    @MockK
//    lateinit var projectStatusHistoryRepository: ProjectStatusHistoryRepository
//
//    lateinit var projectStatusService: ProjectStatusService
//
//    private val user = User(
//            id = 1,
//            email = "applicant@programme.dev",
//            name = "applicant",
//            surname = "",
//            userRole = UserRole(id = 3, name = "applicant user"),
//            password = "hash_pass"
//    )
//
//    private val userApplicant = OutputUserWithRole(
//        user.id,
//        user.email,
//        user.name,
//        user.surname,
//        OutputUserRole(user.userRole.id, user.userRole.name)
//    )
//    private val userProgramme = OutputUserWithRole(16, "programme@email", "", "", OutputUserRole(7, "programme"))
//
//    private val dummyCall = CallEntity(
//        id = 5,
//        creator = user,
//        name = "call",
//        prioritySpecificObjectives = mutableSetOf(ProgrammeSpecificObjectiveEntity(DisadvantagedGroups, "DG")),
//        strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.SeaBasinStrategyArcticOcean, true)),
//        isAdditionalFundAllowed = false,
//        funds = mutableSetOf(),
//        startDate = ZonedDateTime.now().minusDays(2),
//        endDate = ZonedDateTime.now().plusDays(2),
//        status = CallStatus.PUBLISHED,
//        lengthOfPeriod = 1
//    )
//
//    private val projectDraft = createProject(ApplicationStatus.DRAFT)
//    private val projectSubmitted = createProject(ApplicationStatus.SUBMITTED, NOTE_DENIED)
//    private val projectReturned = createProject(ApplicationStatus.RETURNED_TO_APPLICANT)
//    private val projectEligible = createProject(ApplicationStatus.ELIGIBLE)
//    private val projectApprovedWithConditions =
//        createAlreadyApprovedProject(ApplicationStatus.APPROVED_WITH_CONDITIONS)
//
//    @BeforeEach
//    fun setup() {
//        MockKAnnotations.init(this)
//        projectStatusService = ProjectStatusServiceImpl(
//            projectRepository, userRepository, auditService, securityService
//        )
//    }
//
//    @Test
//    fun `project status submitted`() {
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(1) } returns user
////        every { projectRepository.findById(1) } returns Optional.of(projectDraft)
////        every { projectStatusHistoryRepository.save(any<ProjectStatusHistoryEntity>()) } returnsArgument 0
////        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
////
////        val result = projectStatusService.setProjectStatus(
////            1,
////            InputProjectStatus(ApplicationStatusDTO.SUBMITTED, NOTE_DENIED, null)
////        )
////
////        assertThat(result.id).isEqualTo(1)
////        assertThat(result.firstSubmission).isNotNull()
////        assertThat(result.lastResubmission).isNull()
////        assertThat(result.projectStatusDTO.status).isEqualTo(ApplicationStatusDTO.SUBMITTED)
////        assertThat(result.projectStatusDTO.note).isEqualTo(NOTE_DENIED)
//    }
//
//    @Test
//    fun `project status re-submitted to SUBMITTED`() {
////        val ignoreStatuses = setOf(ApplicationStatusDTO.DRAFT, ApplicationStatusDTO.RETURNED_TO_APPLICANT)
////        val previousState = ProjectStatusHistoryEntity(status = ApplicationStatusDTO.SUBMITTED, user = user)
////
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(1) } returns user
////        every { projectRepository.findById(1) } returns Optional.of(projectReturned)
////        every { projectStatusHistoryRepository.save(any<ProjectStatusHistoryEntity>()) } returnsArgument 0
////        every {
////            projectStatusHistoryRepository.findFirstByProjectIdAndStatusNotInOrderByUpdatedDesc(
////                eq(1),
////                eq(ignoreStatuses)
////            )
////        } returns previousState
////        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
////
////        val result =
////            projectStatusService.setProjectStatus(1, InputProjectStatus(ApplicationStatusDTO.SUBMITTED, null, null))
////
////        assertThat(result.id).isEqualTo(1)
////        assertThat(result.firstSubmission).isNotNull()
////        assertThat(result.lastResubmission).isNotNull()
////        assertThat(result.firstSubmission?.updated).isNotEqualTo(result.lastResubmission?.updated)
////        assertThat(result.projectStatusDTO.status).isEqualTo(ApplicationStatusDTO.SUBMITTED)
////        assertThat(result.projectStatusDTO.note).isNull()
//    }
//
//    @Test
//    fun `project status re-submitted to ELIGIBLE`() {
////        val ignoreStatuses = setOf(ApplicationStatusDTO.RETURNED_TO_APPLICANT, ApplicationStatusDTO.DRAFT)
////        val previousState = ProjectStatusHistoryEntity(status = ApplicationStatusDTO.ELIGIBLE, user = user)
////
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(1) } returns user
////        every { projectRepository.findById(1) } returns Optional.of(projectReturned)
////        every { projectStatusHistoryRepository.save(any<ProjectStatusHistoryEntity>()) } returnsArgument 0
////        every {
////            projectStatusHistoryRepository.findFirstByProjectIdAndStatusNotInOrderByUpdatedDesc(
////                eq(1),
////                eq(ignoreStatuses)
////            )
////        } returns previousState
////        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
////
////        val result =
////            projectStatusService.setProjectStatus(1, InputProjectStatus(ApplicationStatusDTO.SUBMITTED, null, null))
////
////        assertThat(result.id).isEqualTo(1)
////        assertThat(result.firstSubmission).isNotNull()
////        assertThat(result.lastResubmission).isNotNull()
////        assertThat(result.firstSubmission?.updated).isNotEqualTo(result.lastResubmission?.updated)
////        assertThat(result.projectStatusDTO.status).isEqualTo(ApplicationStatusDTO.ELIGIBLE)
////        assertThat(result.projectStatusDTO.note).isNull()
//    }
//
//    @Test
//    fun `project status SUBMITTED to ELIGIBLE`() {
////        val eligibilityAssessment = ProjectEligibilityAssessment(
////            id = 10,
////            project = projectSubmitted,
////            result = ProjectEligibilityAssessmentResult.PASSED,
////            user = user
////        )
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(1) } returns user
////        every { projectRepository.findById(1) } returns Optional.of(projectSubmitted.copy(eligibilityAssessment = eligibilityAssessment))
////        every { projectStatusHistoryRepository.save(any<ProjectStatusHistoryEntity>()) } returnsArgument 0
////        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
////
////        val result = projectStatusService.setProjectStatus(
////            projectId = 1,
////            statusChange = InputProjectStatus(
////                ApplicationStatusDTO.ELIGIBLE,
////                "some note",
////                LocalDate.now().plusDays(1)
////            )
////        )
////
////        assertThat(result.id).isEqualTo(1)
////        assertThat(result.eligibilityDecision).isNotNull()
////        assertThat(result.eligibilityDecision?.status).isEqualTo(ApplicationStatusDTO.ELIGIBLE)
////        assertThat(result.projectStatusDTO.status).isEqualTo(ApplicationStatusDTO.ELIGIBLE)
////        assertThat(result.projectStatusDTO.note).isEqualTo("some note")
////        assertThat(result.projectStatusDTO).isEqualTo(result.eligibilityDecision)
//    }
//
//    @Test
//    fun `project status SUBMITTED to ELIGIBLE missing date`() {
////        val eligibilityAssessment = ProjectEligibilityAssessment(
////            id = 10,
////            project = projectSubmitted,
////            result = ProjectEligibilityAssessmentResult.PASSED,
////            user = user
////        )
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(1) } returns user
////        every { projectRepository.findById(1) } returns Optional.of(projectSubmitted.copy(eligibilityAssessment = eligibilityAssessment))
////        every { projectStatusHistoryRepository.save(any<ProjectStatusHistoryEntity>()) } returnsArgument 0
////        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
////
////        val exception = assertThrows<I18nValidationException> {
////            projectStatusService.setProjectStatus(
////                projectId = 1,
////                statusChange = InputProjectStatus(ApplicationStatusDTO.ELIGIBLE, "some note", null)
////            )
////        }
////
////        assertThat(exception.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
////        assertThat(exception.i18nKey).isEqualTo("common.error.field.required")
//    }
//
//    @Test
//    fun `test allowed funding transitions`() {
////        val projectEligibleWithDate = projectEligible
////            .copy(eligibilityDecision = ProjectStatusHistoryEntity(
////                status = ApplicationStatusDTO.ELIGIBLE,
////                decisionDate = LocalDate.now().minusDays(1),
////                user = user
////            ))
////        val allowedTransitions = setOf(
////            projectEligibleWithDate to ApplicationStatusDTO.APPROVED,
////            projectEligibleWithDate to ApplicationStatusDTO.NOT_APPROVED,
////            projectEligibleWithDate to ApplicationStatusDTO.APPROVED_WITH_CONDITIONS
////        )
////
////        allowedTransitions.forEach { testAllowedFundingTransitions(it) }
//    }
//
//    private fun testAllowedFundingTransitions(pair: Pair<ProjectEntity, ApplicationStatusDTO>): Unit {
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(1) } returns user
////        every { projectRepository.findById(1) } returns Optional.of(pair.first)
////        every { projectStatusHistoryRepository.save(any<ProjectStatusHistoryEntity>()) } returnsArgument 0
////        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
////
////        val result = projectStatusService.setProjectStatus(
////            projectId = 1,
////            statusChange = InputProjectStatus(pair.second, "some note", LocalDate.now().plusDays(1))
////        )
////
////        assertThat(result.id).isEqualTo(1)
////        assertThat(result.projectStatusDTO.status).isEqualTo(pair.second)
////        assertThat(result.projectStatusDTO.note).isEqualTo("some note")
////        assertThat(result.projectStatusDTO).isEqualTo(result.fundingDecision)
//    }
//
//    @Test
//    fun `funding decision before eligibility decision`() {
////        val projectEligibleWithDate = projectEligible
////            .copy(eligibilityDecision = ProjectStatusHistoryEntity(
////                status = ApplicationStatusDTO.ELIGIBLE,
////                decisionDate = LocalDate.now().minusDays(1),
////                user = user
////            ))
////
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(1) } returns user
////        every { projectRepository.findById(1) } returns Optional.of(projectEligibleWithDate)
////        every { projectStatusHistoryRepository.save(any<ProjectStatusHistoryEntity>()) } returnsArgument 0
////        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
////
////        val exception = assertThrows<I18nValidationException> {
////            projectStatusService.setProjectStatus(
////                projectId = 1,
////                statusChange = InputProjectStatus(
////                    ApplicationStatusDTO.APPROVED,
////                    "some note",
////                    LocalDate.now().minusDays(2))
////            )
////        }
////        assertThat(exception.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
////        assertThat(exception.i18nKey).isEqualTo("project.funding.decision.is.before.eligibility.decision")
//    }
//
//    @Test
//    fun `project status setting failed successfully`() {
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(1) } returns user
////        every { projectRepository.findById(2) } returns Optional.empty()
////        assertThrows<ResourceNotFoundException> {
////            projectStatusService.setProjectStatus(2, InputProjectStatus(ApplicationStatusDTO.SUBMITTED, null, null))
////        }
//    }
//
//    private fun createProject(status: ApplicationStatus, note: String? = null): ProjectEntity {
//        var submitTime: ZonedDateTime?
//        var statusTime: ZonedDateTime
//        if (status == ApplicationStatus.DRAFT) {
//            submitTime = null
//            statusTime = DRAFT_TIME
//        } else if (status == ApplicationStatus.SUBMITTED) {
//            submitTime = SUBMIT_TIME
//            statusTime = SUBMIT_TIME
//        } else { // status RETURNED_TO_APPLICANT
//            submitTime = SUBMIT_TIME
//            statusTime = SUBMIT_TIME.plusDays(1)
//        }
//        return ProjectEntity(
//            id = 1,
//            call = dummyCall,
//            acronym = "acronym",
//            applicant = user,
//            currentStatus = ProjectStatusHistoryEntity(1, null, status, user, statusTime, null, note),
//            firstSubmission = if (submitTime != null) ProjectStatusHistoryEntity(
//                2,
//                null,
//                ApplicationStatus.SUBMITTED,
//                user,
//                submitTime,
//                null,
//                note
//            ) else null
//        )
//    }
//
//    private fun createAlreadyApprovedProject(appStatus: ApplicationStatus): ProjectEntity {
//        val alreadyApprovedStatuses = setOf(
//            ApplicationStatusDTO.APPROVED,
//            ApplicationStatusDTO.APPROVED_WITH_CONDITIONS,
//            ApplicationStatusDTO.NOT_APPROVED
//        )
//        if (!alreadyApprovedStatuses.contains(appStatus))
//            throw IllegalStateException()
//
//        val status = ProjectStatusHistoryEntity(1, null, appStatus, user, ZonedDateTime.now(), null, null)
//        return ProjectEntity(
//            id = 1,
//            call = dummyCall,
//            acronym = "acronym",
//            applicant = user,
//            currentStatus = status,
//            fundingDecision = status
//        )
//    }
//
////    @Test
////    fun `set quality assessment`() {
////        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(any()) } returns User(
////            1,
////            "programme@email",
////            "",
////            "",
////            UserRole(7, "programme"),
////            "hash_pass"
////        )
////        every { projectRepository.findById(16) } returns Optional.of(projectSubmitted.copy(id = 16))
////        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
////
////        val inputData = InputProjectQualityAssessment(
////            result = ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING,
////            note = "example note"
////        )
////
////        val result = projectStatusService.setQualityAssessment(16, inputData)
////        assertThat(result.qualityAssessment!!.result).isEqualTo(ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING)
////        assertThat(result.projectStatus.status).isEqualTo(ApplicationStatusDTO.SUBMITTED)
////
////        val event = slot<AuditCandidate>()
////        verify { auditService.logEvent(capture(event)) }
////        assertThat(event.captured.action).isEqualTo(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
////        assertThat(event.captured.project?.id).isEqualTo(16.toString())
////        assertThat(event.captured.description).isEqualTo("Project application quality assessment concluded as RECOMMENDED_FOR_FUNDING")
////
////    }
////
////    @Test
////    fun `set QA no user`() {
////        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(eq(userProgramme.id!!)) } returns null
////
////        val data = InputProjectQualityAssessment(ProjectQualityAssessmentResult.NOT_RECOMMENDED)
////        assertThrows<ResourceNotFoundException> { projectStatusService.setQualityAssessment(-9, data) }
////    }
////
////    @Test
////    fun `set QA no project`() {
////        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(any()) } returns User(
////                1,
////                "programme@email",
////                "",
////                "",
////            UserRole(7, "programme"),
////                "hash_pass"
////        )
////        every { projectRepository.findById(-51) } returns Optional.empty()
////
////        val data = InputProjectQualityAssessment(ProjectQualityAssessmentResult.RECOMMENDED_WITH_CONDITIONS)
////        assertThrows<ResourceNotFoundException> { projectStatusService.setQualityAssessment(-51, data) }
////    }
////
////    @Test
////    fun `set eligibility assessment`() {
////        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(any()) } returns User(
////                1,
////                "programme@email",
////                "",
////                "",
////            UserRole(7, "programme"),
////                "hash_pass"
////        )
////        every { projectRepository.findById(79) } returns Optional.of(projectSubmitted.copy(id = 79))
////        every { projectRepository.save(any<ProjectEntity>()) } returnsArgument 0
////
////        val inputData = InputProjectEligibilityAssessment(
////            result = ProjectEligibilityAssessmentResult.PASSED,
////            note = "example note"
////        )
////
////        val result = projectStatusService.setEligibilityAssessment(79, inputData)
////        assertThat(result.eligibilityAssessment!!.result).isEqualTo(ProjectEligibilityAssessmentResult.PASSED)
////        assertThat(result.projectStatus.status).isEqualTo(ApplicationStatusDTO.SUBMITTED)
////
////        val event = slot<AuditCandidate>()
////        verify { auditService.logEvent(capture(event)) }
////        assertThat(event.captured.action).isEqualTo(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
////        assertThat(event.captured.project?.id).isEqualTo(79.toString())
////        assertThat(event.captured.description).isEqualTo("Project application eligibility assessment concluded as PASSED")
////    }
////
////    @Test
////    fun `set EA no user`() {
////        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(eq(userProgramme.id!!)) } returns null
////
////        val data = InputProjectEligibilityAssessment(ProjectEligibilityAssessmentResult.PASSED)
////        assertThrows<ResourceNotFoundException> { projectStatusService.setEligibilityAssessment(-3, data) }
////    }
////
////    @Test
////    fun `set EA no project`() {
////        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
////        every { userRepository.findByIdOrNull(any()) } returns User(
////            1,
////            "programme@email",
////            "",
////            "",
////            UserRole(7, "programme"),
////            "hash_pass"
////        )
////        every { projectRepository.findById(-22) } returns Optional.empty()
////
////        val data = InputProjectEligibilityAssessment(ProjectEligibilityAssessmentResult.FAILED)
////        assertThrows<ResourceNotFoundException> { projectStatusService.setEligibilityAssessment(-22, data) }
////    }
//
//    private val statusSubmitted = ProjectStatusHistoryEntity(status = ApplicationStatus.SUBMITTED, id = 10, user = user)
//    private val statusEligible = ProjectStatusHistoryEntity(status = ApplicationStatus.ELIGIBLE, id = 20, user = user)
//    private val statusIneligible = ProjectStatusHistoryEntity(status = ApplicationStatus.INELIGIBLE, id = 21, user = user)
//    private val statusApproved = ProjectStatusHistoryEntity(status = ApplicationStatus.APPROVED, id = 30, user = user)
//    private val statusApprovedWithConditions = ProjectStatusHistoryEntity(status = ApplicationStatus.APPROVED_WITH_CONDITIONS, id = 31, user = user)
//    private val statusNotApproved = ProjectStatusHistoryEntity(status = ApplicationStatus.NOT_APPROVED, id = 32, user = user)
//
//    @Test
//    fun `can find funding reversion if possible`() {
////        val projectId = 15L
////        listOf(
////            // deletion of ELIGIBILITY decision:
////            listOf(statusEligible, statusSubmitted),
////            listOf(statusIneligible, statusSubmitted),
////            // deletion of FUNDING decision:
////            listOf(statusApproved, statusApprovedWithConditions),
////            listOf(statusNotApproved, statusApprovedWithConditions),
////            listOf(statusApproved, statusEligible),
////            listOf(statusNotApproved, statusEligible),
////            listOf(statusApprovedWithConditions, statusEligible)
////        ).forEach {
////            every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns it
////            val message = "Decision-Reversion from ${it[0].status} back to ${it[1].status} should be possible"
////
////            val result: OutputRevertProjectStatus?
////            try {
////                result = projectStatusService.findPossibleDecisionRevertStatusOutput(projectId)
////            } catch (e: Exception) {
////                fail(message, e)
////            }
////
////            assertThat(result).isNotNull
////            assertThat(listOf(result!!.from.status, result.to.status))
////                .overridingErrorMessage(message)
////                .containsExactlyElementsOf(it.stream().map { outputStatus -> outputStatus.status }.collect(Collectors.toList()))
////        }
//    }
//
//    @Test
//    fun `cannot find funding reversion statuses`() {
////        val projectId = 15L
////
////        every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns listOf(statusEligible)
////        assertThat(projectStatusService.findPossibleDecisionRevertStatusOutput(projectId))
////            .overridingErrorMessage("When statuses cannot be found, there should be no possibility to revert")
////            .isNull()
//    }
//
//    @Test
//    fun `cannot find funding reversion for wrong statuses`() {
////        val projectId = 15L
////
////        every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns listOf(statusEligible, statusIneligible)
////        assertThat(projectStatusService.findPossibleDecisionRevertStatusOutput(projectId))
////            .overridingErrorMessage("When statuses are not allowed to be reverted, there should be no possibility returned")
////            .isNull()
//    }
//
//    @Test
//    fun `cannot revert when wrong statuses specified in request, although possible`() {
////        val projectId = 16L
////        every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns
////            listOf(statusApproved, statusEligible)
////
////        val revertRequest = InputRevertProjectStatus(
////            projectStatusFromId = statusApproved.id,
////            projectStatusToId = statusIneligible.id
////        )
////        val exception = assertThrows<I18nValidationException>(
////            "Statuses provided differ from those possible, so it should throw an exception"
////        ) { projectStatusService.revertLastDecision(projectId, revertRequest) }
////        assertThat(exception.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
////        assertThat(exception.i18nKey).isEqualTo("project.decision.revert.not.possible")
//    }
//
//    @Test
//    fun `cannot revert when project not exists`() {
////        val projectId = 17L
////        every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns
////            listOf(statusApproved, statusEligible)
////        every { projectRepository.findById(eq(projectId)) } returns Optional.empty()
////
////        val revertRequest = InputRevertProjectStatus(
////            projectStatusFromId = statusApproved.id,
////            projectStatusToId = statusEligible.id
////        )
////        val exception = assertThrows<ResourceNotFoundException> {
////            projectStatusService.revertLastDecision(projectId, revertRequest)
////        }
////        assertThat(exception.entity).isEqualTo("project")
//    }
//
//    @Test
//    fun `can revert Eligibility decision`() {
////        val projectId = 18L
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        val projectCapture = slot<ProjectEntity>()
////        every { projectRepository.save(capture(projectCapture)) } returnsArgument 0
////        every { projectStatusHistoryRepository.delete(any<ProjectStatusHistoryEntity>()) } answers { }
////
////        listOf(
////            listOf(statusEligible, statusSubmitted),
////            listOf(statusIneligible, statusSubmitted)
////        ).forEach {
////            every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns it
////            every { projectRepository.findById(eq(projectId)) } returns Optional.of(
////                projectEligible.copy(
////                    eligibilityDecision = it[0]
////                ))
////
////            val revertRequest = InputRevertProjectStatus(projectStatusFromId = it[0].id, projectStatusToId = it[1].id)
////            projectStatusService.revertLastDecision(projectId, revertRequest)
////
////            verify {
////                projectStatusHistoryRepository.delete(it[0])
////            }
////            with (projectCapture.captured) {
////                assertThat(eligibilityDecision).isNull()
////                assertThat(currentStatus).isEqualTo(it[1])
////            }
////        }
//    }
//
//    @Test
//    fun `can revert funding to APPROVED_WITH_CONDITIONS decision`() {
////        val projectId = 19L
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        val projectCapture = slot<ProjectEntity>()
////        every { projectRepository.save(capture(projectCapture)) } returnsArgument 0
////        every { projectStatusHistoryRepository.delete(any<ProjectStatusHistoryEntity>()) } answers { }
////
////        listOf(
////            listOf(statusApproved, statusApprovedWithConditions),
////            listOf(statusNotApproved, statusApprovedWithConditions)
////        ).forEach {
////            every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns it
////            every { projectRepository.findById(eq(projectId)) } returns
////                Optional.of(createAlreadyApprovedProject(it[0].status))
////
////            val revertRequest = InputRevertProjectStatus(projectStatusFromId = it[0].id, projectStatusToId = it[1].id)
////            projectStatusService.revertLastDecision(projectId, revertRequest)
////
////            verify {
////                projectStatusHistoryRepository.delete(it[0])
////            }
////            with (projectCapture.captured) {
////                assertThat(fundingDecision).isEqualTo(it[1])
////                assertThat(currentStatus).isEqualTo(it[1])
////            }
////        }
//    }
//
//    @Test
//    fun `can revert funding to ELIGIBLE decision`() {
////        val projectId = 20L
////        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
////        val projectCapture = slot<ProjectEntity>()
////        every { projectRepository.save(capture(projectCapture)) } returnsArgument 0
////        every { projectStatusHistoryRepository.delete(any<ProjectStatusHistoryEntity>()) } answers { }
////
////        listOf(
////            listOf(statusApproved, statusEligible),
////            listOf(statusNotApproved, statusEligible),
////            listOf(statusApprovedWithConditions, statusEligible)
////        ).forEach {
////            every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(eq(projectId)) } returns it
////            every { projectRepository.findById(eq(projectId)) } returns
////                Optional.of(createAlreadyApprovedProject(it[0].status))
////
////            val revertRequest = InputRevertProjectStatus(projectStatusFromId = it[0].id, projectStatusToId = it[1].id)
////            projectStatusService.revertLastDecision(projectId, revertRequest)
////
////            verify {
////                projectStatusHistoryRepository.delete(it[0])
////            }
////            with (projectCapture.captured) {
////                assertThat(fundingDecision).isNull()
////                assertThat(currentStatus).isEqualTo(it[1])
////            }
////        }
//    }

}
