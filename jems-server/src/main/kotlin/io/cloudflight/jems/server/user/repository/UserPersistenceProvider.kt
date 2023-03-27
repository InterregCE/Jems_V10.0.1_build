package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserNotFound
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.user.UserRoleNotFound
import io.cloudflight.jems.server.user.repository.user.toEntity
import io.cloudflight.jems.server.user.repository.user.toModel
import io.cloudflight.jems.server.user.repository.user.toModelWithPassword
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.repository.userrole.UserRolePermissionRepository
import io.cloudflight.jems.server.user.repository.userrole.UserRoleRepository
import io.cloudflight.jems.server.user.repository.userrole.toModel
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserSearchRequest
import io.cloudflight.jems.server.user.service.model.UserSettings
import io.cloudflight.jems.server.user.service.model.UserSettingsChange
import io.cloudflight.jems.server.user.service.model.UserStatus
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
        userRepo.getById(id).let {
            it.toModelWithPassword(permissions = userRolePermissionRepo.findAllByIdUserRoleId(it.userRole.id).toModel())
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

    @Transactional
    override fun getSummaryByEmail(email: String): UserSummary? =
        userRepo.getOneByEmail(email)?.toUserSummary()

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable, userSearchRequest: UserSearchRequest?): Page<UserSummary> {
        val searchPredicate = UserRepository.buildSearchPredicate(searchRequest = userSearchRequest)
        if (searchPredicate == null)
            return userRepo.findAll(pageable).toModel()
        else
            return userRepo.findAll(searchPredicate, pageable).toModel()
    }

    @Transactional(readOnly = true)
    override fun findAllWithRoleIdIn(roleIds: Set<Long>): List<UserSummary> =
        userRepo.findAllByUserRoleIdInOrderByEmail(userRoleIds = roleIds)
            .map { it.toUserSummary() }

    @Transactional(readOnly = true)
    override fun findAllByEmails(emails: Collection<String>): List<UserSummary> =
        userRepo.findAllByEmailInIgnoreCaseOrderByEmail(emails).map { it.toUserSummary() }

    @Transactional(readOnly = true)
    override fun findAllByIds(ids: Iterable<Long>): List<UserSummary> =
        userRepo.findAllById(ids).map { it.toUserSummary() }

    @Transactional
    override fun create(user: UserChange, passwordEncoded: String): User =
        userRepo.save(
            user.toEntity(passwordEncoded = passwordEncoded, role = userRoleRepo.getById(user.userRoleId))
        ).let {
            it.toModel(
                permissions = userRolePermissionRepo.findAllByIdUserRoleId(it.userRole.id).toModel(),
            )
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
            existingUser.userStatus = userStatus
        }
        return existingUser.let {
            it.toModel(
                permissions = userRolePermissionRepo.findAllByIdUserRoleId(it.userRole.id).toModel()
            )
        }
    }

    @Transactional
    override fun updateSetting(userSettings: UserSettingsChange): UserSettings {
        val existingUser = userRepo.findById(userSettings.id).orElseThrow { UserNotFound() }
        existingUser.sendNotificationsToEmail = disableNotificationsIfUserGotInactive(userSettings, existingUser)

        return UserSettings(existingUser.sendNotificationsToEmail)
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


    private fun disableNotificationsIfUserGotInactive(userSetting: UserSettingsChange, savedUser: UserEntity): Boolean {
        return if(savedUser.userStatus == UserStatus.INACTIVE || savedUser.userStatus == UserStatus.UNCONFIRMED) {
            false
        } else
            userSetting.sendNotificationsToEmail
    }
}
