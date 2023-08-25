package io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid

import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ContractingPartnerStateAidDeMinimisSection(
    val partnerId: Long,
    val dateOfGrantingAid: LocalDate?,
    val amountGrantingAid: BigDecimal?,
    val selfDeclarationSubmissionDate: ZonedDateTime?,
    val baseForGranting: BaseForGranting?,
    val aidGrantedByCountry: String?,
    val memberStatesGranting: Set<MemberStateForGranting>,
    val comment: String?
)
