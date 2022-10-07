package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails

import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails

interface UpdateContractingPartnerBankingDetailsInteractor {

    fun updateBankingDetails(partnerId: Long, projectId: Long, bankingDetails: ContractingPartnerBankingDetails): ContractingPartnerBankingDetails
}