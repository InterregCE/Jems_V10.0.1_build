package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.authorization.CallAuthorization
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.userApplicant
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class ProjectAuthorizationTest : UnitTest() {

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

        private fun testProject(status: ApplicationStatus) = ProjectApplicantAndStatus(
            applicantId = ownerApplicant.id!!,
            projectStatus = status
        )
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var callAuthorization: CallAuthorization

    @InjectMockKs
    lateinit var projectAuthorization: ProjectAuthorization

    @Test
    fun `admin canReadProject`() {
        every { securityService.currentUser } returns adminUser
        ApplicationStatus.values().forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(1)) } returns testProject(it)
            assertTrue(
                projectAuthorization.canReadProject(1),
                "admin is able to read Project anytime (also $it)"
            )
        }
    }

    @Test
    fun `owner canReadProject`() {
        every { securityService.currentUser } returns applicantUser
        ApplicationStatus.values().forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(2)) } returns testProject(it)
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
        every { projectPersistence.getApplicantAndStatusById(eq(3)) } returns testProject(ApplicationStatus.DRAFT)

        val exception = assertThrows<ResourceNotFoundException>(
            "applicant cannot find project when he is not an owner"
        ) { projectAuthorization.canReadProject(3) }
        assertThat(exception.entity).isEqualTo("project")
    }

    @Test
    fun `programmeUser canReadProject DRAFT`() {
        every { securityService.currentUser } returns programmeUser
        every { projectPersistence.getApplicantAndStatusById(eq(4)) } returns testProject(ApplicationStatus.DRAFT)

        ApplicationStatus.values().forEach {
            assertTrue(
                projectAuthorization.canReadProject(4),
                "programme user is able to read Project anytime (also $it)"
            )
        }
    }

    @Test
    fun `programmeUser canReadProject non-DRAFT`() {
        every { securityService.currentUser } returns programmeUser
        val possibleStatuses = ApplicationStatus.values()
            .filter { it != ApplicationStatus.DRAFT && it != ApplicationStatus.STEP1_DRAFT }
            .toMutableSet()

        possibleStatuses.forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(5)) } returns testProject(it)
            assertTrue(projectAuthorization.canReadProject(5), "Programme user can read project in $it")
        }
    }

    @Test
    fun `user without role canReadProject`() {
        every { securityService.currentUser } returns LocalCurrentUser(notOwnerApplicant, "hash_pass", emptyList())
        every { projectPersistence.getApplicantAndStatusById(eq(6)) } returns testProject(ApplicationStatus.DRAFT)
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

    @ParameterizedTest(name = "admin canUpdateProject should throw 404 not found, because he is not owner (status {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `admin canUpdateProject should return false, because he is not owner`(status: ApplicationStatus) {
        every { securityService.currentUser } returns adminUser
        every { projectPersistence.getApplicantAndStatusById(eq(1)) } returns testProject(status)
        assertThrows<ResourceNotFoundException> { projectAuthorization.canOwnerUpdateProject(1) }
    }

    @ParameterizedTest(name = "programme user canUpdateProject should throw 404 not found, because he is not owner (status {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `programmeUser canUpdateProject should return false, because he is not owner`(status: ApplicationStatus) {
        every { securityService.currentUser } returns programmeUser
        every { projectPersistence.getApplicantAndStatusById(eq(2)) } returns testProject(status)
        assertThrows<ResourceNotFoundException> { projectAuthorization.canOwnerUpdateProject(2) }
    }

    @ParameterizedTest(name = "owner canUpdateProject should return false, because {0} is not valid status for change)")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"], mode = EnumSource.Mode.EXCLUDE)
    fun `owner canUpdateProject should return false, status is wrong`(status: ApplicationStatus) {
        every { securityService.currentUser } returns applicantUser
        every { projectPersistence.getApplicantAndStatusById(eq(3)) } returns testProject(status)
        assertFalse(projectAuthorization.canOwnerUpdateProject(3))
    }

    @ParameterizedTest(name = "owner canUpdateProject should return TRUE, because {0} is valid status for change)")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"])
    fun `owner canUpdateProject should return true, status is OK`(status: ApplicationStatus) {
        every { securityService.currentUser } returns applicantUser
        every { projectPersistence.getApplicantAndStatusById(eq(3)) } returns testProject(status)
        assertTrue(projectAuthorization.canOwnerUpdateProject(3))
    }

}
