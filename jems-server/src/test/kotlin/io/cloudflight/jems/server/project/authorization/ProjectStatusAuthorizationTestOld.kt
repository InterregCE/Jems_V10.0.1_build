package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.provider.Arguments
import java.time.ZonedDateTime
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ProjectStatusAuthorizationTestOld {


    companion object {
        const val PID_DRAFT: Long = 1
        const val PID_SUBMITTED: Long = 2L
        const val PID_RETURNED: Long = 3L
        const val PID_SUBMITTED_WITH_EA: Long = 21L
        const val PID_ELIGIBLE: Long = 4L
        const val PID_INELIGIBLE: Long = 5L
        const val PID_ELIGIBLE_WITH_QA: Long = 22L
        const val PID_NOT_APPROVED: Long = 6L
        const val PID_APPROVED_WITH_CONDITIONS: Long = 7L
        const val PID_APPROVED: Long = 8L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectService: ProjectService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    lateinit var projectStatusAuthorization: ProjectStatusAuthorization

    private val userApplicantWithoutRole = UserSummary(
        id = applicantUser.user.id,
        email = applicantUser.user.email,
        name = applicantUser.user.name,
        surname = applicantUser.user.surname,
        userRole = UserRoleSummary(id = 3L, name = "applicant"),
    )

    private fun eligibilityAssessment(projectId: Long, step: Int) = ProjectAssessmentEligibility(
        projectId = projectId,
        step = step,
        result = ProjectAssessmentEligibilityResult.PASSED,
        updated = ZonedDateTime.now()
    )
    private fun qualityAssessment(projectId: Long, step: Int) = ProjectAssessmentQuality(
        projectId = projectId,
        step = step,
        result = ProjectAssessmentQualityResult.RECOMMENDED_FOR_FUNDING,
        updated = ZonedDateTime.now()
    )

    private val projectDraft = createProject(PID_DRAFT, ApplicationStatus.DRAFT)
    private val projectSubmitted = createProject(PID_SUBMITTED, ApplicationStatus.SUBMITTED)
    private val projectReturned = createProject(PID_RETURNED, ApplicationStatus.RETURNED_TO_APPLICANT)
    private val projectSubmittedWithEa = projectSubmitted.copy(
        id = PID_SUBMITTED_WITH_EA,
        assessmentStep2 = ProjectAssessment(assessmentEligibility = eligibilityAssessment(PID_SUBMITTED_WITH_EA, 1))
    )
    private val projectEligible = createProject(PID_ELIGIBLE, ApplicationStatus.ELIGIBLE).copy(
        assessmentStep2 = ProjectAssessment(assessmentEligibility = eligibilityAssessment(PID_ELIGIBLE, 1))
    )
    private val projectIneligible = createProject(PID_INELIGIBLE, ApplicationStatus.INELIGIBLE).copy(
        assessmentStep2 = ProjectAssessment(assessmentEligibility = eligibilityAssessment(PID_INELIGIBLE, 1))
    )
    private val projectEligibleWithQA = createProject(PID_ELIGIBLE_WITH_QA, ApplicationStatus.ELIGIBLE).copy(
        assessmentStep2 = ProjectAssessment(assessmentQuality = qualityAssessment(PID_ELIGIBLE_WITH_QA, 1))
    )
    private val projectNotApproved = createProject(PID_NOT_APPROVED, ApplicationStatus.NOT_APPROVED)
    private val projectApprovedWithConditions =
        createProject(PID_APPROVED_WITH_CONDITIONS, ApplicationStatus.APPROVED_WITH_CONDITIONS).copy(
            assessmentStep2 = ProjectAssessment(assessmentQuality = qualityAssessment(PID_APPROVED_WITH_CONDITIONS, 1))
        )
    private val projectApproved = createProject(PID_APPROVED, ApplicationStatus.APPROVED)

    private val projects = mapOf(
        PID_DRAFT to projectDraft,
        PID_SUBMITTED to projectSubmitted,
        PID_SUBMITTED_WITH_EA to projectSubmittedWithEa,
        PID_RETURNED to projectReturned,
        PID_ELIGIBLE to projectEligible,
        PID_INELIGIBLE to projectIneligible,
        PID_ELIGIBLE_WITH_QA to projectEligibleWithQA,
        PID_NOT_APPROVED to projectNotApproved,
        PID_APPROVED to projectApproved,
        PID_APPROVED_WITH_CONDITIONS to projectApprovedWithConditions
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectStatusAuthorization =
            ProjectStatusAuthorization(securityService, projectPersistence, projectService)

        projects.forEach { (projectId, projectObject) ->
            every { projectPersistence.getProject(projectId) } returns projectObject
        }
    }

    @Test
    fun `admin can perform any allowed status transition`() {
        every { securityService.currentUser } returns adminUser

        listOf(projectSubmitted, projectEligible, projectApprovedWithConditions)
            .forEach {
                every { projectPersistence.getApplicantAndStatusById(eq(it.id!!)) } returns
                    ProjectApplicantAndStatus(applicantId = 6489L, projectStatus = it.projectStatus.status)
                assertTrue(
                    projectStatusAuthorization.canReturnToApplicant(it.id!!),
                    "transition from ${it.projectStatus.status} to ${ApplicationStatusDTO.RETURNED_TO_APPLICANT} should be possible"
                )
            }

        listOf(projectDraft, projectReturned, projectIneligible, projectNotApproved)
            .forEach {
                every { projectPersistence.getApplicantAndStatusById(eq(it.id!!)) } returns
                    ProjectApplicantAndStatus(applicantId = 6488L, projectStatus = it.projectStatus.status)
                assertFalse(
                    projectStatusAuthorization.canReturnToApplicant(it.id!!),
                    "transition from ${projects[it]?.projectStatus?.status} to ${ApplicationStatusDTO.RETURNED_TO_APPLICANT} should not be possible"
                )
            }
    }

    @Test
    fun `owner cannot return`() {
        every { securityService.currentUser } returns applicantUser

        every { projectPersistence.getApplicantAndStatusById(projectSubmitted.id!!) } returns
            ProjectApplicantAndStatus(applicantId = applicantUser.user.id, projectStatus = ApplicationStatus.SUBMITTED)
        assertFalse(projectStatusAuthorization.canReturnToApplicant(projectSubmitted.id!!))

        every { projectPersistence.getApplicantAndStatusById(projectReturned.id!!) } returns
            ProjectApplicantAndStatus(applicantId = applicantUser.user.id, projectStatus = ApplicationStatus.RETURNED_TO_APPLICANT)
        assertFalse(projectStatusAuthorization.canReturnToApplicant(projectReturned.id!!))
    }

    @Test
    fun `programme user can only return to applicant`() {
        every { securityService.currentUser } returns programmeUser

        every { projectPersistence.getApplicantAndStatusById(projectSubmitted.id!!) } returns
            ProjectApplicantAndStatus(applicantId = applicantUser.user.id, projectStatus = ApplicationStatus.SUBMITTED)
        assertTrue(projectStatusAuthorization.canReturnToApplicant(projectSubmitted.id!!))

        every { projectPersistence.getApplicantAndStatusById(projectReturned.id!!) } returns
            ProjectApplicantAndStatus(applicantId = applicantUser.user.id, projectStatus = ApplicationStatus.RETURNED_TO_APPLICANT)
        assertFalse(projectStatusAuthorization.canReturnToApplicant(projectReturned.id!!))
    }

    @Test
    fun `current user is admin`() {
        every { securityService.currentUser } returns adminUser
        assertTrue(projectStatusAuthorization.isAdmin())
    }

    @Test
    fun `current user is not admin`() {
        every { securityService.currentUser } returns programmeUser
        assertFalse(projectStatusAuthorization.isAdmin())
    }

    @Test
    fun `current user is programme user`() {
        every { securityService.currentUser } returns programmeUser
        assertTrue(projectStatusAuthorization.isProgrammeUser())
    }

    @Test
    fun `current user is not programme user`() {
        every { securityService.currentUser } returns applicantUser
        assertFalse(projectStatusAuthorization.isProgrammeUser())
    }

    private fun createProject(id: Long, status: ApplicationStatus): Project {
        return Project(
            id = id,
            callSettings = ProjectCallSettings(
                callId = 1,
                callName = "call",
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now(),
                endDateStep1 = null,
                lengthOfPeriod = 12,
                isAdditionalFundAllowed = false,
                flatRates = emptySet(),
                lumpSums = emptyList(),
                unitCosts = emptyList(),
                applicationFormFieldConfigurations = mutableSetOf()
            ),
            acronym = "acronym",
            applicant = userApplicantWithoutRole,
            projectStatus = ProjectStatus(1, status, userApplicantWithoutRole, ZonedDateTime.now(), null),
            duration = 12,
        )
    }

    private fun provideAdminAndProgramUsers(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(programmeUser),
            Arguments.of(adminUser)
        )
    }

    private fun provideAllUsers(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(adminUser),
            Arguments.of(programmeUser),
            Arguments.of(applicantUser)
        )
    }

}
