package io.cloudflight.jems.api.project.dto.lumpsum

import java.time.ZonedDateTime

data class ProjectLumpSumDTO (
    val orderNr: Int,
    val programmeLumpSumId: Long,
    val period: Int? = null,
    val lumpSumContributions: List<ProjectPartnerLumpSumDTO> = emptyList(),
    val fastTrack: Boolean,
    val readyForPayment: Boolean,
    val comment: String?,
    val paymentEnabledDate: ZonedDateTime? = null,
    val lastApprovedVersionBeforeReadyForPayment: String? = null
)
