package io.cloudflight.jems.server.project.repository.contracting.partner.partnerLock

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerLockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContractingPartnerLockRepository: JpaRepository<ProjectContractingPartnerLockEntity, Long>{

    fun findAllByProjectId(projectId: Long): List<ProjectContractingPartnerLockEntity>

}