package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails

import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails

interface GetContractingPartnerBankingDetailsInteractor {

    fun getBankingDetails(partnerId: Long): ContractingPartnerBankingDetails?
}