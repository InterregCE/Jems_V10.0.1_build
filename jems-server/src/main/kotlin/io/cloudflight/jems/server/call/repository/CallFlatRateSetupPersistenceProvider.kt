package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.call.service.flatrate.model.FlatRateModel
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CallFlatRateSetupPersistenceProvider(
    private val callRepository: CallRepository
) : CallFlatRateSetupPersistence {

    @Transactional
    override fun updateFlatRateSetup(callId: Long, flatRates: Set<FlatRateModel>) =
        getCallOrThrow(callId).updateFlatRateSetup(flatRates.toEntity())

    @Transactional(readOnly = true)
    override fun getFlatRateSetup(callId: Long) =
        getCallOrThrow(callId).flatRateSetup.toModel()

    private fun getCallOrThrow(callId: Long) =
        callRepository.findById(callId).orElseThrow { ResourceNotFoundException("call") }

}
