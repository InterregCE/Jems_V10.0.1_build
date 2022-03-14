package io.cloudflight.jems.server.project.service.report.model.contribution

data class ProjectPartnerReportContributionOverview(
    val public: ProjectPartnerReportContributionRow,
    val automaticPublic: ProjectPartnerReportContributionRow,
    val private: ProjectPartnerReportContributionRow,
    var total: ProjectPartnerReportContributionRow,
)
