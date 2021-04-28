package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.call.authorization.CallAuthorization
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.dto.ProjectApplicantAndStatus
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.userApplicant
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
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

internal class ProjectAuthorizationTest {

    companion object {
        private val ownerApplicant = OutputUser(
            id = userApplicant.id,
            name = userApplicant.name,
            email = userApplicant.email,
            surname = userApplicant.surname
        )

        private val notOwnerApplicant = User(
            id = 256,
            name = "not-owner",
            email = "not-owner@applicant",
            surname = "applicant",
            userRole = UserRole(id = 1, name = "applicant", permissions = emptySet())
        )

        private fun testProject(status: ApplicationStatusDTO) = ProjectApplicantAndStatus(
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
        ApplicationStatusDTO.values().forEach {
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
        ApplicationStatusDTO.values().forEach {
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
            )
        )
        every { projectService.getApplicantAndStatusById(eq(3)) } returns testProject(ApplicationStatusDTO.DRAFT)

        val exception = assertThrows<ResourceNotFoundException>(
            "applicant cannot find project when he is not an owner"
        ) { projectAuthorization.canReadProject(3) }
        assertThat(exception.entity).isEqualTo("project")
    }

    @Test
    fun `programmeUser canReadProject DRAFT`() {
        every { securityService.currentUser } returns programmeUser
        every { projectService.getApplicantAndStatusById(eq(4)) } returns testProject(ApplicationStatusDTO.DRAFT)

        val exception = assertThrows<ResourceNotFoundException>(
            "programme user cannot find project in ${ApplicationStatusDTO.DRAFT}"
        ) { projectAuthorization.canReadProject(4) }
        assertThat(exception.entity).isEqualTo("project")
    }

    @Test
    fun `programmeUser canReadProject non-DRAFT`() {
        every { securityService.currentUser } returns programmeUser
        val possibleStatuses = ApplicationStatusDTO.values()
            .filter { it != ApplicationStatusDTO.DRAFT && it != ApplicationStatusDTO.STEP1_DRAFT }
            .toMutableSet()

        possibleStatuses.forEach {
            every { projectService.getApplicantAndStatusById(eq(5)) } returns testProject(it)
            assertTrue(projectAuthorization.canReadProject(5), "Programme user can read project in $it")
        }
    }

    @Test
    fun `user without role canReadProject`() {
        every { securityService.currentUser } returns LocalCurrentUser(notOwnerApplicant, "hash_pass", emptyList())
        every { projectService.getApplicantAndStatusById(eq(6)) } returns testProject(ApplicationStatusDTO.DRAFT)
        assertFalse(projectAuthorization.canReadProject(6), "Fallback - user without role should get 'false'")
    }

    @Test
    fun `admin canCreateProjectForCall`() {
        every { securityService.currentUser } returns adminUser
        every { callAuthorization.canReadCall(eq(1)) } returns true
        assertTrue(
            projectAuthorization.canCreateProjectForCall(1),
            "admin is able to create call when he can read call"
        )
    }

    @Test
    fun `applicant canCreateProjectForCall`() {
        every { securityService.currentUser } returns applicantUser
        every { callAuthorization.canReadCall(eq(2)) } returns true
        assertTrue(
            projectAuthorization.canCreateProjectForCall(2),
            "applicant is able to create call when he can read call"
        )
    }

    @Test
    fun `programmeUser canCreateProjectForCall`() {
        every { securityService.currentUser } returns programmeUser
        every { callAuthorization.canReadCall(eq(3)) } returns true
        assertFalse(
            projectAuthorization.canCreateProjectForCall(3),
            "programmeUser is NOT able to create call"
        )
    }

    @Test
    fun `anyone canCreateProjectForCall when cannot read`() {
        every { callAuthorization.canReadCall(eq(4)) } returns false
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
        val impossibleStatuses = ApplicationStatusDTO.values().toMutableSet()
        impossibleStatuses.removeAll(
            listOf(
                ApplicationStatusDTO.DRAFT,
                ApplicationStatusDTO.STEP1_DRAFT,
                ApplicationStatusDTO.RETURNED_TO_APPLICANT
            )
        )

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
            ApplicationStatusDTO.DRAFT,
            ApplicationStatusDTO.RETURNED_TO_APPLICANT
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
        ApplicationStatusDTO.values()
            .filter { it != ApplicationStatusDTO.DRAFT && it != ApplicationStatusDTO.STEP1_DRAFT }
            .toMutableSet()
            .forEach {
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
        every { projectService.getApplicantAndStatusById(eq(4)) } returns testProject(ApplicationStatusDTO.DRAFT)

        val exception = assertThrows<ResourceNotFoundException>(
            "programmeUser cannot find project when he is in ${ApplicationStatusDTO.DRAFT}"
        ) { projectAuthorization.canUpdateProject(4) }
        assertThat(exception.entity).isEqualTo("project")
    }

    @Test
    fun `not-owner canUpdateProject`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            notOwnerApplicant, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userApplicant.userRole.name)
            )
        )
        ApplicationStatusDTO.values().forEach {
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
        every { projectService.getApplicantAndStatusById(eq(6)) } returns testProject(ApplicationStatusDTO.DRAFT)
        assertFalse(projectAuthorization.canUpdateProject(6), "Fallback - user without role should get 'false'")
    }

}
