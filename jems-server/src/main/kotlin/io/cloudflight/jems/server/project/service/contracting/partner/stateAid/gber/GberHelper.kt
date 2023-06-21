package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import org.springframework.stereotype.Service

@Service
class GberHelper(
    private val projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence
) {
    fun getSelectedFundsListForPartner(partnerId: Long, lastApprovedVersion: String): List<ProgrammeFundType?> {
        val selectedPartnerFunds = projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, lastApprovedVersion).finances
                .filter { it.fundType == ProjectPartnerCoFinancingFundTypeDTO.MainFund }
                .map { it.fund?.type }
        val selectedSpfPartnerFunds = projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(partnerId, lastApprovedVersion).finances
                .filter { it.fundType == ProjectPartnerCoFinancingFundTypeDTO.MainFund }
                .map { it.fund?.type }

        return if (selectedPartnerFunds.isNotEmpty()) {
            selectedPartnerFunds
        } else selectedSpfPartnerFunds
    }

    fun getPartnerFunds(partnerId: Long, budgetPerFund: Set<PartnerBudgetPerFund>, lastApprovedVersion: String): Set<PartnerBudgetPerFund> {
        val selectedFundsByPartner = this.getSelectedFundsListForPartner(partnerId, lastApprovedVersion)

        return budgetPerFund.filter { selectedFundsByPartner.contains(it.fund?.type) }.toSet()
    }
}
