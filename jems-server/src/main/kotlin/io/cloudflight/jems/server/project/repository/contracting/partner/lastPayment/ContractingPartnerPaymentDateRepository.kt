package io.cloudflight.jems.server.project.repository.contracting.partner.lastPayment

import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingPartnerPaymentDateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContractingPartnerPaymentDateRepository: JpaRepository<ProjectContractingPartnerPaymentDateEntity, Long> {

    fun findAllByPartnerProjectId(projectId: Long): List<ProjectContractingPartnerPaymentDateEntity>

}
