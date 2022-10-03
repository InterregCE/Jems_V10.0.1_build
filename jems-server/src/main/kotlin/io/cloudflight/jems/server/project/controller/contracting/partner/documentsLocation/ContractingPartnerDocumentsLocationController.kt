package io.cloudflight.jems.server.project.controller.contracting.partner.documentsLocation

import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnerDocumentsLocationApi
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerDocumentsLocationDTO
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.getDocumentsLocation.GetContractingPartnerDocumentsLocationInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.updateDocumentsLocation.UpdateContractingPartnerDocumentsLocationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingPartnerDocumentsLocationController(
    private val getDocumentsLocationInteractor: GetContractingPartnerDocumentsLocationInteractor,
    private val updateDocumentsLocationInteractor: UpdateContractingPartnerDocumentsLocationInteractor
): ContractingPartnerDocumentsLocationApi {

    override fun getDocumentsLocation(projectId: Long, partnerId: Long): ContractingPartnerDocumentsLocationDTO {
        return getDocumentsLocationInteractor.getDocumentsLocation(partnerId).toDto()
    }

    override fun updateDocumentsLocation(
        projectId: Long,
        partnerId: Long,
        documentsLocation: ContractingPartnerDocumentsLocationDTO
    ): ContractingPartnerDocumentsLocationDTO {
        return updateDocumentsLocationInteractor.updateDocumentsLocation(partnerId, documentsLocation.toModel()).toDto()
    }

}
