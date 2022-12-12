package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel


fun ProjectReportEntity.toModelSummary(
    periodResolver: (Int) -> ProjectPeriod?,
) = ProjectReportSummary(
    id = id,
    reportNumber = number,
    status = status,
    linkedFormVersion = applicationFormVersion,
    startDate = startDate,
    endDate = endDate,

    type = deadline?.type ?: type,
    periodDetail = (deadline?.periodNumber ?: periodNumber)?.let { periodResolver.invoke(it) },
    reportingDate = deadline?.deadline ?: reportingDate,

    createdAt = createdAt,
    firstSubmission = firstSubmission,
    verificationDate = verificationDate,
    deletable = false,
)

fun ProjectReportEntity.toModel() = ProjectReportModel(
    id = id,
    reportNumber = number,
    status = status,
    linkedFormVersion = applicationFormVersion,
    startDate = startDate,
    endDate = endDate,

    deadlineId = deadline?.id,
    type = deadline?.type ?: type,
    periodNumber = deadline?.periodNumber ?: periodNumber,
    reportingDate = deadline?.deadline ?: reportingDate,

    projectId = projectId,
    projectIdentifier = projectIdentifier,
    projectAcronym = projectAcronym,
    leadPartnerNameInOriginalLanguage = leadPartnerNameInOriginalLanguage,
    leadPartnerNameInEnglish = leadPartnerNameInEnglish,

    createdAt = createdAt,
    firstSubmission = firstSubmission,
    verificationDate = verificationDate,
)

fun ProjectReportModel.toEntity(
    deadlineResolver: (Long) -> ProjectContractingReportingEntity,
) = ProjectReportEntity(
    projectId = projectId,
    number = reportNumber,
    status = status,
    applicationFormVersion = linkedFormVersion,
    startDate = startDate,
    endDate = endDate,

    deadline = deadlineId?.let { deadlineResolver.invoke(it) },
    type = type,
    reportingDate = reportingDate,
    periodNumber = periodNumber,

    projectIdentifier = projectIdentifier,
    projectAcronym = projectAcronym,
    leadPartnerNameInOriginalLanguage = leadPartnerNameInOriginalLanguage,
    leadPartnerNameInEnglish = leadPartnerNameInEnglish,

    createdAt = createdAt,
    firstSubmission = firstSubmission,
    verificationDate = verificationDate,
)
