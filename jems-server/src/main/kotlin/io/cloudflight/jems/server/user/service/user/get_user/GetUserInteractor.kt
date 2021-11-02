package io.cloudflight.jems.server.user.service.user.get_user

import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserSearchRequest
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetUserInteractor {

    fun getUsers(pageable: Pageable, searchRequest: UserSearchRequest?): Page<UserSummary>

    fun getUsersFilteredByPermissions(
        needsToHaveAtLeastOneFrom: Set<UserRolePermission>,
        needsNotToHaveAnyOf: Set<UserRolePermission>,
    ): List<UserSummary>

    fun getUserById(userId: Long): User

}
