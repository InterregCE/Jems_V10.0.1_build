package io.cloudflight.jems.server.call.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.common.audit.fromOldToNewChanges
import io.cloudflight.jems.server.common.audit.onlyNewChanges

fun callCreated(context: Any, call: CallDetail) = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.CALL_ADDED)
        .entityRelatedId(call.id)
        .description("A new call id=${call.id} name='${call.name}' for type='${call.type}' project was created as:\n${call.getDiff().onlyNewChanges()}")
        .build()
)

fun callUpdated(context: Any, oldCall: CallDetail, call: CallDetail): AuditCandidateEvent {
    val changes = call.getDiff(old = oldCall).fromOldToNewChanges()
    val callStatus = if (call.isPublished()) "published" else "not-published"

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.CALL_CONFIGURATION_CHANGED)
            .entityRelatedId(call.id)
            .description("Configuration of $callStatus call id=${call.id} name='${call.name}' changed:\n$changes")
            .build()
    )
}

fun callPublished(context: Any, call: CallSummary) = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.CALL_PUBLISHED)
        .entityRelatedId(call.id)
        .description("Call id=${call.id} '${call.name}' published")
        .build()
)
