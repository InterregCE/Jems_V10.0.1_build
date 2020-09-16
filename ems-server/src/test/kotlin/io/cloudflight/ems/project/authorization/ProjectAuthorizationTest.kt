package io.cloudflight.ems.project.authorization

import io.cloudflight.ems.api.call.dto.OutputCallWithDates
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.api.user.dto.OutputUser
import io.cloudflight.ems.api.user.dto.OutputUserRole
import io.cloudflight.ems.api.user.dto.OutputUserWithRole
import io.cloudflight.ems.call.authorization.CallAuthorization
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.project.dto.ProjectApplicantAndStatus
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.ems.project.service.ProjectService
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.userApplicant
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.ZonedDateTime

internal class ProjectAuthorizationTest {

    companion object {
        private val ownerApplicant = OutputUser(
            id = userApplicant.id,
            name = userApplicant.name,
            email = userApplicant.email,
            surname = userApplicant.surname
        )

        private val notOwnerApplicant = OutputUserWithRole(
            id = 256,
            name = "not-owner",
            email = "not-owner@applicant",
            surname = "applicant",
            userRole = OutputUserRole(id = 1, name = "applicant")
        )

        private val call = OutputCallWithDates(
            id = 1,
            name = "call",
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1)
        )

        private fun testProject(status: ProjectApplicationStatus) = ProjectApplicantAndStatus(
            applicantId = ownerApplicant.id!!,
            projectStatus = status
        )
    }

    @MockK
    lateinit var securityService: SecurityService
    @MockK
    lateinit var projectService: ProjectService
    @MockK
    lateinit var callAuthorization: CallAuthorization

    lateinit var projectAuthorization: ProjectAuthorization

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectAuthorization = ProjectAuthorization(securityService, projectService, callAuthorization)
    }

    @Test
    fun `admin canReadProject`() {
        every { securityService.currentUser } returns adminUser
        ProjectApplicationStatus.values().forEach {
            every { projectService.getApplicantAndStatusById(eq(1)) } returns testProject(it)
            assertTrue(
                projectAuthorization.canReadProject(1),
                "admin is able to read Project anytime (also $it)"
            )
        }
    }

    @Test
    fun `owner canReadProject`() {
        every { securityService.currentUser } returns applicantUser
        ProjectApplicationStatus.values().forEach {
            every { projectService.getApplicantAndStatusById(eq(2)) } returns testProject(it)
            assertTrue(
                projectAuthorization.canReadProject(2),
                "applicant who is owner of Project is able to read Project anytime (also $it)"
            )
        }
    }

    @Test
    fun `not-owner canReadProject`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            notOwnerApplicant, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userApplicant.userRole.name)
        ))
        every { projectService.getApplicantAndStatusById(eq(3)) } returns testProject(ProjectApplicationStatus.DRAFT)

        val exception = assertThrows<ResourceNotFoundException>(
            "applicant cannot find project when he is not an owner"
        ) { projectAuthorization.canReadProject(3) }
        assertThat(exception.entity).isEqualTo("project")
    }

    @Test
    fun `programmeUser canReadProject DRAFT`() {
        every { securityService.currentUser } returns programmeUser
        every { projectService.getApplicantAndStatusById(eq(4)) } returns testProject(ProjectApplicationStatus.DRAFT)

        val exception = assertThrows<ResourceNotFoundException>(
            "programme user cannot find project in ${ProjectApplicationStatus.DRAFT}"
        ) { projectAuthorization.canReadProject(4) }
        assertThat(exception.entity).isEqualTo("project")
    }

    @Test
    fun `programmeUser canReadProject non-DRAFT`() {
        every { securityService.currentUser } returns programmeUser
        val possibleStatuses = ProjectApplicationStatus.values().toMutableSet()
        possibleStatuses.remove(ProjectApplicationStatus.DRAFT)

        possibleStatuses.forEach {
            every { projectService.getApplicantAndStatusById(eq(5)) } returns testProject(it)
            assertTrue(projectAuthorization.canReadProject(5), "Programme user can read project in $it")
        }
    }

    @Test
    fun `user without role canReadProject`() {
        every { securityService.currentUser } returns LocalCurrentUser(notOwnerApplicant, "hash_pass", emptyList())
        every { projectService.getApplicantAndStatusById(eq(6)) } returns testProject(ProjectApplicationStatus.DRAFT)
        assertFalse(projectAuthorization.canReadProject(6), "Fallback - user without role should get 'false'")
    }

    @Test
    fun `admin canCreateProjectForCall`() {
        every { securityService.currentUser } returns adminUser
        every { callAuthorization.canReadCallDetail(eq(1)) } returns true
        assertTrue(
            projectAuthorization.canCreateProjectForCall(1),
            "admin is able to create call when he can read call"
        )
    }

    @Test
    fun `applicant canCreateProjectForCall`() {
        every { securityService.currentUser } returns applicantUser
        every { callAuthorization.canReadCallDetail(eq(2)) } returns true
        assertTrue(
            projectAuthorization.canCreateProjectForCall(2),
            "applicant is able to create call when he can read call"
        )
    }

    @Test
    fun `programmeUser canCreateProjectForCall`() {
        every { securityService.currentUser } returns programmeUser
        every { callAuthorization.canReadCallDetail(eq(3)) } returns true
        assertFalse(
            projectAuthorization.canCreateProjectForCall(3),
            "programmeUser is NOT able to create call"
        )
    }

    @Test
    fun `anyone canCreateProjectForCall when cannot read`() {
        every { callAuthorization.canReadCallDetail(eq(4)) } returns false
        listOf(applicantUser, adminUser, programmeUser).forEach {
            every { securityService.currentUser } returns it
            assertFalse(
                projectAuthorization.canCreateProjectForCall(4),
                "user is NOT able to create call, when he cannot read call details"
            )
        }
    }

    @Test
    fun `admin canUpdateProject - NOT`() {
        every { securityService.currentUser } returns adminUser
        val impossibleStatuses = ProjectApplicationStatus.values().toMutableSet()
        impossibleStatuses.removeAll(listOf(
            ProjectApplicationStatus.DRAFT,
            ProjectApplicationStatus.RETURNED_TO_APPLICANT
        ))

        impossibleStatuses.forEach {
            every { projectService.getApplicantAndStatusById(eq(1)) } returns testProject(it)
            assertFalse(
                projectAuthorization.canUpdateProject(1),
                "admin is NOT able to update Project when $it"
            )
        }
    }

    @Test
    fun `admin canUpdateProject`() {
        every { securityService.currentUser } returns adminUser
        val possibleStatuses = listOf(
            ProjectApplicationStatus.DRAFT,
            ProjectApplicationStatus.RETURNED_TO_APPLICANT
        )

        possibleStatuses.forEach {
            every { projectService.getApplicantAndStatusById(eq(2)) } returns testProject(it)
            assertTrue(
                projectAuthorization.canUpdateProject(2),
                "admin is able to update Project when $it"
            )
        }
    }

    @Test
    fun `programmeUser canUpdateProject`() {
        every { securityService.currentUser } returns programmeUser
        val impossibleStatuses = ProjectApplicationStatus.values().toMutableSet()
        impossibleStatuses.removeAll(listOf(
            ProjectApplicationStatus.DRAFT
        ))

        impossibleStatuses.forEach {
            every { projectService.getApplicantAndStatusById(eq(3)) } returns testProject(it)
            assertFalse(
                projectAuthorization.canUpdateProject(3),
                "programmeUser is NOT able to update Project anytime (tested with $it)"
            )
        }
    }

    @Test
    fun `programmeUser canUpdateProject - DRAFT`() {
        every { securityService.currentUser } returns programmeUser
        every { projectService.getApplicantAndStatusById(eq(4)) } returns testProject(ProjectApplicationStatus.DRAFT)

        val exception = assertThrows<ResourceNotFoundException>(
            "programmeUser cannot find project when he is in ${ProjectApplicationStatus.DRAFT}"
        ) { projectAuthorization.canUpdateProject(4) }
        assertThat(exception.entity).isEqualTo("project")
    }

    @Test
    fun `not-owner canUpdateProject`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            notOwnerApplicant, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + userApplicant.userRole.name)
        ))
        ProjectApplicationStatus.values().forEach {
            every { projectService.getApplicantAndStatusById(eq(5)) } returns testProject(it)
            val exception = assertThrows<ResourceNotFoundException>(
                "applicant, who is not an owner of project can never find project ($it)"
            ) { projectAuthorization.canReadProject(5) }
            assertThat(exception.entity).isEqualTo("project")
        }
    }

    @Test
    fun `user without role canUpdateProject`() {
        every { securityService.currentUser } returns LocalCurrentUser(notOwnerApplicant, "hash_pass", emptyList())
        every { projectService.getApplicantAndStatusById(eq(6)) } returns testProject(ProjectApplicationStatus.DRAFT)
        assertFalse(projectAuthorization.canUpdateProject(6), "Fallback - user without role should get 'false'")
    }

}
