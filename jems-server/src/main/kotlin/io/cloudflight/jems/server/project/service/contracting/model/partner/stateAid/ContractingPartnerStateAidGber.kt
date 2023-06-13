package io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid

import java.math.BigDecimal

data class ContractingPartnerStateAidGber(
    val aidIntensity: BigDecimal?,
    val locationInAssistedArea: LocationInAssistedArea?,
    val comment: String?
)
