package io.cloudflight.jems.server.project.repository.contracting.partner.beneficialOwner

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBeneficialOwnerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContractingPartnerBeneficialOwnersRepository: JpaRepository<ProjectContractingPartnerBeneficialOwnerEntity, Long> {

    fun findTop10ByProjectPartnerId(projectPartnerId: Long): MutableList<ProjectContractingPartnerBeneficialOwnerEntity>
}
