package io.cloudflight.jems.server.call.service

import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import java.util.stream.Collectors

fun callCreated(call: CallDetail) = AuditBuilder(AuditAction.CALL_ADDED)
    .description("A new call id=${call.id} name='${call.name}' was created as:\n${call.getDiff().onlyNewChanges()}")
    .build()

fun callUpdated(oldCall: CallDetail, call: CallDetail): AuditCandidate {
    val changes = call.getDiff(old = oldCall).fromOldToNewChanges()
    val callStatus = if (call.isPublished()) "published" else "not-published"
    return AuditBuilder(AuditAction.CALL_CONFIGURATION_CHANGED)
        .description("Configuration of $callStatus call id=${call.id} name='${call.name}' changed:\n$changes")
        .build()
}

fun callPublished(call: CallSummary) = AuditBuilder(AuditAction.CALL_PUBLISHED)
    .description("Call id=${call.id} '${call.name}' published")
    .build()

private fun Map<String, Pair<Any?, Any?>>.onlyNewChanges() = entries.stream()
    .map { "${it.key} set to ${it.value.second}" }
    .collect(Collectors.joining(",\n"))

private fun Map<String, Pair<Any?, Any?>>.fromOldToNewChanges() =
    if (isEmpty())
        "(no-change)"
    else
        entries.stream()
            .map { "${it.key} changed from ${it.value.first} to ${it.value.second}" }
            .collect(Collectors.joining(",\n"))
