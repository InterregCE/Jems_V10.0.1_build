package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority

class ProjectMonitoringAuthorizationTest: UnitTest() {

    companion object {
        val programmeUser = User(
            id = 22,
            email = "user@programme.dev",
            name = "",
            surname = "",
            userRole = UserRole(id = 2, name = "programme user", permissions = emptySet(), isDefault = false),
            userStatus = UserStatus.ACTIVE
        )
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var authorization: Authorization

    @MockK
    lateinit var currentUser: CurrentUser

    @InjectMockKs
    lateinit var projectMonitoringAuthorization: ProjectMonitoringAuthorization

    @BeforeEach
    fun setup() {
        clearMocks(authorization, securityService)
        MockKAnnotations.init(this)
    }

    @Test
    fun canViewProjectMonitoring() {
        val currentUser = LocalCurrentUser(
            programmeUser, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + AuthorizationUtil.userProgramme.userRole.name),
                SimpleGrantedAuthority(UserRolePermission.ProjectContractingView.key),
            )
        )
        currentUser.user.assignedProjects = setOf(1L)
        every { securityService.currentUser } returns currentUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectContractingView, 1L) } returns true
        assertThat(projectMonitoringAuthorization.canViewProjectMonitoring(1L)).isTrue
        assertThat(projectMonitoringAuthorization.canEditProjectMonitoring(1L)).isFalse
    }

    @Test
    fun canEditProjectMonitoring() {
        val currentUser = LocalCurrentUser(
            programmeUser, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + AuthorizationUtil.userProgramme.userRole.name),
                SimpleGrantedAuthority(UserRolePermission.ProjectSetToContracted.key),
            )
        )
        currentUser.user.assignedProjects = setOf(1L)
        every { securityService.currentUser } returns currentUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectSetToContracted, 1L) } returns true
        assertThat(projectMonitoringAuthorization.canEditProjectMonitoring(1L)).isTrue
    }
}
