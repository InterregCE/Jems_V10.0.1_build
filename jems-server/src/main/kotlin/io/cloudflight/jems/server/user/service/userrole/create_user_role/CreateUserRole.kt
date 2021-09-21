package io.cloudflight.jems.server.user.service.userrole.create_user_role

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanCreateRole
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate
import io.cloudflight.jems.server.user.service.userrole.userRoleCreated
import io.cloudflight.jems.server.user.service.userrole.validateUserRoleCommon
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateUserRole(
    private val persistence: UserRolePersistence,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher
) : CreateUserRoleInteractor {

    @CanCreateRole
    @Transactional
    @ExceptionWrapper(CreateUserRoleException::class)
    override fun createUserRole(userRole: UserRoleCreate): UserRole {
        validateUserRoleCommon(generalValidator, userRole.name)
        validateUserRoleNameNotTaken(userRole.name)

        return persistence.create(userRole).also {
            auditPublisher.publishEvent(userRoleCreated(this, it))
        }
    }

    private fun validateUserRoleNameNotTaken(name: String) {
        if (persistence.findUserRoleByName(name).isPresent)
            throw UserRoleNameAlreadyTaken()
    }

}
