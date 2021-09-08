package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.user.repository.user.UserNotFound
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.user.UserRoleNotFound
import io.cloudflight.jems.server.user.repository.user.toEntity
import io.cloudflight.jems.server.user.repository.user.toModel
import io.cloudflight.jems.server.user.repository.user.toModelWithPassword
import io.cloudflight.jems.server.user.repository.userrole.UserRolePermissionRepository
import io.cloudflight.jems.server.user.repository.userrole.UserRoleRepository
import io.cloudflight.jems.server.user.repository.userrole.toModel
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserSearchRequest
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserPersistenceProvider(
    private val userRepo: UserRepository,
    private val userRoleRepo: UserRoleRepository,
    private val userRolePermissionRepo: UserRolePermissionRepository
) : UserPersistence {

    @Transactional(readOnly = true)
    override fun getById(id: Long): UserWithPassword =
        userRepo.getOne(id).let {
            it.toModelWithPassword(userRolePermissionRepo.findAllByIdUserRoleId(it.userRole.id).toModel())
        }

    @Transactional(readOnly = true)
    override fun throwIfNotExists(id: Long) {
        if (!userRepo.existsById(id))
            throw UserNotFound()
    }

    @Transactional(readOnly = true)
    override fun getByEmail(email: String): UserWithPassword? =
        userRepo.getOneByEmail(email)?.let {
            it.toModelWithPassword(userRolePermissionRepo.findAllByIdUserRoleId(it.userRole.id).toModel())
        }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable, userSearchRequest: UserSearchRequest?): Page<UserSummary> =
        userRepo.findAll(pageable, userSearchRequest).toModel()

    @Transactional
    override fun create(user: UserChange, passwordEncoded: String): User =
        userRepo.save(
            user.toEntity(passwordEncoded = passwordEncoded, role = userRoleRepo.getOne(user.userRoleId))
        ).let {
            it.toModel(permissions = userRolePermissionRepo.findAllByIdUserRoleId(it.userRole.id).toModel())
        }

    @Transactional
    override fun update(user: UserChange): User {
        val existingUser = userRepo.findById(user.id).orElseThrow { UserNotFound() }
        with(user) {
            existingUser.email = email
            existingUser.name = name
            existingUser.surname = surname
            if (existingUser.userRole.id != userRoleId)
                existingUser.userRole = userRoleRepo.findById(userRoleId).orElseThrow { UserRoleNotFound() }
        }
        return existingUser.let {
            it.toModel(permissions = userRolePermissionRepo.findAllByIdUserRoleId(it.userRole.id).toModel())
        }
    }

    @Transactional
    override fun updatePassword(userId: Long, encodedPassword: String) {
        userRepo.findById(userId).orElseThrow { UserNotFound() }
            .password = encodedPassword
    }

    @Transactional(readOnly = true)
    override fun userRoleExists(roleId: Long): Boolean =
        userRoleRepo.existsById(roleId)

    @Transactional(readOnly = true)
    override fun emailExists(email: String): Boolean =
        userRepo.existsByEmail(email)

}
