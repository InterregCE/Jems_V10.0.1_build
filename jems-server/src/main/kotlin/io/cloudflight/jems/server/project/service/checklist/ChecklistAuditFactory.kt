package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus

fun checklistStatusChanged(
    context: Any,
    checklist: ChecklistInstance,
    oldStatus: ChecklistInstanceStatus
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.ASSESSMENT_CHECKLIST_STATUS_CHANGE,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "[" + checklist.id + "] [" + checklist.type + "]" +
                " [" + checklist.name + "]" + " status changed from " + "[" + oldStatus + "]"
                + " to " + "[" + checklist.status + "]"
        )
    )

fun checklistDeleted(
    context: Any,
    checklist: ChecklistInstanceDetail,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.ASSESSMENT_CHECKLIST_DELETED,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "[" + checklist.id + "] [" + checklist.type + "]" +
                " [" + checklist.name + "]" + " deleted"
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
