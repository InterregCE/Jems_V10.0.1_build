package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

data class ProjectPartnerReportParkedLinked(
    val reportRelatedId: Long,
    val projectRelatedId: Long,
    val lumpSumNumber: Int?,
    var entityStillAvailable: Boolean,
)
