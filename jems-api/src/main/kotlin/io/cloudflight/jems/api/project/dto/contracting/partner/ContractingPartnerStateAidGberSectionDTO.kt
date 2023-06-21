package io.cloudflight.jems.api.project.dto.contracting.partner

import io.cloudflight.jems.api.project.dto.budget.PartnerBudgetPerFundDTO
import io.cloudflight.jems.api.project.dto.partner.NaceGroupLevelDTO
import java.math.BigDecimal
import java.time.LocalDate

data class ContractingPartnerStateAidGberSectionDTO(
    val partnerId: Long?,
    val dateOfGrantingAid: LocalDate?,
    val partnerFunds: Set<PartnerBudgetPerFundDTO>,
    val totalEligibleBudget: BigDecimal,
    val naceGroupLevel: NaceGroupLevelDTO?,

    val aidIntensity: BigDecimal?,
    val locationInAssistedArea: LocationInAssistedAreaDTO?,
    val comment: String?
)
