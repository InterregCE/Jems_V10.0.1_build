package io.cloudflight.jems.server.project.service.report.model.create

import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage

data class ProjectPartnerReportCreate(
    val baseData: PartnerReportBaseData,
    val identification: PartnerReportIdentificationCreate,

    val workPackages: List<CreateProjectPartnerReportWorkPackage>,
    val targetGroups: List<ProjectRelevanceBenefit>,

    val budget: PartnerReportBudget,
)
