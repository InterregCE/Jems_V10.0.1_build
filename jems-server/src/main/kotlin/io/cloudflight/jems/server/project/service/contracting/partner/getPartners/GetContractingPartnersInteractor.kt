package io.cloudflight.jems.server.project.service.contracting.partner.getPartners

import io.cloudflight.jems.server.project.service.contracting.model.partner.getPartners.ContractingPartnerSummary
import org.springframework.data.domain.Sort

interface GetContractingPartnersInteractor {

    fun findAllByProjectIdForContracting(projectId: Long, sort: Sort): List<ContractingPartnerSummary>
}
