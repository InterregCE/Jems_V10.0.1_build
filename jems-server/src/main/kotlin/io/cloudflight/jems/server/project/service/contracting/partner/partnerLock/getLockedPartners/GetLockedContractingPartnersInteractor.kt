package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.getLockedPartners

interface GetLockedContractingPartnersInteractor {
    fun getLockedPartners(projectId: Long): Set<Long>
}