package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.audit.fromOldToNewChanges
import io.cloudflight.jems.server.nuts.service.NutsIdentifier
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import java.util.stream.Collectors

fun programmePriorityAdded(programmePriority: ProgrammePriority): AuditCandidate {
    return AuditBuilder(AuditAction.PROGRAMME_PRIORITY_ADDED)
        .description("New programme priority '${programmePriority.code}' '${programmePriority.title}'\nobjective = ${programmePriority.objective}\nspecificObjectives = " +
            "${priorityObjectives(programmePriority)} was created")
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

fun programmePriorityDeleted(context: Any, programmePriority: ProgrammePriority): AuditCandidateEvent {
    return AuditCandidateEvent(context, AuditBuilder(AuditAction.PROGRAMME_PRIORITY_DELETED)
        .description("Programme priority '${programmePriority.code}' '${programmePriority.title}' has been deleted")
        .build())
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

fun programmeFundsChanged(context: Any, funds: Iterable<ProgrammeFund>): AuditCandidateEvent {
    val fundsAsString = funds.asSequence()
        .map { fund -> "[selected=${fund.selected}, " + fund.abbreviation + "]" }.joinToString(",\n")

    return AuditCandidateEvent(context, AuditBuilder(AuditAction.PROGRAMME_FUNDS_CHANGED)
        .description("Programme funds has been set to:\n$fundsAsString")
        .build())
}

fun programmeLegalStatusesChanged(context: Any, statuses: List<ProgrammeLegalStatus>): AuditCandidateEvent {
    val statusesAsString = statuses.asSequence()
        .map { fund ->  fund.description.toString() }.joinToString(",\n")

    return AuditCandidateEvent(context, AuditBuilder(AuditAction.LEGAL_STATUS_EDITED)
        .description("Values for partner legal status set to:\n$statusesAsString")
        .build())
}

fun programmeTypologyErrorsChanged(context: Any, errors: List<TypologyErrors>): AuditCandidateEvent {
    val typologyErrorsAsString = errors.joinToString(",\n") { typologyError -> typologyError.description }

    return AuditCandidateEvent(context, AuditBuilder(AuditAction.PROGRAMME_TYPOLOGY_ERRORS)
        .description("Values for typology errors set to:\n$typologyErrorsAsString")
        .build())
}

fun programmeStateAidsChanged(context: Any, stateAids: List<ProgrammeStateAid>): AuditCandidateEvent {
    val stateAidsAsString = stateAids.asSequence()
        .map { fund -> "[id=${fund.id}, " +
            "name=${fund.name}, " +
            "measure=${fund.measure}, " +
            "abbreviatedName=${fund.abbreviatedName}, " +
            "schemeNumber=${fund.schemeNumber ?: ""}, " +
            "maxIntensity=${fund.maxIntensity}, " +
            "threshold=${fund.threshold}]"
        }.joinToString(",\n")

    return AuditCandidateEvent(context, AuditBuilder(AuditAction.PROGRAMME_STATE_AID_CHANGED)
        .description("Programme State aid was set to:\n$stateAidsAsString")
        .build())
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

fun programmeTranslationFileUploaded(
    context: Any,
    fileName: String
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROGRAMME_TRANSLATION_FILE_UPLOADED)
            .description("Translation file $fileName uploaded")
            .build()
    )

fun lumpSumDeleted(context: Any, lumpSum: ProgrammeLumpSum): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROGRAMME_LUMP_SUM_DELETED)
        .description("Programme lump sum (id=${lumpSum.id}) '${lumpSum.name}' has been deleted")
        .build()
    )

fun lumpSumChangedAudit(context: Any, lumpSum: ProgrammeLumpSum, oldLumpSum: ProgrammeLumpSum): AuditCandidateEvent {
    val changes = lumpSum.getDiff(old = oldLumpSum).fromOldToNewChanges()
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROGRAMME_LUMP_SUM_CHANGED)
            .description("Programme lump sum (id=${lumpSum.id}) '${lumpSum.name}' has been changed: $changes")
            .build()
    )
}

fun unitCostChangedAudit(context: Any, unitCost: ProgrammeUnitCost, oldUnitCost: ProgrammeUnitCost): AuditCandidateEvent {
    val changes = unitCost.getDiff(old = oldUnitCost).fromOldToNewChanges()
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROGRAMME_UNIT_COST_CHANGED)
            .description("Programme unit cost (id=${unitCost.id}) '${unitCost.name}' has been changed: $changes")
            .build()
    )
}

fun unitCostDeleted(context: Any, unitCost: ProgrammeUnitCost): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROGRAMME_UNIT_COST_DELETED)
            .description("Programme unit cost (id=${unitCost.id}) '${unitCost.name}' has been deleted")
            .build()
    )

fun checklistCreated(context: Any, checklist: ProgrammeChecklistDetail): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_IS_CREATED,
            description = "[" + checklist.id + "]" +
                " [" + checklist.type + "]" +
                " [" + checklist.name + "]" + " created"
        )
    )

fun checklistUpdated(context: Any, checklist: ProgrammeChecklistDetail, oldName: String): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.CHECKLIST_IS_CHANGED)
          .description("Checklist [${checklist.id}], type [${checklist.type}], name [${oldName}] changed its name to [${checklist.name}]")
          .build()
    )

fun checklistDeleted(context: Any, checklist: ProgrammeChecklistDetail): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_IS_DELETED,
            description = "[" + checklist.id + "] [" + checklist.type + "]" +
                " [" + checklist.name + "]" + " deleted"
        )
    )

private fun priorityObjectives(programmePriority: ProgrammePriority) =
    programmePriority.specificObjectives.joinToString(",\n") {
        "${it.programmeObjectivePolicy} - Dimensions: ${dimensions(it.dimensionCodes)}"
    }

private fun dimensions(dimensionCodes: Map<ProgrammeObjectiveDimension, List<String>>) =
    dimensionCodes.entries.joinToString(", ") {
            "${it.key.name} (${it.value.joinToString(" ")})"
    }
