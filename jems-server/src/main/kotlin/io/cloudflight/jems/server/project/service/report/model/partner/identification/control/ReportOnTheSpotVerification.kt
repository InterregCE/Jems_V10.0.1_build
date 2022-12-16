package io.cloudflight.jems.server.project.service.report.model.partner.identification.control

import java.time.LocalDate

data class ReportOnTheSpotVerification(
    val id: Long = 0,
    val verificationFrom: LocalDate?,
    val verificationTo: LocalDate?,
    val verificationLocations: Set<ReportLocationOnTheSpotVerification>,
    val verificationFocus: String?
)
