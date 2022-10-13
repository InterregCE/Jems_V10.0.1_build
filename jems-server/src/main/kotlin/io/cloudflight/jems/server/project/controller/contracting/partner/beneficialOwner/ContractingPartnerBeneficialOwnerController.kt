package io.cloudflight.jems.server.project.controller.contracting.partner.beneficialOwner

import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnerBeneficialOwnerApi
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerBeneficialOwnerDTO
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.getBeneficialOwners.GetContractingPartnerBeneficialOwnersInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.updateBeneficialOwners.UpdateContractingPartnerBeneficialOwnersInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingPartnerBeneficialOwnerController(
    private val updateBeneficialOwnersInteractor: UpdateContractingPartnerBeneficialOwnersInteractor,
    private val getBeneficialOwnersInteractor: GetContractingPartnerBeneficialOwnersInteractor
) : ContractingPartnerBeneficialOwnerApi {

    override fun getBeneficialOwners(projectId: Long, partnerId: Long): List<ContractingPartnerBeneficialOwnerDTO> {
        return getBeneficialOwnersInteractor.getBeneficialOwners(partnerId).toDto()
    }

    override fun updateBeneficialOwners(
        projectId: Long,
        partnerId: Long,
        owners: List<ContractingPartnerBeneficialOwnerDTO>
    ): List<ContractingPartnerBeneficialOwnerDTO> {
        return updateBeneficialOwnersInteractor.updateBeneficialOwners(projectId, partnerId, owners.toModel()).toDto()
    }
}