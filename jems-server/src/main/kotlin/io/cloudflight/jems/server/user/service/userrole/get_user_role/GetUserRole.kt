package io.cloudflight.jems.server.user.service.userrole.get_user_role

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanRetrieveRole
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUserRole(private val persistence: UserRolePersistence) : GetUserRoleInteractor {

    @CanRetrieveRole
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetUserRoleException::class)
    override fun getUserRoles(pageable: Pageable): Page<UserRoleSummary> =
        persistence.findAll(pageable)

    @CanRetrieveRole
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetUserRoleException::class)
    override fun getUserRoleById(id: Long): UserRole =
        persistence.getById(id)

}
