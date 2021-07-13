package io.cloudflight.jems.server.programme.service.userrole.update_role

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateDefaultUserRole(
    private val persistence: ProgrammeDataPersistence
) : UpdateDefaultUserRoleInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(UpdateDefaultUserRoleFailed::class)
    override fun update(userRoleId: Long) {
        return persistence.updateDefaultUserRole(userRoleId)
    }
}
