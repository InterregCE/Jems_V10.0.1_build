package io.cloudflight.jems.server.project.service.report.project

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine

fun projectReportCreated(
    context: Any,
    project: ProjectFull,
    report: ProjectReportModel,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_REPORT_ADDED)
            .project(project)
            .entityRelatedId(entityRelatedId = report.id)
            .description("[${report.projectIdentifier}] Project report PR.${report.reportNumber} added")
            .build()
    )

fun projectReportDeleted(
    context: Any,
    report: ProjectReportModel,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_REPORT_DELETED)
            .project(
                projectId = report.projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description("[${report.projectIdentifier}] ${report.status} project report PR.${report.reportNumber} deleted")
            .build()
    )

fun projectReportSubmitted(
    context: Any,
    projectId: Long,
    report: ProjectReportSubmissionSummary,
    certificates: List<ProjectPartnerReportSubmissionSummary>,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_REPORT_SUBMITTED)
            .project(
                projectId = projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description("[${report.projectIdentifier}]: Project report PR.${report.reportNumber} submitted, certificates included: " +
                certificates.joinToString(separator = ", ", transform = {
                    "${if (it.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP"}${it.partnerNumber}-R.${it.reportNumber}"
                })
            )
            .build()
    )

fun controlCertificateCreated(
    context: Any,
    projectId: Long,
    report: ProjectPartnerReport
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.CONTROL_REPORT_CERTIFICATE_GENERATED).project(
        projectId = projectId,
        customIdentifier = report.identification.projectIdentifier,
        acronym = report.identification.projectAcronym
    ).entityRelatedId(entityRelatedId = report.id)
        .description(
            "A control certificate was generated for partner report R.${report.reportNumber} of partner " +
                "${if (report.identification.partnerRole.isLead) "LP" else "PP"}${report.identification.partnerNumber}"
        )
        .build()
)

fun controlReportCreated(
    context: Any,
    projectId: Long,
    report: ProjectPartnerReport
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.CONTROL_REPORT_EXPORT_GENERATED).project(
        projectId = projectId,
        customIdentifier = report.identification.projectIdentifier,
        acronym = report.identification.projectAcronym
    ).entityRelatedId(entityRelatedId = report.id)
        .description(
            "A control report was generated for partner report R.${report.reportNumber} of partner " +
                "${if (report.identification.partnerRole.isLead) "LP" else "PP"}${report.identification.partnerNumber}"
        )
        .build()
)

fun projectReportStartedVerification(
    context: Any,
    projectId: Long,
    report: ProjectReportSubmissionSummary,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_REPORT_VERIFICATION_ONGOING)
            .project(
                projectId = projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description(
                "[" +
                    report.projectIdentifier +
                    "] Project report R.${report.reportNumber} verification started"
            )
            .build()
    )

fun projectReportFinalizedVerification(
    context: Any,
    projectId: Long,
    report: ProjectReportSubmissionSummary,
    parkedExpenditures: List<ProjectReportVerificationExpenditureLine>
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_REPORT_VERIFICATION_FINALIZED)
            .project(
                projectId = projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description(
                "[" +
                    report.projectIdentifier +
                    "] Project report R.${report.reportNumber} verification was finalised and following expenditure items were parked:  ${
                        getParkedExpendituresDescription(
                            parkedExpenditures
                        )
                    }"
            )
            .build()
    )

fun projectReportVerificationDoneByJs(
    context: Any,
    projectId: Long,
    report: ProjectReportModel,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_REPORT_VERIFICATION_BY_JS_DONE_NOTIFICATION_SENT)
            .project(
                projectId = projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description(
                "[" +
                    report.projectIdentifier +
                    "] Project report R.${report.reportNumber} verification by JS is done"
            )
            .build()
    )

fun verificationCertificateCreated(
    context: Any,
    report: ProjectReportModel
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.PROJECT_REPORT_VERIFICATION_CERTIFICATE_GENERATED)
        .project(report)
        .entityRelatedId(entityRelatedId = report.id)
        .description("A verification certificate was generated for project report PR.${report.reportNumber}")
        .build()
)

private fun getParkedExpendituresDescription(parkedExpenditure: List<ProjectReportVerificationExpenditureLine>): String {
    val expenditureString = StringBuilder()
    parkedExpenditure.forEach {
        expenditureString.append("[${getPartnerName(it.expenditure)}] - item [${getReportNumber(it.expenditure)}], ")
    }

    return expenditureString.toString()
}

private fun getPartnerName(expenditure: ProjectPartnerReportExpenditureItem): String =
    if (expenditure.partnerRole == ProjectPartnerRole.LEAD_PARTNER) {
        "LP" + expenditure.partnerNumber
    } else "PP" + expenditure.partnerNumber

private fun getReportNumber(expenditureLine: ProjectPartnerReportExpenditureItem): String =
    "R${expenditureLine.partnerReportNumber}.${expenditureLine.number}"

