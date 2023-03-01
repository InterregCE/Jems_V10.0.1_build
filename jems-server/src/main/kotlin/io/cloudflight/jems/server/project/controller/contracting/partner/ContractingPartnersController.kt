package io.cloudflight.jems.server.project.controller.contracting.partner

import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnersApi
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerSummaryDTO
import io.cloudflight.jems.server.project.controller.partner.toContractingDto
import io.cloudflight.jems.server.project.service.contracting.partner.getPartners.GetContractingPartnersInteractor
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingPartnersController(
    private val getContractingPartnersInteractor: GetContractingPartnersInteractor
): ContractingPartnersApi {

    override fun getProjectPartnersForContracting(
        projectId: Long,
        pageable: Pageable,
    ): List<ContractingPartnerSummaryDTO> =
        getContractingPartnersInteractor.findAllByProjectIdForContracting(projectId, pageable.sort).toContractingDto()

}
