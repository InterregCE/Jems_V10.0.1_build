package io.cloudflight.jems.server.project.service.report.project.base

import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel

fun ProjectReportModel.toServiceModel(
    periodResolver: (Int) -> ProjectPeriod,
    paymentIdsInstallmentExists: Set<Long> = setOf(),
    paymentToEcIdsReportIncluded: Set<Long> = setOf()
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
    verificationEndDate = verificationEndDate,
    verificationLastReOpenDate = lastVerificationReOpening,

    paymentIdsInstallmentExists = paymentIdsInstallmentExists,
    paymentToEcIdsReportIncluded = paymentToEcIdsReportIncluded,

    finalReport = finalReport
)

fun ProjectReportModel.toServiceSummaryModel(
    periodResolver: (Int) -> ProjectPeriod?,
) = ProjectReportSummary(
    id = id,
    projectId = projectId,
    projectIdentifier = projectIdentifier,
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
    lastReSubmission = lastReSubmission,
    verificationDate = verificationDate,
    verificationEndDate = verificationEndDate,
    deletable = status.isOpenInitially(),
    amountRequested = amountRequested,
    totalEligibleAfterVerification = totalEligibleAfterVerification,

    verificationConclusionJS = null,
    verificationConclusionMA = null,
    verificationFollowup = null
)
