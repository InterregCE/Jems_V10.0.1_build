package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.cloudflight.jems.server.user.entity.UserRolePermissionEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionId
import io.cloudflight.jems.server.user.repository.userrole.UserRoleNotFound
import io.cloudflight.jems.server.user.repository.userrole.UserRolePermissionRepository
import io.cloudflight.jems.server.user.repository.userrole.UserRoleRepository
import io.cloudflight.jems.server.user.repository.userrole.toEntity
import io.cloudflight.jems.server.user.repository.userrole.toModel
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class UserRolePersistenceProvider(
    private val userRoleRepo: UserRoleRepository,
    private val userRolePermissionRepo: UserRolePermissionRepository,
    private val programmeDataPersistence: ProgrammeDataPersistence
) : UserRolePersistence {

    @Transactional(readOnly = true)
    override fun getById(id: Long): UserRole {
        val defaultUserRoleId = programmeDataPersistence.getDefaultUserRole()
        val permissions = userRolePermissionRepo.findAllByIdUserRoleId(id).toModel()
        return userRoleRepo.getOne(id).toModel(permissions, defaultUserRoleId)
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<UserRoleSummary> {
        val defaultUserRoleId = programmeDataPersistence.getDefaultUserRole()
        return userRoleRepo.findAll(pageable).toModel(defaultUserRoleId)
    }

    @Transactional
    override fun create(userRole: UserRoleCreate): UserRole {
        val role = userRoleRepo.save(userRole.toEntity())
        val permissions = userRolePermissionRepo.saveAll(userRole.permissions.toEntity(role)).toModel()
        if (userRole.isDefault) {
            programmeDataPersistence.updateDefaultUserRole(role.id)
        }
        val defaultUserRoleId = if (userRole.isDefault) role.id else null
        return role.toModel(permissions, defaultUserRoleId)
    }

    @Transactional
    override fun update(userRole: UserRole): UserRole {
        val role = userRoleRepo.findById(userRole.id).orElseThrow { UserRoleNotFound() }
        role.name = userRole.name

        val existingPermissions = userRolePermissionRepo.findAllByIdUserRoleId(userRole.id).toModel()

        existingPermissions
            .filter { !userRole.permissions.contains(it) }
            .forEach { userRolePermissionRepo.deleteById(UserRolePermissionId(role, it)) }

        userRolePermissionRepo.saveAll(
            userRole.permissions.filter { !existingPermissions.contains(it) }
                .map { UserRolePermissionEntity(UserRolePermissionId(role, it)) }
        )
        var currentDefaultUserRoleId = programmeDataPersistence.getDefaultUserRole()
        if (userRole.isDefault && currentDefaultUserRoleId != userRole.id) {
            programmeDataPersistence.updateDefaultUserRole(role.id)
            currentDefaultUserRoleId = userRole.id
        }

        return role.toModel(userRolePermissionRepo.findAllByIdUserRoleId(role.id).toModel(), currentDefaultUserRoleId)
    }

    @Transactional(readOnly = true)
    override fun findUserRoleByName(name: String): Optional<UserRoleSummary> =
        userRoleRepo.findByName(name).map { it.toModel(null) }

    @Transactional(readOnly = true)
    override fun findById(id: Long): UserRoleSummary =
        userRoleRepo.findById(id).map { it.toModel(null) }
            .orElseThrow { UserRoleNotFound() }

}
