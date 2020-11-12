package io.cloudflight.jems.server.factory

import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.cloudflight.jems.server.user.repository.UserRepository
import io.cloudflight.jems.server.user.repository.UserRoleRepository
import io.cloudflight.jems.server.authentication.model.ADMINISTRATOR
import io.cloudflight.jems.server.authentication.model.APPLICANT_USER
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class UserFactory(
    val userRepository: UserRepository,
    val userRoleRepository: UserRoleRepository,
    val passwordEncoder: PasswordEncoder
) {

    val adminUser: User = saveAdminUser(ADMINISTRATOR_EMAIL)

    val applicantUser: User = saveApplicantUser(APPLICANT_USER_EMAIL)

    @Transactional
    fun saveRole(roleName: String): UserRole {
        return userRoleRepository.findOneByName(roleName)
            ?: userRoleRepository.save(UserRole(0, roleName))
    }

    @Transactional
    fun saveUser(email: String, role: UserRole): User {
        return userRepository.findOneByEmail(email)
            ?: userRepository.save(
                    User(
                            id = 0,
                            email = email,
                            password = passwordEncoder.encode(email),
                            name = email,
                            surname = email,
                            userRole = role
                    )
            )
    }

    fun saveAdminUser(email: String): User {
        val adminRole: UserRole = saveRole(ADMINISTRATOR)
        return saveUser(email, adminRole)
    }

    fun saveApplicantUser(email: String): User {
        val programmeRole = saveRole(APPLICANT_USER)
        return saveUser(email, programmeRole)
    }

    companion object {
        const val ADMINISTRATOR_EMAIL = "administrator@email.com"
        const val APPLICANT_USER_EMAIL = "applicant_user@email.com"
    }
}
