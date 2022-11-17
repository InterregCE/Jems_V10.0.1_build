package io.cloudflight.jems.server.project.repository.contracting.partner.bankingDetails

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBankingDetailsEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ContractingPartnerBankingDetailsRepository: JpaRepository<ProjectContractingPartnerBankingDetailsEntity, Long> {

    fun findByPartnerId(partnerId: Long): ProjectContractingPartnerBankingDetailsEntity?
}