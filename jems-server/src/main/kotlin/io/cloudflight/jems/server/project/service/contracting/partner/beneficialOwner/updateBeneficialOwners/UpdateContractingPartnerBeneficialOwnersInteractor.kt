package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.updateBeneficialOwners

import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner

interface UpdateContractingPartnerBeneficialOwnersInteractor {
    fun updateBeneficialOwners(partnerId: Long,
                               beneficialOwners: List<ContractingPartnerBeneficialOwner>): List<ContractingPartnerBeneficialOwner>
}
