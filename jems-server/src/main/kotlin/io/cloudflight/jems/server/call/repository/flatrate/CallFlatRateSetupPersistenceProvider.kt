package io.cloudflight.jems.server.call.repository.flatrate

import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CallFlatRateSetupPersistenceProvider(
    private val callRepository: CallRepository,
    private val projectPartnerRepository: ProjectPartnerRepository
) : CallFlatRateSetupPersistence {

    @Transactional
    override fun updateProjectCallFlatRate(callId: Long, flatRates: Set<ProjectCallFlatRate>) =
        getCallOrThrow(callId).updateFlatRateSetup(flatRates.toEntity(callId))

    @Transactional(readOnly = true)
    override fun getProjectCallFlatRate(callId: Long): Set<ProjectCallFlatRate> =
        getCallOrThrow(callId).flatRates.toProjectCallFlatRate()

    @Transactional(readOnly = true)
    override fun getProjectCallFlatRateByPartnerId(partnerId: Long): Set<ProjectCallFlatRate> =
        projectPartnerRepository.findById(partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
            .project.call.flatRates.toProjectCallFlatRate()

    private fun getCallOrThrow(callId: Long) =
        callRepository.findById(callId).orElseThrow { ResourceNotFoundException("call") }

}
