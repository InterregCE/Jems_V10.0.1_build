package io.cloudflight.jems.server.project.service.report.model.project.certificate

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal
import java.time.ZonedDateTime

data class PartnerReportCertificate(
    val partnerReportId: Long,
    val partnerReportNumber: Int,
    val partnerId: Long,
    val partnerRole: ProjectPartnerRole,
    val partnerNumber: Int,

    val totalEligibleAfterControl: BigDecimal,
    val controlEnd: ZonedDateTime,

    val projectReportId: Long?,
    val projectReportNumber: Int?,
)
