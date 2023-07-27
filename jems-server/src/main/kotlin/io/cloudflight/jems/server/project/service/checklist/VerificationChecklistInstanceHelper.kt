package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import java.time.ZonedDateTime

fun isChecklistCreatedAfterVerification(
    existing: ChecklistInstanceDetail,
    reportVerificationEndDate: ZonedDateTime?
): Boolean {
    if (reportVerificationEndDate == null || existing.createdAt == null) {
        return false
    }
    return existing.createdAt < reportVerificationEndDate
}
