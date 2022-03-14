package io.cloudflight.jems.api.project.dto.report.partner.contribution

data class ProjectPartnerReportContributionOverviewDTO(
    val public: ProjectPartnerReportContributionRowDTO,
    val automaticPublic: ProjectPartnerReportContributionRowDTO,
    val private: ProjectPartnerReportContributionRowDTO,
    val total: ProjectPartnerReportContributionRowDTO,
)
