package io.cloudflight.jems.server.project.repository.contracting.partner.bankingDetails

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetailsPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails.GetContractingPartnerBankingDetailsPartnerNotFoundException
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails.UpdateContractingPartnerBankingDetailsNotAllowedException
import io.cloudflight.jems.server.project.service.projectContractingPartnerInfoChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingPartnerBankingDetailsPersistenceProvider(
    private val contractingPartnerBankingDetailsRepository: ContractingPartnerBankingDetailsRepository,
    private val partnerRepository: ProjectPartnerRepository,
    private val auditPublisher: ApplicationEventPublisher
) : ContractingPartnerBankingDetailsPersistence {

    @Transactional(readOnly = true)
    override fun getBankingDetails(partnerId: Long): ContractingPartnerBankingDetails? {
        partnerRepository.findById(partnerId).orElseThrow { GetContractingPartnerBankingDetailsPartnerNotFoundException() }
        return contractingPartnerBankingDetailsRepository.findByPartnerId(partnerId)?.toModel()
    }

    @Transactional
    override fun updateBankingDetails(
        partnerId: Long,
        projectId: Long,
        bankingDetails: ContractingPartnerBankingDetails
    ): ContractingPartnerBankingDetails {
        val projectPartner = partnerRepository.findById(partnerId).orElseThrow { ResourceNotFoundException("project_partner") }
        if (projectPartner == null || projectPartner.project.id != projectId) {
            throw UpdateContractingPartnerBankingDetailsNotAllowedException()
        }
        val currentBankingDetails = getBankingDetails(partnerId)

        return contractingPartnerBankingDetailsRepository.save(bankingDetails.toEntity()).toModel().also {
            auditPublisher.publishEvent(projectContractingPartnerInfoChanged(this, projectPartner, currentBankingDetails, bankingDetails))
        }
    }
}