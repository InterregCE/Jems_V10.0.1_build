package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails

import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetailsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingPartnerBankingDetailsService(
    private val bankingDetailsPersistence: ContractingPartnerBankingDetailsPersistence
) {

    @Transactional(readOnly = true)
    fun getBankingDetails(partnerId: Long): ContractingPartnerBankingDetails? {
        return bankingDetailsPersistence.getBankingDetails(partnerId)
    }
}
