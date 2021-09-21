package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import java.util.stream.Collectors

fun indicatorAdded(context: Any, identifier: String) = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.PROGRAMME_INDICATOR_ADDED)
        .description("Programme indicator $identifier has been added")
        .build(),
)

fun indicatorEdited(context: Any, identifier: String, changes: Map<String, Pair<Any?, Any?>>): AuditCandidateEvent {
    val changedString = changes.entries.stream()
        .map { "${it.key} changed from ${it.value.first} to ${it.value.second}" }
        .collect(Collectors.joining(",\n"))

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROGRAMME_INDICATOR_EDITED)
            .description("Programme indicator $identifier edited:\n$changedString")
            .build()
    )
}
