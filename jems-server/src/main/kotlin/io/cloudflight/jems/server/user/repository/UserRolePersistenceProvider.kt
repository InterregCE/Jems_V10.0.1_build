package io.cloudflight.jems.server.user.repository

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
) : UserRolePersistence {

    @Transactional(readOnly = true)
    override fun getById(id: Long): UserRole {
        val permissions = userRolePermissionRepo.findAllByIdUserRoleId(id).toModel()
        return userRoleRepo.getOne(id).toModel(permissions)
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<UserRoleSummary> =
        userRoleRepo.findAll(pageable).toModel()

    @Transactional
    override fun create(userRole: UserRoleCreate): UserRole {
        val role = userRoleRepo.save(userRole.toEntity())
        val permissions = userRolePermissionRepo.saveAll(userRole.permissions.toEntity(role)).toModel()
        return role.toModel(permissions)
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

        return role.toModel(userRolePermissionRepo.findAllByIdUserRoleId(role.id).toModel())
    }

    @Transactional(readOnly = true)
    override fun findUserRoleByName(name: String): Optional<UserRoleSummary> =
        userRoleRepo.findByName(name).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun existsById(id: Long): Boolean =
        userRoleRepo.existsById(id)

}
