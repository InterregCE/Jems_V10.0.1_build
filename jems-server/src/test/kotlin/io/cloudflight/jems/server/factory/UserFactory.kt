package io.cloudflight.jems.server.factory

import io.cloudflight.jems.server.authentication.model.ADMINISTRATOR
import io.cloudflight.jems.server.authentication.model.APPLICANT_USER
import io.cloudflight.jems.server.authentication.model.PROGRAMME_USER
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionId
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.userrole.UserRolePermissionRepository
import io.cloudflight.jems.server.user.repository.userrole.UserRoleRepository
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserFactory(
    val userRepository: UserRepository,
    val userRoleRepository: UserRoleRepository,
    val userRolePermissionRepository: UserRolePermissionRepository,
    val programmeDataPersistence: ProgrammeDataPersistence,
    val passwordEncoder: PasswordEncoder
) {

    val adminUser: UserEntity = saveAdminUser(ADMINISTRATOR_EMAIL)

    val applicantUser: UserEntity = saveApplicantUser(APPLICANT_USER_EMAIL)

    @Transactional
    fun saveRole(
        roleName: String,
        permissions: List<UserRolePermission> = emptyList(),
        isDefault: Boolean = false
    ): UserRoleEntity {
        var role = userRoleRepository.findByName(roleName).orElse(null)
        if (role != null)
            return role

        role = userRoleRepository.save(UserRoleEntity(0, roleName))
        when {
            permissions.isNotEmpty() -> {
                permissions.map {
                    UserRolePermissionEntity(UserRolePermissionId(role, it))
                }.let { userRolePermissionRepository.saveAll(it) }
            }
            roleName == ADMINISTRATOR -> {
                UserRolePermission.values().map {
                    UserRolePermissionEntity(UserRolePermissionId(role, it))
                }.let { userRolePermissionRepository.saveAll(it) }
            }
            roleName == PROGRAMME_USER -> {
                userRolePermissionRepository.save(
                    UserRolePermissionEntity(UserRolePermissionId(role, UserRolePermission.AuditRetrieve))
                )
            }
        }
        if (isDefault) {
            programmeDataPersistence.updateDefaultUserRole(role.id)
        }

        return role
    }

    @Transactional
    fun saveUser(email: String, role: UserRoleEntity): UserEntity {
        return userRepository.getOneByEmail(email)
            ?: userRepository.save(
                UserEntity(
                    id = 0,
                    email = email,
                    password = passwordEncoder.encode(email),
                    name = email,
                    surname = email,
                    userRole = role,
                    userStatus = UserStatus.ACTIVE
                )
            )
    }

    fun saveAdminUser(email: String): UserEntity {
        val adminRole: UserRoleEntity = saveRole(ADMINISTRATOR)
        return saveUser(email, adminRole)
    }

    fun saveApplicantUser(email: String): UserEntity {
        val programmeRole = saveRole(APPLICANT_USER)
        return saveUser(email, programmeRole)
    }

    companion object {
        const val ADMINISTRATOR_EMAIL = "administrator@email.com"
        const val APPLICANT_USER_EMAIL = "applicant_user@email.com"
    }
}
