package io.cloudflight.jems.api.project.dto.lumpsum

data class ProjectLumpSumDTO (
    val programmeLumpSumId: Long,
    val period: Int? = null,
    val lumpSumContributions: List<ProjectPartnerLumpSumDTO> = emptyList(),
    val fastTrack: Boolean,
    val readyForPayment: Boolean,
    val comment: String?
)
