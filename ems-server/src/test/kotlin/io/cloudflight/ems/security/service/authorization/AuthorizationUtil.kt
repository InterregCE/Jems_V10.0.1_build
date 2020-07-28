package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.security.model.LocalCurrentUser
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class AuthorizationUtil {
    companion object {

        val userAdmin = OutputUserWithRole(
            id = 1,
            email = "admin@admin.dev",
            name = "Name",
            surname = "Surname",
            userRole = OutputUserRole(id = 1, name = "administrator")
        )

        val userProgramme = OutputUserWithRole(
            id = 2,
            email = "user@programme.dev",
            name = "",
            surname = "",
            userRole = OutputUserRole(id = 2, name = "programme user")
        )

        val userApplicant = OutputUserWithRole(
            id = 3,
            email = "user@applicant.dev",
            name = "applicant",
            surname = "",
            userRole = OutputUserRole(id = 3, name = "applicant user")
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
