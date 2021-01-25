package io.cloudflight.jems.api.project.dto.lumpsum

data class ProjectLumpSumDTO (
    val programmeLumpSumId: Long,
    val period: Int? = null,
    val lumpSumContributions: List<ProjectPartnerLumpSumDTO> = emptyList()
)
