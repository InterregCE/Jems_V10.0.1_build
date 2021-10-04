package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class AuthorizationUtil {
    companion object {

        val userAdmin = User(
            id = 1,
            email = "admin@admin.dev",
            name = "Name",
            surname = "Surname",
            userRole = UserRole(
                id = 1,
                name = "administrator",
                permissions = UserRolePermission.values().toSet(),
                isDefault = false
            ),
            userStatus = UserStatus.ACTIVE
        )

        val userProgramme = User(
            id = 2,
            email = "user@programme.dev",
            name = "",
            surname = "",
            userRole = UserRole(id = 2, name = "programme user", permissions = emptySet(), isDefault = false),
            userStatus = UserStatus.ACTIVE
        )

        val userApplicant = User(
            id = 3,
            email = "user@applicant.dev",
            name = "applicant",
            surname = "",
            userRole = UserRole(id = 3, name = "applicant user", permissions = emptySet(), isDefault = true),
            userStatus = UserStatus.ACTIVE
        )

        internal val programmeUser = LocalCurrentUser(
            userProgramme, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userProgramme.userRole.name),
                SimpleGrantedAuthority(UserRolePermission.ProgrammeSetupRetrieve.key),
                SimpleGrantedAuthority(UserRolePermission.ProgrammeSetupUpdate.key),
            )
        )

        internal val adminUser = LocalCurrentUser(
            userAdmin, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userAdmin.userRole.name),
                SimpleGrantedAuthority(UserRolePermission.ProgrammeSetupRetrieve.key),
                SimpleGrantedAuthority(UserRolePermission.ProgrammeSetupUpdate.key),
                SimpleGrantedAuthority(UserRolePermission.ProjectFormUpdate.key),
            )
        )

        internal val applicantUser = LocalCurrentUser(
            userApplicant, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + userApplicant.userRole.name)
            )
        )
    }
}
