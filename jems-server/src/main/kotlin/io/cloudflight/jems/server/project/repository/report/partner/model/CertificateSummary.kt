package io.cloudflight.jems.server.project.repository.report.partner.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal
import java.time.Instant

interface CertificateSummary {
    val partnerReportId: Long
    val partnerReportNumber: Int

    val partnerId: Long
    val partnerRole: ProjectPartnerRole
    val partnerNumber: Int

    val totalEligibleAfterControl: BigDecimal
    val controlEnd: Instant
    val projectReportId: Long?
    val projectReportNumber: Int?
}
