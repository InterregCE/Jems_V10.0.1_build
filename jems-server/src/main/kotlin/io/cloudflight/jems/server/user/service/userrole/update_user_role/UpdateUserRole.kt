package io.cloudflight.jems.server.user.service.userrole.update_user_role

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanUpdateRole
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.userrole.validateUserRoleCommon
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateUserRole(
    private val persistence: UserRolePersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateUserRoleInteractor {

    @CanUpdateRole
    @Transactional
    @ExceptionWrapper(UpdateUserRoleException::class)
    override fun updateUserRole(userRole: UserRole): UserRole {
        validateUserRoleCommon(generalValidator, userRole.name)
        validateUserRoleExists(userRole.id)
        validateUserRoleNameNotTaken(userRole)

        return persistence.update(userRole)
    }

    private fun validateUserRoleExists(userRoleId: Long) {
        if (!persistence.existsById(userRoleId))
            throw UserRoleNotFound()
    }

    private fun validateUserRoleNameNotTaken(userRole: UserRole) {
        val roleWithSameName = persistence.findUserRoleByName(userRole.name)
        if (roleWithSameName.isPresent && roleWithSameName.get().id != userRole.id)
            throw UserRoleNameAlreadyTaken()
    }

}
