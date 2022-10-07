package io.cloudflight.jems.server.project.controller.contracting.partner.bankingDetails

import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnerBankingDetailsApi
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerBankingDetailsDTO
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails.GetContractingPartnerBankingDetailsInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails.UpdateContractingPartnerBankingDetailsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingPartnerBankingDetailsController (
    private val getBankingDetailsInteractor: GetContractingPartnerBankingDetailsInteractor,
    private val updateBankingDetailsInteractor: UpdateContractingPartnerBankingDetailsInteractor
    ) : ContractingPartnerBankingDetailsApi {

    override fun getBankingDetails(projectId: Long, partnerId: Long): ContractingPartnerBankingDetailsDTO? {
        return getBankingDetailsInteractor.getBankingDetails(partnerId)?.toDto()
    }

    override fun updateBankingDetails(
        projectId: Long,
        partnerId: Long,
        bankingDetailsDTO: ContractingPartnerBankingDetailsDTO
    ): ContractingPartnerBankingDetailsDTO? {
        return updateBankingDetailsInteractor.updateBankingDetails(partnerId, projectId, bankingDetailsDTO.toModel()).toDto()
    }
}