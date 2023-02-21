package io.cloudflight.jems.server.project.repository.report.project

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCoFinancingEntity
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import java.math.BigDecimal

fun PreviouslyProjectReportedCoFinancing.toEntity(
    reportEntity: ProjectReportEntity,
): ReportProjectCertificateCoFinancingEntity {
    return ReportProjectCertificateCoFinancingEntity(
        reportEntity = reportEntity,

        partnerContributionTotal = totalPartner,
        publicContributionTotal = totalPublic,
        automaticPublicContributionTotal = totalAutoPublic,
        privateContributionTotal = totalPrivate,
        sumTotal = totalSum,

        partnerContributionCurrent = BigDecimal.ZERO,
        publicContributionCurrent = BigDecimal.ZERO,
        automaticPublicContributionCurrent = BigDecimal.ZERO,
        privateContributionCurrent = BigDecimal.ZERO,
        sumCurrent = BigDecimal.ZERO,

        partnerContributionPreviouslyReported = previouslyReportedPartner,
        publicContributionPreviouslyReported = previouslyReportedPublic,
        automaticPublicContributionPreviouslyReported = previouslyReportedAutoPublic,
        privateContributionPreviouslyReported = previouslyReportedPrivate,
        sumPreviouslyReported = previouslyReportedSum,
    )
}
