package io.cloudflight.jems.api.project.dto.contracting.partner

import java.time.ZonedDateTime

data class ContractingPartnerStateAidDeMinimisDTO (
    val selfDeclarationSubmissionDate: ZonedDateTime?,
    val baseForGranting: BaseForGrantingDTO?,
    val aidGrantedByCountry: String?,
    val aidGrantedByCountryCode: String?,
    val memberStatesGranting: Set<MemberStateForGrantingDTO>,
    val comment: String?
)
