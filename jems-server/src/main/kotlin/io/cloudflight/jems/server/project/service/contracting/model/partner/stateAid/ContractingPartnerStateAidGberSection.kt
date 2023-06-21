package io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid

import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import java.math.BigDecimal
import java.time.LocalDate

data class ContractingPartnerStateAidGberSection(
    val partnerId: Long,
    val dateOfGrantingAid: LocalDate?,
    val partnerFunds: Set<PartnerBudgetPerFund>,
    val totalEligibleBudget: BigDecimal,
    val naceGroupLevel: NaceGroupLevel?,

    val aidIntensity: BigDecimal?,
    val locationInAssistedArea: LocationInAssistedArea?,
    val comment: String?
)
