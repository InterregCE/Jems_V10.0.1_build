package io.cloudflight.jems.api.project.dto.contracting.partner

import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ContractingPartnerStateAidDeMinimisSectionDTO(
    val partnerId: Long?,
    val dateOfGrantingAid: LocalDate?,
    val amountGrantingAid: BigDecimal?,
    val selfDeclarationSubmissionDate: ZonedDateTime?,
    val baseForGranting: BaseForGrantingDTO?,
    val aidGrantedByCountry: String?,
    val memberStatesGranting: Set<MemberStateForGrantingDTO>,
    val comment: String?
)
