package io.cloudflight.jems.api.project.dto.lumpsum

import java.util.UUID

data class ProjectLumpSumDTO (
    val id: UUID? = null,
    val programmeLumpSumId: Long,
    val period: Int? = null,
    val lumpSumContributions: List<ProjectPartnerLumpSumDTO> = emptyList()
)
