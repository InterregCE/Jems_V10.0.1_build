package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails

interface ContractingPartnerBankingDetailsPersistence {

    fun getBankingDetails(partnerId: Long): ContractingPartnerBankingDetails?

    fun updateBankingDetails(partnerId: Long, projectId: Long, bankingDetails: ContractingPartnerBankingDetails): ContractingPartnerBankingDetails
}