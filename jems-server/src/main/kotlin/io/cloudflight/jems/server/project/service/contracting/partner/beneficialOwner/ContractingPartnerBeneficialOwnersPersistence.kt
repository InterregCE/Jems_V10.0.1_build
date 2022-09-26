package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner

interface ContractingPartnerBeneficialOwnersPersistence {
    fun getBeneficialOwners(partnerId: Long): List<ContractingPartnerBeneficialOwner>

    fun updateBeneficialOwners(partnerId: Long, beneficialOwners: List<ContractingPartnerBeneficialOwner>):
        List<ContractingPartnerBeneficialOwner>
}
