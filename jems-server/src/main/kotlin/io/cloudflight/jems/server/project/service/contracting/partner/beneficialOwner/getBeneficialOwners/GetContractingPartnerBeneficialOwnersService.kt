package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.getBeneficialOwners

import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwnersPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingPartnerBeneficialOwnersService(
    private val beneficialOwnersPersistence: ContractingPartnerBeneficialOwnersPersistence
) {

    @Transactional(readOnly = true)
    fun getBeneficialOwners(partnerId: Long): List<ContractingPartnerBeneficialOwner> =
        beneficialOwnersPersistence.getBeneficialOwners(partnerId)

}
