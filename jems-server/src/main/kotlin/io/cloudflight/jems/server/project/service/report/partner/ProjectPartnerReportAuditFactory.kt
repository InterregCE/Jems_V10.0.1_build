package io.cloudflight.jems.server.project.service.report.partner

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification

fun partnerReportCreated(
    context: Any,
    project: ProjectFull,
    report: ProjectPartnerReportCreate,
    reportId: Long,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_ADDED)
            .project(project)
            .entityRelatedId(entityRelatedId = reportId)
            .description("[" +
                report.identification.projectIdentifier +
                "] [" +
                (if (report.identification.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                "${report.identification.partnerNumber}" +
                "] Partner report R.${report.baseData.reportNumber} added")
            .build()
    )

fun partnerReportSubmitted(
    context: Any,
    projectId: Long,
    report: ProjectPartnerReportSubmissionSummary,
    isGdprSensitive: Boolean
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_SUBMITTED)
            .project(
                projectId = projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description(
                "[" +
                        report.projectIdentifier +
                        "] [" +
                        (if (report.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                        "${report.partnerNumber}" +
                        "] Partner report R.${report.reportNumber} submitted ${(if (isGdprSensitive) "[Contains sensitive data]" else "")}"
            )
            .build()
    )


fun partnerReportStartedControl(
    context: Any,
    projectId: Long,
    report: ProjectPartnerReportSubmissionSummary,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_CONTROL_ONGOING)
            .project(
                projectId = projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description("[" +
                report.projectIdentifier +
                "] [" +
                (if (report.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                "${report.partnerNumber}" +
                "] Partner report R.${report.reportNumber} control ongoing")
            .build()
    )

fun partnerReportControlFinalized(
    context: Any,
    projectId: Long,
    report: ProjectPartnerReportSubmissionSummary,
    parked: List<ProjectPartnerReportExpenditureVerification>
): AuditCandidateEvent {
    val parkedIds = parked.mapTo(HashSet()) {
        if (it.parkingMetadata != null)
            Pair(it.parkingMetadata.reportOfOriginNumber, it.parkingMetadata.originalExpenditureNumber)
        else
            Pair(report.reportNumber, it.number)
    }
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_CONTROL_FINALIZED)
            .project(
                projectId = projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description(
                "[" + (if (report.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") + "${report.partnerNumber}] " +
                        "Control for partner report R.${report.reportNumber} is finalized" +
                        (if (parkedIds.isNotEmpty()) " and the following items were parked by control: " +
                                parkedIds.joinToString(
                                    separator = ", ",
                                    prefix = "[", postfix = "]",
                                    transform = { "R" + it.first + "." + it.second })
                        else "")
            )
            .build()
    )
}

fun partnerReportDeleted(
    context: Any,
    projectId: Long,
    partnerReport: ProjectPartnerReport,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_DELETED)
            .project(
                projectId = projectId,
                customIdentifier = partnerReport.identification.projectIdentifier,
                acronym = partnerReport.identification.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = partnerReport.id)
            .description("[" +
                        partnerReport.identification.projectIdentifier +
                        "] [" +
                        (if (partnerReport.identification.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                        "${partnerReport.identification.partnerNumber}" +
                        "] Draft partner report R.${partnerReport.reportNumber} deleted"
            )
            .build()
    )

fun partnerReportExpenditureReIncluded(
    context: Any,
    projectId: Long,
    partnerReport: ProjectPartnerReport,
    expenditure: ExpenditureParkingMetadata,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARKED_EXPENDITURE_REINCLUDED)
            .project(
                projectId = projectId,
                customIdentifier = partnerReport.identification.projectIdentifier,
                acronym = partnerReport.identification.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = partnerReport.id)
            .description("Parked expenditure R${expenditure.reportOfOriginNumber}.${expenditure.originalExpenditureNumber} " +
                "was re-included in partner report R.${partnerReport.reportNumber} by partner " +
                (if (partnerReport.identification.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                partnerReport.identification.partnerNumber
            ).build()
    )

fun partnerReportExpenditureDeleted(
    context: Any,
    projectId: Long,
    partnerReport: ProjectPartnerReport,
    expenditure: ExpenditureParkingMetadata,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARKED_EXPENDITURE_DELETED)
            .project(
                projectId = projectId,
                customIdentifier = partnerReport.identification.projectIdentifier,
                acronym = partnerReport.identification.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = partnerReport.id)
            .description("Parked expenditure R${expenditure.reportOfOriginNumber}.${expenditure.originalExpenditureNumber} " +
                "was deleted in partner report R.${partnerReport.reportNumber} by partner " +
                (if (partnerReport.identification.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                partnerReport.identification.partnerNumber
            ).build()
    )
