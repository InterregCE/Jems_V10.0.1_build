package io.cloudflight.jems.server.project.service.report.model

import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage

data class ProjectPartnerReportCreate(
    val partnerId: Long,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,

    val identification: PartnerReportIdentificationCreate,
    val workPackages: List<CreateProjectPartnerReportWorkPackage>,
    val targetGroups: List<ProjectRelevanceBenefit>,
)
