package io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCoFinancingEntity
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import java.math.BigDecimal

fun ReportProjectCertificateCoFinancingEntity.toModel(
    coFinancing: List<ProjectReportCoFinancingEntity>,
) = ReportCertificateCoFinancing(
    totalsFromAF = ReportCertificateCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.total }),
        partnerContribution = partnerContributionTotal,
        publicContribution = publicContributionTotal,
        automaticPublicContribution = automaticPublicContributionTotal,
        privateContribution = privateContributionTotal,
        sum = sumTotal,
    ),
    currentlyReported = ReportCertificateCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.current }),
        partnerContribution = partnerContributionCurrent,
        publicContribution = publicContributionCurrent,
        automaticPublicContribution = automaticPublicContributionCurrent,
        privateContribution = privateContributionCurrent,
        sum = sumCurrent,
    ),
    previouslyReported = ReportCertificateCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.previouslyReported }),
        partnerContribution = partnerContributionPreviouslyReported,
        publicContribution = publicContributionPreviouslyReported,
        automaticPublicContribution = automaticPublicContributionPreviouslyReported,
        privateContribution = privateContributionPreviouslyReported,
        sum = sumPreviouslyReported,
    ),
    previouslyPaid = ReportCertificateCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.previouslyPaid }),
        partnerContribution = BigDecimal.ZERO,
        publicContribution = BigDecimal.ZERO,
        automaticPublicContribution = BigDecimal.ZERO,
        privateContribution = BigDecimal.ZERO,
        sum = coFinancing.sumOf { it.previouslyPaid },
    ),
)
