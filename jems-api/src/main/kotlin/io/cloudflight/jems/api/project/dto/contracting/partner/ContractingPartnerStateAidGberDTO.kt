package io.cloudflight.jems.api.project.dto.contracting.partner

import java.math.BigDecimal

data class ContractingPartnerStateAidGberDTO(
    val aidIntensity: BigDecimal?,
    val locationInAssistedArea: LocationInAssistedAreaDTO?,
    val comment: String?
)
