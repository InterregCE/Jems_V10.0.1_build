package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetailsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateContractingPartnerBankingDetails(
    private val bankingDetailsPersistence: ContractingPartnerBankingDetailsPersistence
) : UpdateContractingPartnerBankingDetailsInteractor {

    @CanUpdateProjectContractingPartner
    @Transactional
    @ExceptionWrapper(UpdateContractingPartnerBankingDetailsException::class)
    override fun updateBankingDetails(
        partnerId: Long,
        projectId: Long,
        bankingDetails: ContractingPartnerBankingDetails
    ): ContractingPartnerBankingDetails {
        return bankingDetailsPersistence.updateBankingDetails(partnerId, projectId, bankingDetails)
    }
}