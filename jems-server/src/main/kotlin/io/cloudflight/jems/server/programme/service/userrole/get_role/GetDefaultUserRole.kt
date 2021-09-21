package io.cloudflight.jems.server.programme.service.userrole.get_role

import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetDefaultUserRole(
    private val persistence: ProgrammeDataPersistence,
) : GetDefaultUserRoleInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    override fun getDefault(): Long? =
        persistence.getDefaultUserRole()

}
