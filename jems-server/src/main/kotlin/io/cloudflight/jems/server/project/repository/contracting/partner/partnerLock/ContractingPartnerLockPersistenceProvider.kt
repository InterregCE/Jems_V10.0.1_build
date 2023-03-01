package io.cloudflight.jems.server.project.repository.contracting.partner.partnerLock

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerLockEntity
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.ContractingPartnerLockPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingPartnerLockPersistenceProvider(
    private val contractingPartnerLockRepository: ContractingPartnerLockRepository
): ContractingPartnerLockPersistence {

    @Transactional(readOnly = true)
    override fun isLocked(partnerId: Long): Boolean =
        contractingPartnerLockRepository.findById(partnerId).isPresent

    @Transactional(readOnly = true)
    override fun getLockedPartners(projectId: Long): Set<Long> =
        contractingPartnerLockRepository.findAllByProjectId(projectId).map { it.partnerId }.toSet()

    @Transactional
    override fun lock(partnerId: Long, projectId: Long) {
        contractingPartnerLockRepository.save(
            ProjectContractingPartnerLockEntity(partnerId = partnerId, projectId = projectId)
        )
    }

    @Transactional
    override fun unlock(partnerId: Long) {
        contractingPartnerLockRepository.deleteById(partnerId)
    }
}