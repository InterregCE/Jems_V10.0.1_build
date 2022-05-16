package io.cloudflight.jems.server.project.service.report.model.create

import io.cloudflight.jems.server.project.service.report.model.ReportStatus

data class PartnerReportBaseData(
    val partnerId: Long,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,
)
