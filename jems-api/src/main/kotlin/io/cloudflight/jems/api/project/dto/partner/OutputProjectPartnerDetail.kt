package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.OutputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.partner.cofinancing.OutputProjectPartnerCoFinancing

data class OutputProjectPartnerDetail (
    val id: Long?,
    val abbreviation: String,
    val role: ProjectPartnerRole,
    val sortNumber: Int? = null,
    val nameInOriginalLanguage: String?,
    val nameInEnglish: String?,
    val department: String?,
    val addresses: List<OutputProjectPartnerAddress> = emptyList(),
    val contacts: List<OutputProjectPartnerContact> = emptyList(),
    val partnerContribution: OutputProjectPartnerContribution? = null,
    val financing: List<OutputProjectPartnerCoFinancing> = emptyList()
)
