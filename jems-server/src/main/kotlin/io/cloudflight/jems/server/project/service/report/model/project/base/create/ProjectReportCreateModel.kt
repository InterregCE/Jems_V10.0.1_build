package io.cloudflight.jems.server.project.service.report.model.project.base.create

import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageCreate

data class ProjectReportCreateModel(
    val reportBase: ProjectReportModel,
    val reportBudget: ProjectReportBudget,
    val workPackages: List<ProjectReportWorkPackageCreate>,
    val targetGroups: List<ProjectRelevanceBenefit>,
    val partners: List<ProjectReportPartnerCreateModel>,
    val results: List<ProjectReportResultCreate>,
    val horizontalPrinciples: ProjectHorizontalPrinciples,
)
