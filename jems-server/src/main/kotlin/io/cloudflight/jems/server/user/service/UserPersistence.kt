package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserSearchRequest
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserPersistence {

    fun getById(id: Long): UserWithPassword

    fun throwIfNotExists(id: Long)

    fun getByEmail(email: String): UserWithPassword?

    fun findAll(pageable: Pageable, userSearchRequest: UserSearchRequest?): Page<UserSummary>

    fun findAllWithRoleIdIn(roleIds: Set<Long>): List<UserSummary>

    fun findAllByIds(ids: Iterable<Long>): List<UserSummary>

    fun create(user: UserChange, passwordEncoded: String): User

    fun update(user: UserChange): User

    fun updatePassword(userId: Long, encodedPassword: String)

    fun userRoleExists(roleId: Long): Boolean

    fun emailExists(email: String): Boolean

}
