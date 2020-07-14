package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputProjectStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
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
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.ZonedDateTime

internal class ProjectStatusAuthorizationTest {

    companion object {
        const val PID_DRAFT: Long = 1
        const val PID_SUBMITTED: Long = 2L
        const val PID_RETURNED: Long = 3L
        const val PID_RESUBMITTED: Long = 4L
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

    private val projectDraft = createProject(PID_DRAFT, ProjectApplicationStatus.DRAFT)
    private val projectSubmitted = createProject(PID_SUBMITTED, ProjectApplicationStatus.SUBMITTED)
    private val projectReturned = createProject(PID_RETURNED, ProjectApplicationStatus.RETURNED_TO_APPLICANT)
    private val projectResubmitted = createProject(PID_RESUBMITTED, ProjectApplicationStatus.RESUBMITTED)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectStatusAuthorization = ProjectStatusAuthorization(securityService, projectService)

        every { projectService.getById(PID_DRAFT) } returns projectDraft
        every { projectService.getById(PID_SUBMITTED) } returns projectSubmitted
        every { projectService.getById(PID_RETURNED) } returns projectReturned
        every { projectService.getById(PID_RESUBMITTED) } returns projectResubmitted
    }

    @Test
    fun `admin can change any allowed status`() {
        every { securityService.currentUser } returns LocalCurrentUser(userAdmin, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userAdmin.userRole.name)
        ))

        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_DRAFT, ProjectApplicationStatus.SUBMITTED))
        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED, ProjectApplicationStatus.RETURNED_TO_APPLICANT))
        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_RETURNED, ProjectApplicationStatus.RESUBMITTED))
        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_RESUBMITTED, ProjectApplicationStatus.RETURNED_TO_APPLICANT))

        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_DRAFT, ProjectApplicationStatus.RESUBMITTED))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED, ProjectApplicationStatus.SUBMITTED))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_RETURNED, ProjectApplicationStatus.RETURNED_TO_APPLICANT))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_RESUBMITTED, ProjectApplicationStatus.SUBMITTED))
    }

    @Test
    fun `owner can only submit and resubmit`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userApplicant.userRole.name)
        ))

        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_DRAFT, ProjectApplicationStatus.SUBMITTED))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED, ProjectApplicationStatus.RETURNED_TO_APPLICANT))
        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_RETURNED, ProjectApplicationStatus.RESUBMITTED))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_RESUBMITTED, ProjectApplicationStatus.RETURNED_TO_APPLICANT))

        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_DRAFT, ProjectApplicationStatus.RESUBMITTED))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED, ProjectApplicationStatus.SUBMITTED))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_RETURNED, ProjectApplicationStatus.RETURNED_TO_APPLICANT))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_RESUBMITTED, ProjectApplicationStatus.SUBMITTED))
    }

    @Test
    fun `programme user can only return to applicant`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userProgramme.userRole.name)
        ))

        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_DRAFT, ProjectApplicationStatus.SUBMITTED))
        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED, ProjectApplicationStatus.RETURNED_TO_APPLICANT))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_RETURNED, ProjectApplicationStatus.RESUBMITTED))
        assertTrue(projectStatusAuthorization.canChangeStatusTo(PID_RESUBMITTED, ProjectApplicationStatus.RETURNED_TO_APPLICANT))

        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_DRAFT, ProjectApplicationStatus.RESUBMITTED))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_SUBMITTED, ProjectApplicationStatus.SUBMITTED))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_RETURNED, ProjectApplicationStatus.RETURNED_TO_APPLICANT))
        assertFalse(projectStatusAuthorization.canChangeStatusTo(PID_RESUBMITTED, ProjectApplicationStatus.SUBMITTED))
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
        every { securityService.currentUser } returns LocalCurrentUser(userAdmin, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userAdmin.userRole.name)
        ))
        assertTrue(projectStatusAuthorization.isAdmin())
    }

    @Test
    fun `current user is not admin`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userProgramme.userRole.name)
        ))
        assertFalse(projectStatusAuthorization.isAdmin())
    }

    @Test
    fun `current user is programme user`() {
        every { securityService.currentUser } returns LocalCurrentUser(userProgramme, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userProgramme.userRole.name)
        ))
        assertTrue(projectStatusAuthorization.isProgrammeUser())
    }

    @Test
    fun `current user is not programme user`() {
        every { securityService.currentUser } returns LocalCurrentUser(userApplicant, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userApplicant.userRole.name)
        ))
        assertFalse(projectStatusAuthorization.isProgrammeUser())
    }

    private fun createProject(id: Long, status: ProjectApplicationStatus): OutputProject {
        return OutputProject(
            id = id,
            acronym = "acronym",
            applicant = userApplicantWithoutRole,
            submissionDate = null,
            resubmissionDate = null,
            projectStatus = OutputProjectStatus(1, status, userApplicantWithoutRole, ZonedDateTime.now(), null)
        )
    }
}
