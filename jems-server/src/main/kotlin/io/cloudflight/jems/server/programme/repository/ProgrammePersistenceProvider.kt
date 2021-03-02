package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.repository.CallRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.transaction.annotation.Transactional

@NoRepositoryBean
abstract class ProgrammePersistenceProvider(
    private val callRepository: CallRepository,
) {

    @Transactional(readOnly = true)
    open fun isProgrammeSetupRestricted(): Boolean = callRepository.existsByStatus(CallStatus.PUBLISHED)

}
