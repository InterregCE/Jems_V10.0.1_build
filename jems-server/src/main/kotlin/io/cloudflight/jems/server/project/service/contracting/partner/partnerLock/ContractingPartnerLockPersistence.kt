package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock

interface ContractingPartnerLockPersistence {
    fun isLocked(partnerId: Long): Boolean
    fun getLockedPartners(projectId: Long): Set<Long>
    fun lock(partnerId: Long, projectId: Long)
    fun unlock(partnerId: Long)
}