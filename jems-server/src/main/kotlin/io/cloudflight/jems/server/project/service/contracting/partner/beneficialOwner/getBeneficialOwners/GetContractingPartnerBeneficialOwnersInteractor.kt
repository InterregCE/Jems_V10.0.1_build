package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.getBeneficialOwners

import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner

interface GetContractingPartnerBeneficialOwnersInteractor {
    fun getBeneficialOwners(partnerId: Long): List<ContractingPartnerBeneficialOwner>
}
