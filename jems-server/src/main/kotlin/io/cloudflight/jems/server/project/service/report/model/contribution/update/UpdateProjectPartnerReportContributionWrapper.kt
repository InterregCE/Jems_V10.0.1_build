package io.cloudflight.jems.server.project.service.report.model.contribution.update

data class UpdateProjectPartnerReportContributionWrapper(
    val toBeUpdated: Set<UpdateProjectPartnerReportContributionExisting>,
    val toBeDeletedIds: Set<Long>,
    val toBeCreated: List<UpdateProjectPartnerReportContributionCustom>,
)
