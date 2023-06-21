package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.unlockPartner

interface ContractingPartnerUnlockInteractor {

    fun unlockPartner(partnerId: Long, projectId: Long)
}