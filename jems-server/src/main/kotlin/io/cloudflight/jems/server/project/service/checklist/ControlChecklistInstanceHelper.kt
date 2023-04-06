package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import java.time.ZonedDateTime

fun isChecklistCreatedAfterControl(
    existing: ChecklistInstanceDetail,
    reportControlEndDate: ZonedDateTime?
): Boolean {
    if (reportControlEndDate == null || existing.createdAt == null) {
        return false
    }
    return existing.createdAt < reportControlEndDate
}
