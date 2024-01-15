package io.cloudflight.jems.server.project.service.report.model.partner.base.create

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf

data class PartnerReportCoFinancing(
    val coFinancing: ProjectPartnerCoFinancingAndContribution,
    val coFinancingSpf: ProjectPartnerCoFinancingAndContributionSpf,
)
