package io.cloudflight.jems.server.project.repository.contracting.partner.beneficialOwner

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBeneficialOwnerEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwnersPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingPartnerBeneficialOwnersPersistenceProvider(
    private val beneficialOwnersRepository: ContractingPartnerBeneficialOwnersRepository,
    private val partnerRepository: ProjectPartnerRepository
): ContractingPartnerBeneficialOwnersPersistence {

    @Transactional(readOnly = true)
    override fun getBeneficialOwners(partnerId: Long): List<ContractingPartnerBeneficialOwner> {
        return beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId).toModel()
    }

    @Transactional
    override fun updateBeneficialOwners(
        partnerId: Long,
        beneficialOwners: List<ContractingPartnerBeneficialOwner>
    ): List<ContractingPartnerBeneficialOwner> {
        val projectPartner = partnerRepository.findById(partnerId).orElseThrow { ResourceNotFoundException("project_partner") }
        val existingById = beneficialOwnersRepository
            .findTop10ByProjectPartnerId(partnerId).associateBy { it.id }
        val stayedBeneficialOwnerIds = beneficialOwners.filter { it.id > 0 }.mapTo(HashSet()) { it.id }
        beneficialOwnersRepository.deleteAll(existingById.minus(stayedBeneficialOwnerIds).values)

        beneficialOwners.forEach { newData ->
            existingById[newData.id].let { existing ->
                when {
                    existing != null -> existing.updateWith(newData)
                    else -> beneficialOwnersRepository.save(newData.toEntity(projectPartner))
                }
            }
        }
        return beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId).toModel()
    }

    private fun ProjectContractingPartnerBeneficialOwnerEntity.updateWith(newData: ContractingPartnerBeneficialOwner) {
        firstName = newData.firstName
        lastName = newData.lastName
        birth = newData.birth
        vatNumber = newData.vatNumber
    }
}
