package io.cloudflight.jems.server.project.service.lumpsum.model

data class ProjectLumpSum (
    val programmeLumpSumId: Long,
    val period: Int? = null,
    val lumpSumContributions: List<ProjectPartnerLumpSum> = emptyList(),
)
