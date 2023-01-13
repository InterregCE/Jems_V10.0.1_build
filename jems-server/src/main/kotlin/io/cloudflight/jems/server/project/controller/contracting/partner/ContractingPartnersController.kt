package io.cloudflight.jems.server.project.controller.contracting.partner

import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnersApi
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.server.project.controller.partner.toDto
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
        version: String?
    ): List<ProjectPartnerSummaryDTO> =
        getContractingPartnersInteractor.findAllByProjectIdForContracting(projectId, pageable.sort, version).toDto()

}
