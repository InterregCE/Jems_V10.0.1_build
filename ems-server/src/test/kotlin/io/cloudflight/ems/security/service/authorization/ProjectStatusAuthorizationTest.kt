package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputProjectEligibilityAssessment
import io.cloudflight.ems.api.dto.OutputProjectQualityAssessment
import io.cloudflight.ems.api.dto.OutputProjectStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.api.dto.ProjectEligibilityAssessmentResult
import io.cloudflight.ems.api.dto.ProjectQualityAssessmentResult
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.ProjectService
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
import org.springframework.security.core.authority.SimpleGrantedAuthority
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

    lateinit var projectStatusAuthorization: ProjectStatusAuthorization

    private val userAdmin = OutputUserWithRole(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "administrator")
    )

    private val userProgramme = OutputUserWithRole(
        id = 2,
        email = "user@programme.dev",
        name = "",
        surname = "",
        userRole = OutputUserRole(id = 2, name = "programme user")
    )

    private val userApplicant = OutputUserWithRole(
        id = 3,
        email = "applicant@programme.dev",
        name = "applicant",
        surname = "",
        userRole = OutputUserRole(id = 3, name = "applicant user")
    )

    private val userApplicantWithoutRole = OutputUser(
        id = userApplicant.id,
        email = userApplicant.email,
        name = userApplicant.name,
        surname = userApplicant.surname
    )

    private val userProgrammeWithoutRole = OutputUser(
        id = userProgramme.id,
        email = userProgramme.email,
        name = userProgramme.name,
        surname = userProgramme.surname
    )

    private val programmeUser = LocalCurrentUser(
        userProgramme, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userProgramme.userRole.name)
        )
    )
    private val adminUser = LocalCurrentUser(
        userAdmin, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userAdmin.userRole.name)
        )
    )
    private val applicantUser = LocalCurrentUser(
        userApplicant, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userApplicant.userRole.name)
        )
    )

    private val eligibilityAssessment = OutputProjectEligibilityAssessment(
        result = ProjectEligibilityAssessmentResult.PASSED,
        user = userProgrammeWithoutRole,
        updated = ZonedDateTime.now())
    private val qualityAssessment = OutputProjectQualityAssessment(
        result = ProjectQualityAssessmentResult.RECOMMENDED_FOR_FUNDING,
        user = userProgrammeWithoutRole,
        updated = ZonedDateTime.now()
    )

    private val projectDraft = createProject(PID_DRAFT, ProjectApplicationStatus.DRAFT)
    private val projectSubmitted = createProject(PID_SUBMITTED, ProjectApplicationStatus.SUBMITTED)
    private val projectReturned = createProject(PID_RETURNED, ProjectApplicationStatus.RETURNED_TO_APPLICANT)
    private val projectSubmittedWithEa = projectSubmitted.copy(
        eligibilityAssessment = eligibilityAssessment)
    private val projectEligible = createProject(PID_ELIGIBLE, ProjectApplicationStatus.ELIGIBLE).copy(
        eligibilityAssessment = eligibilityAssessment)
    private val projectIneligible = createProject(PID_INELIGIBLE, ProjectApplicationStatus.INELIGIBLE).copy(
        eligibilityAssessment = eligibilityAssessment)
    private val projectEligibleWithQA = createProject(PID_ELIGIBLE_WITH_QA, ProjectApplicationStatus.ELIGIBLE).copy(
        qualityAssessment = qualityAssessment)
    private val projectNotApproved = createProject(PID_NOT_APPROVED, ProjectApplicationStatus.NOT_APPROVED)
    private val projectApprovedWithConditions = createProject(PID_APPROVED_WITH_CONDITIONS, ProjectApplicationStatus.APPROVED_WITH_CONDITIONS)
    private val projectApproved = createProject(PID_APPROVED, ProjectApplicationStatus.APPROVED)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectStatusAuthorization = ProjectStatusAuthorization(securityService, projectService)

        every { projectService.getById(PID_DRAFT) } returns projectDraft
        every { projectService.getById(PID_SUBMITTED) } returns projectSubmitted
        every { projectService.getById(PID_SUBMITTED_WITH_EA) } returns projectSubmittedWithEa
        every { projectService.getById(PID_RETURNED) } returns projectReturned
        every { projectService.getById(PID_ELIGIBLE) } returns projectEligible
        every { projectService.getById(PID_INELIGIBLE) } returns projectIneligible
        every { projectService.getById(PID_ELIGIBLE_WITH_QA) } returns projectEligibleWithQA
        every { projectService.getById(PID_NOT_APPROVED) } returns projectNotApproved
        every { projectService.getById(PID_APPROVED_WITH_CONDITIONS) } returns projectApprovedWithConditions
        every { projectService.getById(PID_APPROVED) } returns projectApproved
    }

    @Test
    fun `admin can change any allowed status`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            userAdmin, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userAdmin.userRole.name)
            )
        )

        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_DRAFT, ProjectApplicationStatus.SUBMITTED))
        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(
                PID_SUBMITTED,
                ProjectApplicationStatus.RETURNED_TO_APPLICANT
            )
        )

        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED, ProjectApplicationStatus.SUBMITTED))
        assertFalse(
            projectStatusAuthorization.canChangeStatusTo(
                PID_RETURNED,
                ProjectApplicationStatus.RETURNED_TO_APPLICANT
            )
        )
    }

    @Test
    fun `owner can only submit and resubmit`() {
        every { securityService.currentUser } returns applicantUser

        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_DRAFT, ProjectApplicationStatus.SUBMITTED))
        assertFalse(
            projectStatusAuthorization.canChangeStatusTo(
                PID_SUBMITTED,
                ProjectApplicationStatus.RETURNED_TO_APPLICANT
            )
        )

        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED, ProjectApplicationStatus.SUBMITTED))
        assertFalse(
            projectStatusAuthorization.canChangeStatusTo(
                PID_RETURNED,
                ProjectApplicationStatus.RETURNED_TO_APPLICANT
            )
        )
    }

    @Test
    fun `programme user can only return to applicant`() {
        every { securityService.currentUser } returns programmeUser

        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_DRAFT, ProjectApplicationStatus.SUBMITTED))
        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(
                PID_SUBMITTED,
                ProjectApplicationStatus.RETURNED_TO_APPLICANT
            )
        )

        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED, ProjectApplicationStatus.SUBMITTED))
        assertFalse(
            projectStatusAuthorization.canChangeStatusTo(
                PID_RETURNED,
                ProjectApplicationStatus.RETURNED_TO_APPLICANT
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `programme or admin user can enter eligibility decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED_WITH_EA, ProjectApplicationStatus.ELIGIBLE)
        )
        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED_WITH_EA, ProjectApplicationStatus.INELIGIBLE)
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

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(PID_ELIGIBLE_WITH_QA, ProjectApplicationStatus.APPROVED)
        )

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(PID_ELIGIBLE_WITH_QA, ProjectApplicationStatus.APPROVED_WITH_CONDITIONS)
        )

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(PID_ELIGIBLE_WITH_QA, ProjectApplicationStatus.NOT_APPROVED)
        )
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `programme or admin user can change funding decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(PID_APPROVED_WITH_CONDITIONS, ProjectApplicationStatus.APPROVED)
        )
        assertTrue(
            projectStatusAuthorization.canChangeStatusTo(PID_APPROVED_WITH_CONDITIONS, ProjectApplicationStatus.NOT_APPROVED)
        )
        assertFalse( // no change
            projectStatusAuthorization.canChangeStatusTo(PID_APPROVED_WITH_CONDITIONS, ProjectApplicationStatus.APPROVED_WITH_CONDITIONS)
        )
    }

    @ParameterizedTest
    @MethodSource("provideAllUsers")
    fun `cannot update final funding decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        listOf(PID_APPROVED, PID_NOT_APPROVED).forEach {
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ProjectApplicationStatus.APPROVED_WITH_CONDITIONS)
            )
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ProjectApplicationStatus.APPROVED)
            )
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ProjectApplicationStatus.NOT_APPROVED)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideAllUsers")
    fun `cannot enter eligibility decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        listOf(PID_SUBMITTED, PID_RETURNED, PID_DRAFT).forEach {
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ProjectApplicationStatus.ELIGIBLE)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideAllUsers")
    fun `cannot enter funding decision`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        listOf(PID_SUBMITTED, PID_RETURNED, PID_DRAFT, PID_ELIGIBLE, PID_INELIGIBLE).forEach {
            assertFalse(
                projectStatusAuthorization.canChangeStatusTo(it, ProjectApplicationStatus.APPROVED)
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
    fun `current user is owner of project`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", emptyList())
        assertTrue(projectStatusAuthorization.isOwner(projectDraft))
    }

    @Test
    fun `current user is not owner of project`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", emptyList())
        assertFalse(projectStatusAuthorization.isOwner(projectDraft))
    }

    @Test
    fun `current user is admin`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            userAdmin, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userAdmin.userRole.name)
            )
        )
        assertTrue(projectStatusAuthorization.isAdmin())
    }

    @Test
    fun `current user is not admin`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            userProgramme, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userProgramme.userRole.name)
            )
        )
        assertFalse(projectStatusAuthorization.isAdmin())
    }

    @Test
    fun `current user is programme user`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            userProgramme, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userProgramme.userRole.name)
            )
        )
        assertTrue(projectStatusAuthorization.isProgrammeUser())
    }

    @Test
    fun `current user is not programme user`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            userApplicant, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userApplicant.userRole.name)
            )
        )
        assertFalse(projectStatusAuthorization.isProgrammeUser())
    }

    private fun createProject(id: Long, status: ProjectApplicationStatus): OutputProject {
        return OutputProject(
            id = id,
            acronym = "acronym",
            applicant = userApplicantWithoutRole,
            firstSubmission = null,
            lastResubmission = null,
            projectStatus = OutputProjectStatus(1, status, userApplicantWithoutRole, ZonedDateTime.now(), null)
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
