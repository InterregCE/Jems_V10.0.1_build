package io.cloudflight.ems.strategy.service

import io.cloudflight.ems.api.strategy.OutputProgrammeStrategy
import io.cloudflight.ems.audit.entity.AuditAction
import io.cloudflight.ems.audit.service.AuditBuilder
import io.cloudflight.ems.audit.service.AuditCandidate
import java.util.stream.Collectors

fun strategyChanged(strategies: List<OutputProgrammeStrategy>): AuditCandidate {
    val changedString = strategies.stream()
        .filter {it.active}
        .map { "${it.strategy}" }
        .collect(Collectors.joining(",\n"))

    return AuditBuilder(AuditAction.PROGRAMME_STRATEGIES_CHANGED)
        .description("Programme strategies was set to:\n$changedString")
        .build()
}
