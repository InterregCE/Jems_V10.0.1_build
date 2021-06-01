package io.cloudflight.jems.server.user.service.userrole.get_user_role

import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetUserRoleInteractor {

    fun getUserRoles(pageable: Pageable): Page<UserRoleSummary>

    fun getUserRoleById(id: Long): UserRole

}
