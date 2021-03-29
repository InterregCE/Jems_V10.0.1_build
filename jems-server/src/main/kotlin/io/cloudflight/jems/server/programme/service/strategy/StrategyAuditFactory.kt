package io.cloudflight.jems.server.programme.service.strategy

import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import java.util.stream.Collectors

fun strategyChanged(context: Any, strategies: List<OutputProgrammeStrategy>): AuditCandidateEvent {
    val changedString = strategies.stream()
        .filter {it.active}
        .map { "${it.strategy}" }
        .collect(Collectors.joining(",\n"))

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROGRAMME_STRATEGIES_CHANGED,
            description = "Programme strategies was set to:\n$changedString",
        )
    )
}
