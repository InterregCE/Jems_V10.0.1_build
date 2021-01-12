package io.cloudflight.jems.server.project.service.lumpsum.model

import java.util.UUID

data class ProjectLumpSum(
    val id: UUID? = null,
    val programmeLumpSumId: Long,
    val period: Int,
    val lumpSumContributions: List<ProjectPartnerLumpSum> = emptyList(),
)
