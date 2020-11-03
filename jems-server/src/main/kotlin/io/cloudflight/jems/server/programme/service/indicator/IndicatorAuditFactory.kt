package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import java.util.stream.Collectors

fun indicatorAdded(identifier: String): AuditCandidate {
    return AuditBuilder(AuditAction.PROGRAMME_INDICATOR_ADDED)
        .description("Programme indicator $identifier has been added")
        .build()
}

fun indicatorEdited(identifier: String, changes: Map<String, Pair<Any?, Any?>>): AuditCandidate {
    val changedString = changes.entries.stream()
        .map { "${it.key} changed from ${it.value.first} to ${it.value.second}" }
        .collect(Collectors.joining(",\n"))

    return AuditBuilder(AuditAction.PROGRAMME_INDICATOR_EDITED)
        .description("Programme indicator $identifier edited:\n$changedString")
        .build()
}
