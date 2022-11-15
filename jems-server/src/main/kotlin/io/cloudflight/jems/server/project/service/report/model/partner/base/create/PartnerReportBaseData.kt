package io.cloudflight.jems.server.project.service.report.model.partner.base.create

import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus

data class PartnerReportBaseData(
    val partnerId: Long,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,
)
