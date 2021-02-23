package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.nuts.service.NutsIdentifier
import io.cloudflight.jems.server.programme.entity.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import java.util.stream.Collectors

fun programmePriorityAdded(programmePriority: ProgrammePriority): AuditCandidate {
    return AuditBuilder(AuditAction.PROGRAMME_PRIORITY_ADDED)
        .description("New programme priority '${programmePriority.code}' '${programmePriority.title}' was created")
        .build()
}

fun programmePriorityUpdated(oldPriority: ProgrammePriority, changes: Map<String, Pair<Any?, Any?>>): AuditCandidate {
    val changedString = changes.entries.stream()
        .map { "${it.key} changed from ${it.value.first} to ${it.value.second}" }
        .collect(Collectors.joining(",\n"))

    return AuditBuilder(AuditAction.PROGRAMME_PRIORITY_UPDATED)
        .description("Programme priority data changed for '${oldPriority.code}' '${oldPriority.title}':\n$changedString")
        .build()
}

fun programmeBasicDataChanged(changes: Map<String, Pair<Any?, Any?>>): AuditCandidate {
    val changedString = changes.entries.stream()
        .map { "${it.key} changed from ${it.value.first} to ${it.value.second}" }
        .collect(Collectors.joining(",\n"))

    return AuditBuilder(AuditAction.PROGRAMME_BASIC_DATA_EDITED)
        .description("Programme basic data changed:\n$changedString")
        .build()
}

fun programmeNutsAreaChanged(updatedNuts: Collection<NutsIdentifier>): AuditCandidate {
    val changes = updatedNuts.stream()
        .map { it.toString() }
        .collect(Collectors.joining(",\n"))

    return AuditBuilder(AuditAction.PROGRAMME_NUTS_AREA_CHANGED)
        .description("The programme area has been set to:\n$changes")
        .build()
}

fun programmeFundsChanged(funds: Iterable<ProgrammeFundEntity>): AuditCandidate {
    val fundsAsString = funds.asSequence()
        .filter { it.selected }
        .map { it.abbreviation }.joinToString(",\n")

    return AuditBuilder(AuditAction.PROGRAMME_FUNDS_CHANGED)
        .description("Programme funds has been set to:\n$fundsAsString")
        .build()
}

fun programmeLegalStatusesChanged(statuses: List<ProgrammeLegalStatus>): AuditCandidate {
    val statusesAsString = statuses.asSequence()
        .map { "[" + it.translatedValues.joinToString { "${it.language}=${it.description}" } + "]" }.joinToString(",\n")

    return AuditBuilder(AuditAction.LEGAL_STATUS_EDITED)
        .description("Values for partner legal status set to:\n$statusesAsString")
        .build()
}

fun programmeUILanguagesChanged(languages: Iterable<ProgrammeLanguage>): AuditCandidate {
    val systemLanguagesAsString = languages.asSequence()
        .filter { it.ui }
        .map { it.code }.joinToString(", ")

    return AuditBuilder(AuditAction.PROGRAMME_UI_LANGUAGES_CHANGED)
        .description("Programme UI languages available set to:\n$systemLanguagesAsString")
        .build()
}

fun programmeInputLanguagesChanged(languages: Iterable<ProgrammeLanguage>): AuditCandidate {
    val inputLanguagesAsString = languages.asSequence()
        .filter { it.input }
        .map { it.code }.joinToString(", ")

    return AuditBuilder(AuditAction.PROGRAMME_INPUT_LANGUAGES_CHANGED)
        .description("Programme INPUT languages set to:\n$inputLanguagesAsString")
        .build()
}
