package io.cloudflight.jems.server.project.service.report.project.base

import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel

fun ProjectReportModel.toServiceModel(
    periodResolver: (Int) -> ProjectPeriod,
) = ProjectReport(
    id = id,
    reportNumber = reportNumber,
    status = status,
    linkedFormVersion = linkedFormVersion,
    startDate = startDate,
    endDate = endDate,

    deadlineId = deadlineId,
    type = type,
    periodDetail = periodNumber?.let { periodResolver.invoke(it) },
    reportingDate = reportingDate,

    projectId = projectId,
    projectIdentifier = projectIdentifier,
    projectAcronym = projectAcronym,
    leadPartnerNameInOriginalLanguage = leadPartnerNameInOriginalLanguage,
    leadPartnerNameInEnglish = leadPartnerNameInEnglish,

    createdAt = createdAt,
    firstSubmission = firstSubmission,
    verificationDate = verificationDate,
)

fun ProjectReportModel.toServiceSummaryModel(
    periodResolver: (Int) -> ProjectPeriod?,
) = ProjectReportSummary(
    id = id,
    reportNumber = reportNumber,
    status = status,
    linkedFormVersion = linkedFormVersion,
    startDate = startDate,
    endDate = endDate,

    type = type,
    periodDetail = periodNumber?.let { periodResolver.invoke(it) },
    reportingDate = reportingDate,

    createdAt = createdAt,
    firstSubmission = firstSubmission,
    verificationDate = verificationDate,
    deletable = status.isOpen(),
)
