package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.getLockedPartners

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.ContractingPartnerLockPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetLockedContractingPartners(
    private val contractingPartnerLockPersistence: ContractingPartnerLockPersistence
): GetLockedContractingPartnersInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetLockedContractingPartnersException::class)
    override fun getLockedPartners(projectId: Long): Set<Long> =
        contractingPartnerLockPersistence.getLockedPartners(projectId)
}