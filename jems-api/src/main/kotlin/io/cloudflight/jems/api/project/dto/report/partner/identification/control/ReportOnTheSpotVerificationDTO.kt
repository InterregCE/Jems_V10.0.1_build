package io.cloudflight.jems.api.project.dto.report.partner.identification.control

import java.time.LocalDate

data class ReportOnTheSpotVerificationDTO(
    val id: Long? = null,
    val verificationFrom: LocalDate?,
    val verificationTo: LocalDate?,
    val verificationLocations: Set<ReportLocationOnTheSpotVerificationDTO>,
    val verificationFocus: String?
)
