package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail

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
            description = "[${checklist.id}] [${checklist.type}] [${checklist.name}] " +
                    "changed status from [$oldStatus] to [${checklist.status}]"
        )
    )

fun controlChecklistStatusChanged(
    context: Any,
    checklist: ChecklistInstance,
    oldStatus: ChecklistInstanceStatus,
    partner: ProjectPartnerDetail,
    reportId: Long
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_STATUS_CHANGE,
            project = AuditProject(id = partner.projectId.toString()),
            description = "Checklist [${checklist.id}] type [${checklist.type}] name [${checklist.name}] " +
                    "for partner [${if (partner.role.isLead) "LP" else "PP".plus(partner.sortNumber)}] " +
                    "and partner report [R.${reportId}] changed status from [$oldStatus] to [${checklist.status}]"
        )
    )

fun checklistDeleted(
    context: Any,
    checklist: ChecklistInstanceDetail
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.ASSESSMENT_CHECKLIST_DELETED,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "[${checklist.id}] [${checklist.type}] [${checklist.name}] deleted"
        )
    )

fun controlChecklistDeleted(
    context: Any,
    checklist: ChecklistInstanceDetail,
    partnerName: String,
    reportId: Long,
    projectId: Long
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_DELETED,
            project = AuditProject(id = projectId.toString()),
            description = "Checklist [${checklist.id}] type [${checklist.type}] name [${checklist.name}] " +
                    "for partner [${partnerName}] and partner report [R.${reportId}] was deleted"
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