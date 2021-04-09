package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ProjectEligibilityAssessmentResult
import io.cloudflight.jems.api.project.dto.status.ProjectQualityAssessmentResult
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.ZonedDateTime
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ProjectStatusAuthorizationTest {

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
    lateinit var projectAuthorization: ProjectAuthorization

    lateinit var projectStatusAuthorization: ProjectStatusAuthorization

    private val userApplicantWithoutRole = OutputUser(
        id = applicantUser.user.id,
        email = applicantUser.user.email,
        name = applicantUser.user.name,
        surname = applicantUser.user.surname
    )

    private val eligibilityAssessment = OutputProjectEligibilityAssessment(
        result = ProjectEligibilityAssessmentResult.PASSED,
        updated = ZonedDateTime.now()
    )
    private val qualityAssessment = OutputProjectQualityAssessment(
        result = ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING,
        updated = ZonedDateTime.now()
    )

    private val projectDraft = createProject(PID_DRAFT, ApplicationStatusDTO.DRAFT)
    private val projectSubmitted = createProject(PID_SUBMITTED, ApplicationStatusDTO.SUBMITTED)
    private val projectReturned = createProject(PID_RETURNED, ApplicationStatusDTO.RETURNED_TO_APPLICANT)
    private val projectSubmittedWithEa = projectSubmitted.copy(
        eligibilityAssessment = eligibilityAssessment
    )
    private val projectEligible = createProject(PID_ELIGIBLE, ApplicationStatusDTO.ELIGIBLE).copy(
        eligibilityAssessment = eligibilityAssessment
    )
    private val projectIneligible = createProject(PID_INELIGIBLE, ApplicationStatusDTO.INELIGIBLE).copy(
        eligibilityAssessment = eligibilityAssessment
    )
    private val projectEligibleWithQA = createProject(PID_ELIGIBLE_WITH_QA, ApplicationStatusDTO.ELIGIBLE).copy(
        qualityAssessment = qualityAssessment
    )
    private val projectNotApproved = createProject(PID_NOT_APPROVED, ApplicationStatusDTO.NOT_APPROVED)
    private val projectApprovedWithConditions =
        createProject(PID_APPROVED_WITH_CONDITIONS, ApplicationStatusDTO.APPROVED_WITH_CONDITIONS)
    private val projectApproved = createProject(PID_APPROVED, ApplicationStatusDTO.APPROVED)

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
            ProjectStatusAuthorization(securityService, projectAuthorization, projectService)

        projects.forEach { (projectId, projectObject) ->
            every { projectService.getById(projectId) } returns projectObject
        }
    }

    @Test
    fun `admin can perform any allowed status transition`() {
        every { securityService.currentUser } returns adminUser
        every { projectAuthorization.canReadProject(eq(PID_DRAFT)) } returns true

        assertTrue(projectStatusAuthorization.canChangeStatusTo(projectDraft, ApplicationStatusDTO.SUBMITTED))

        listOf(projectSubmitted, projectEligible, projectApprovedWithConditions)
            .forEach {
                every { projectAuthorization.canReadProject(eq(it.id!!)) } returns true
                assertTrue(
                    projectStatusAuthorization.canChangeStatusTo(it, ApplicationStatusDTO.RETURNED_TO_APPLICANT),
                    "transition from ${it.projectStatus.status} to ${ApplicationStatusDTO.RETURNED_TO_APPLICANT} should be possible"
                )
            }

        assertFalse(projectStatusAuthorization.canChangeStatusTo(projectSubmitted, ApplicationStatusDTO.SUBMITTED))

        listOf(projectDraft, projectReturned, projectIneligible, projectNotApproved)
            .forEach {
                every { projectAuthorization.canReadProject(eq(it.id!!)) } returns true
                assertFalse(
                    projectStatusAuthorization.canChangeStatusTo(it, ApplicationStatusDTO.RETURNED_TO_APPLICANT),
                    "transition from ${projects[it]?.projectStatus?.status} to ${ApplicationStatusDTO.RETURNED_TO_APPLICANT} should not be possible"
                )
            }
    }

    @Test
    fun `owner can only submit and resubmit`() {
        every { securityService.currentUser } returns applicantUser

        assertTrue(projectStatusAuthorization.canChangeStatusTo(projectDraft, ApplicationStatusDTO.SUBMITTED))
        assertFalse(
            projectStatusAuthorization.canChangeStatusTo(
                projectSubmitted,
                ApplicationStatusDTO.RETURNED_TO_APPLICANT
            )
        )

        assertFalse(projectStatusAuthorization.canChangeStatusTo(projectSubmitted, ApplicationStatusDTO.SUBMITTED))
        assertFalse(
            projectStatusAuthorization.canChangeStatusTo(
                projectReturned,
                ApplicationStatusDTO.RETURNED_TO_APPLICANT
            )
        )
    }

    @Test
    fun `programme user can only return to applicant`() {
        every { securityService.currentUser } returns programmeUser

        assertFalse(projectStatusAuthorization.canChangeStatusTo(projectDraft, ApplicationStatusDTO.SUBMITTED))
        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(projectSubmitted, ApplicationStatusDTO.RETURNED_TO_APPLICANT)
        )

        assertFalse(projectStatusAuthorization.canChangeStatusTo(projectSubmitted, ApplicationStatusDTO.SUBMITTED))
        assertFalse(
            projectStatusAuthorization.canChangeStatusTo(
                projectReturned,
                ApplicationStatusDTO.RETURNED_TO_APPLICANT
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `programme or admin user can enter eligibility decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser
        every { projectAuthorization.canReadProject(eq(PID_SUBMITTED_WITH_EA)) } returns true

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(projectSubmittedWithEa, ApplicationStatusDTO.ELIGIBLE)
        )
        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(projectSubmittedWithEa, ApplicationStatusDTO.INELIGIBLE)
        )
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `programme or admin user can enter EA`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        assertTrue(
            projectStatusAuthorization.canSetEligibilityAssessment(PID_SUBMITTED)
        )
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `programme or admin user can enter QA`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        listOf(PID_SUBMITTED, PID_SUBMITTED_WITH_EA, PID_ELIGIBLE).forEach {
            assertTrue(
                projectStatusAuthorization.canSetQualityAssessment(it)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `programme or admin user cannot enter QA when INELIGIBLE`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        assertFalse(projectStatusAuthorization.canSetQualityAssessment(PID_INELIGIBLE))
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `programme or admin user can enter funding decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser
        every { projectAuthorization.canReadProject(eq(PID_ELIGIBLE_WITH_QA)) } returns true

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(projectEligibleWithQA, ApplicationStatusDTO.APPROVED)
        )

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(
                projectEligibleWithQA,
                ApplicationStatusDTO.APPROVED_WITH_CONDITIONS
            )
        )

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(projectEligibleWithQA, ApplicationStatusDTO.NOT_APPROVED)
        )
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `programme or admin user can change funding decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser
        every { projectAuthorization.canReadProject(eq(PID_APPROVED_WITH_CONDITIONS)) } returns true

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(projectApprovedWithConditions, ApplicationStatusDTO.APPROVED)
        )
        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(
                projectApprovedWithConditions,
                ApplicationStatusDTO.NOT_APPROVED
            )
        )
        assertFalse( // no change
            projectStatusAuthorization.canChangeStatusTo(
                projectApprovedWithConditions,
                ApplicationStatusDTO.APPROVED_WITH_CONDITIONS
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideAllUsers")
    fun `cannot update final funding decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        listOf(projectApproved, projectNotApproved).forEach {
            every { projectAuthorization.canReadProject(eq(it.id!!)) } returns true
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ApplicationStatusDTO.APPROVED_WITH_CONDITIONS)
            )
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ApplicationStatusDTO.APPROVED)
            )
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ApplicationStatusDTO.NOT_APPROVED)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `cannot enter eligibility decision admin and programme user`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        val listOfAllowed = mutableListOf(projectSubmitted, projectReturned)
        if (currentUser.isAdmin)
            listOfAllowed.add(projectDraft)

        listOfAllowed.forEach {
            every { projectAuthorization.canReadProject(eq(it.id!!)) } returns true
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ApplicationStatusDTO.ELIGIBLE),
                "cannot make eligibility decision without eligibility assessment"
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideAllUsers")
    fun `cannot enter funding decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        val listOfAllowed =
            mutableListOf(projectSubmitted, projectReturned, projectDraft, projectEligible, projectIneligible)
        if (!currentUser.isAdmin && !currentUser.isProgrammeUser)
            listOfAllowed.remove(PID_DRAFT)

        listOfAllowed.forEach {
            every { projectAuthorization.canReadProject(eq(it.id!!)) } returns true
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ApplicationStatusDTO.APPROVED)
            )
        }
    }

    @Test
    fun `applicant cannot enter decisions`() {
        every { securityService.currentUser } returns applicantUser

        assertFalse(projectStatusAuthorization.canSetQualityAssessment(PID_SUBMITTED))
        assertFalse(projectStatusAuthorization.canSetEligibilityAssessment(PID_SUBMITTED))
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

    private fun createProject(id: Long, status: ApplicationStatusDTO): ProjectDetailDTO {
        return ProjectDetailDTO(
            id = id,
            callSettings = ProjectCallSettingsDTO(
                callId = 1,
                callName = "call",
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now(),
                lengthOfPeriod = 12,
                isAdditionalFundAllowed = false,
                flatRates = FlatRateSetupDTO(),
                lumpSums = emptyList(),
                unitCosts = emptyList(),
            ),
            acronym = "acronym",
            applicant = userApplicantWithoutRole,
            firstSubmission = null,
            lastResubmission = null,
            projectStatus = ProjectStatusDTO(1, status, userApplicantWithoutRole, ZonedDateTime.now(), null)
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
