package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetailsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingPartnerBankingDetails(
    private val bankingDetailsPersistence: ContractingPartnerBankingDetailsPersistence
) : GetContractingPartnerBankingDetailsInteractor {

    @CanRetrieveProjectContractingPartner
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetContractingPartnerBankingDetailsException::class)
    override fun getBankingDetails(partnerId: Long): ContractingPartnerBankingDetails? {
        return bankingDetailsPersistence.getBankingDetails(partnerId)
    }
}