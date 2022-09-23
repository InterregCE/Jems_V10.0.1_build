package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel

fun checklistStatusChanged(
    context: Any,
    checklist: ChecklistInstance,
    oldStatus: ChecklistInstanceStatus,
    author: Long
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.ASSESSMENT_CHECKLIST_STATUS_CHANGE,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "Checklist [${checklist.id}] type [${checklist.type}] name [${checklist.name}] " +
                    "changed status from [$oldStatus] to [${checklist.status}] by [$author]"
        )
    )

fun controlChecklistStatusChanged(
    context: Any,
    checklist: ChecklistInstance,
    oldStatus: ChecklistInstanceStatus,
    author: Long,
    partnerId: Long
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CONTROL_CHECKLIST_STATUS_CHANGE,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "Checklist [${checklist.id}] type [${checklist.type}] name [${checklist.name}] " +
                    "for [${partnerId}] and [${checklist.relatedToId}] changed status from [$oldStatus] to [${checklist.status}] by [$author]"
        )
    )

fun checklistCreated(
    context: Any,
    checklist: CreateChecklistInstanceModel,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_IS_CREATED,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "Checklist with ID [${checklist.programmeChecklistId}] created"
        )
    )

fun checklistDeleted(
    context: Any,
    checklist: ChecklistInstanceDetail,
    author: Long
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.ASSESSMENT_CHECKLIST_DELETED,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "Checklist [${checklist.id}] type [${checklist.type}] name [${checklist.name}] was deleted by [$author]"
        )
    )

fun controlChecklistDeleted(
    context: Any,
    checklist: ChecklistInstanceDetail,
    author: Long,
    partnerId: Long,
    reportId: Long
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CONTROL_CHECKLIST_DELETED,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "Checklist [${checklist.id}] type [${checklist.type}] name [${checklist.name}] " +
                    "for [${partnerId}] and [${reportId}] was deleted by [$author]"
        )
    )

fun checklistConsolidated(
    context: Any,
    checklist: ChecklistInstance,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.ASSESSMENT_CHECKLIST_CONSOLIDATION_CHANGE,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "[${checklist.id}] [${checklist.type}] [${checklist.name}] consolidation set to ${checklist.consolidated}"
        )
    )

fun checklistSelectionUpdate(
    context: Any,
    checklists: List<ChecklistInstance>
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.ASSESSMENT_CHECKLIST_VISIBILITY_CHANGE,
            project = AuditProject(id = checklists[0].relatedToId.toString()),
            description = checklists.joinToString (", ") {
                "[${it.id}] [${it.type}] [${it.name}] set to visibility ${it.visible}"
            }
        )
    )