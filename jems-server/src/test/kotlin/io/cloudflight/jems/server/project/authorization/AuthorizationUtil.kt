package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class AuthorizationUtil {
    companion object {

        val userAdmin = User(
            id = 1,
            email = "admin@admin.dev",
            name = "Name",
            surname = "Surname",
            userRole = UserRole(id = 1, name = "administrator", permissions = UserRolePermission.values().toSet())
        )

        val userProgramme = User(
            id = 2,
            email = "user@programme.dev",
            name = "",
            surname = "",
            userRole = UserRole(id = 2, name = "programme user", permissions = emptySet())
        )

        val userApplicant = User(
            id = 3,
            email = "user@applicant.dev",
            name = "applicant",
            surname = "",
            userRole = UserRole(id = 3, name = "applicant user", permissions = emptySet())
        )

        internal val programmeUser = LocalCurrentUser(
            userProgramme, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userProgramme.userRole.name)
            )
        )

        internal val adminUser = LocalCurrentUser(
            userAdmin, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userAdmin.userRole.name)
            )
        )

        internal val applicantUser = LocalCurrentUser(
            userApplicant, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userApplicant.userRole.name)
            )
        )
    }
}
