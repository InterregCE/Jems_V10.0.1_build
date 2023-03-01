package io.cloudflight.jems.server.project.controller.contracting.partner.partnerLock

import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnerLockApi
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.getLockedPartners.GetLockedContractingPartnersInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.lockPartner.ContractingPartnerLockInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.unlockPartner.ContractingPartnerUnlockInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingPartnerLockController(
    private val getLockedContractingPartnersInteractor: GetLockedContractingPartnersInteractor,
    private val contractingPartnerLock: ContractingPartnerLockInteractor,
    private val contractingPartnerUnlock: ContractingPartnerUnlockInteractor,
): ContractingPartnerLockApi {

    override fun getLockedPartners(projectId: Long): Set<Long> =
        getLockedContractingPartnersInteractor.getLockedPartners(projectId)


    override fun lock(partnerId: Long, projectId: Long) =
        contractingPartnerLock.lockPartner(partnerId, projectId)

    override fun unlock(partnerId: Long, projectId: Long) =
        contractingPartnerUnlock.unlockPartner(partnerId, projectId)
}