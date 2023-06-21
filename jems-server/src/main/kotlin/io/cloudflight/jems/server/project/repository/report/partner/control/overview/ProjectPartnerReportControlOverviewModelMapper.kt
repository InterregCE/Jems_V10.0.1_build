package io.cloudflight.jems.server.project.repository.report.partner.control.overview

import io.cloudflight.jems.server.project.entity.report.control.overview.PartnerReportControlOverviewEntity
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview

fun PartnerReportControlOverviewEntity.toModel(): ControlOverview =
    ControlOverview(
        startDate = startDate,
        requestsForClarifications = requestsForClarifications,
        receiptOfSatisfactoryAnswers = receiptOfSatisfactoryAnswers,
        endDate = endDate,
        findingDescription = findingDescription,
        followUpMeasuresFromLastReport = followUpMeasuresFromLastReport,
        conclusion = conclusion,
        followUpMeasuresForNextReport = followUpMeasuresForNextReport,
        lastCertifiedReportIdWhenCreation = lastCertifiedReportIdWhenCreation
    )
