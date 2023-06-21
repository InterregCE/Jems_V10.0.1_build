package io.cloudflight.jems.api.project.dto.report.project.certificate

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal
import java.time.ZonedDateTime

data class PartnerReportCertificateDTO(
    val partnerReportId: Long,
    val partnerReportNumber: Int,

    val partnerId: Long,
    val partnerRole: ProjectPartnerRoleDTO,
    val partnerNumber: Int,

    val totalEligibleAfterControl: BigDecimal,
    val controlEnd: ZonedDateTime,

    val projectReportId: Long?,
    val projectReportNumber: Int?,
    // FE helpers
    val disabled: Boolean,
    val checked: Boolean,
)
