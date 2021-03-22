package io.cloudflight.jems.server.call.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import java.util.stream.Collectors

fun callCreated(context: Any, call: CallDetail) = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.CALL_ADDED)
        .entityRelatedId(call.id)
        .description("A new call id=${call.id} name='${call.name}' was created as:\n${call.getDiff().onlyNewChanges()}")
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

private fun Map<String, Pair<Any?, Any?>>.onlyNewChanges() = entries.stream()
    .map { "${it.key} set to ${it.changeTo()}" }
    .collect(Collectors.joining(",\n"))

private fun Map<String, Pair<Any?, Any?>>.fromOldToNewChanges(): String {
    if (isEmpty())
        return "(no-change)"

    return entries.stream()
        .map { "${it.key} changed from ${it.changeFrom()} to ${it.changeTo()}" }
        .collect(Collectors.joining(",\n"))
}

private fun Map.Entry<String, Pair<Any?, Any?>>.changeFrom() = value.first?.toAuditString()
private fun Map.Entry<String, Pair<Any?, Any?>>.changeTo() = value.second?.toAuditString()

private fun Any.toAuditString(): String {
    return when (this) {

        is Collection<*> -> if (isEmpty()) "[]" else if(first() is Int || first() is Long) toString() else joinToString(
            separator = "\n  ",
            prefix = "[\n  ",
            transform = { it.toString() }
        ) + "\n]"

        is String -> "'${this}'"

        is Boolean -> if (this) "enabled" else "disabled"

        else -> toString()
    }
}
