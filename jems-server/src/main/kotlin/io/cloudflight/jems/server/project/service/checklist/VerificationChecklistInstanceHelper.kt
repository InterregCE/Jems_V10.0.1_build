package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import java.time.ZonedDateTime

private fun isChecklistCreatedBeforeVerificationEnd(
    checklistCreationDate: ZonedDateTime?,
    reportVerificationEndDate: ZonedDateTime?
): Boolean {
    if (reportVerificationEndDate == null || checklistCreationDate == null) {
        return false
    }
    return checklistCreationDate < reportVerificationEndDate
}

private fun isChecklistCreatedBeforeVerificationReopening(
    checklistCreationDate: ZonedDateTime?,
    lastVerificationReOpening: ZonedDateTime?
): Boolean {
    if (lastVerificationReOpening == null || checklistCreationDate == null) {
        return false
    }
    return checklistCreationDate < lastVerificationReOpening
}

fun isChecklistCreatedBeforeDateLimits(
    checklistCreationDate: ZonedDateTime?,
    projectReport: ProjectReportModel
): Boolean {
    return isChecklistCreatedBeforeVerificationEnd(checklistCreationDate, projectReport.verificationEndDate) ||
        isChecklistCreatedBeforeVerificationReopening(checklistCreationDate, projectReport.lastVerificationReOpening)
}
