package io.cloudflight.jems.server.programme.service.is_programme_setup_locked

import io.cloudflight.jems.server.call.repository.CallPersistenceProvider
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IsProgrammeSetupLocked(
    private val callPersistence: CallPersistenceProvider,
) : IsProgrammeSetupLockedInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    @ExceptionWrapper(IsProgrammeSetupLockedException::class)
    override fun isLocked(): Boolean =
        callPersistence.hasAnyCallPublished()

}
