package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.lockPartner

interface ContractingPartnerLockInteractor {

    fun lockPartner(partnerId: Long, projectId: Long)
}