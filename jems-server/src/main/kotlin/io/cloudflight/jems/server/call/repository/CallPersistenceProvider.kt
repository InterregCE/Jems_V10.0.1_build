package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.service.CallPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CallPersistenceProvider(
    private val callRepository: CallRepository
) : CallPersistence {

    @Transactional(readOnly = true)
    override fun hasAnyCallPublished() =
        callRepository.existsByStatus(CallStatus.PUBLISHED)
}
