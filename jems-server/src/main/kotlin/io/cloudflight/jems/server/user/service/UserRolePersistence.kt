package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface UserRolePersistence {

    fun getById(id: Long): UserRole

    fun findAll(pageable: Pageable): Page<UserRoleSummary>

    fun create(userRole: UserRoleCreate): UserRole

    fun update(userRole: UserRole): UserRole

    fun findUserRoleByName(name: String): Optional<UserRoleSummary>

    fun findById(id: Long): Optional<UserRoleSummary>

}
