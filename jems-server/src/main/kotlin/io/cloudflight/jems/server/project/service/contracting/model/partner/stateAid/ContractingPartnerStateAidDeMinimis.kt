package io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid

import java.time.ZonedDateTime

data class ContractingPartnerStateAidDeMinimis (
    val selfDeclarationSubmissionDate: ZonedDateTime?,
    val baseForGranting: BaseForGranting?,
    val aidGrantedByCountry: String?,
    val aidGrantedByCountryCode: String?,
    val memberStatesGranting: Set<MemberStateForGranting>,
    val comment: String?,
)
