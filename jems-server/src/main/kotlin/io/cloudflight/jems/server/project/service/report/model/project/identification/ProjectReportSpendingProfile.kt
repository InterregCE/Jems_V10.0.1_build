package io.cloudflight.jems.server.project.service.report.model.project.identification

data class ProjectReportSpendingProfile(
    val lines: List<SpendingProfileLine>,
    val total: SpendingProfileTotal,
)
