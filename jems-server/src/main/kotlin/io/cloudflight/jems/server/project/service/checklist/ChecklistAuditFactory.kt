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

fun projectContractingChecklistStatusChanged(
    context: Any,
    checklist: ChecklistInstance,
    oldStatus: ChecklistInstanceStatus,
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_STATUS_CHANGE,
            project = AuditProject(id = checklist.relatedToId.toString()),
            description = "Checklist ${checklist.id} type ${checklist.type} name ${checklist.name} " +
                    "for contract monitoring changed status from '$oldStatus' to '${checklist.status}'"
        )
    )
}

fun projectControlReportChecklistStatusChanged(
    context: Any,
    checklist: ChecklistInstance,
    oldStatus: ChecklistInstanceStatus,
    partner: ProjectPartnerDetail,
    reportId: Long
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_STATUS_CHANGE,
            project = AuditProject(id = partner.projectId.toString()),
            description = "Checklist ${checklist.id} type ${checklist.type} name ${checklist.name} for partner " +
                "${getPartnerName(partner)} and partner report R.${reportId} changed status from '$oldStatus' to '${checklist.status}'"
        )
    )
}

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

fun projectContractingChecklistDeleted(
    context: Any,
    checklist: ChecklistInstanceDetail,
    projectId: Long,
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_DELETED,
            project = AuditProject(id = projectId.toString()),
            description = "Checklist ${checklist.id} type ${checklist.type} name ${checklist.name} for contract monitoring was deleted"
        )
    )
}

fun projectControlReportChecklistDeleted(
    context: Any,
    checklist: ChecklistInstanceDetail,
    projectId: Long,
    partner: ProjectPartnerDetail,
    reportId: Long
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_DELETED,
            project = AuditProject(id = projectId.toString()),
            description = "Checklist ${checklist.id} type ${checklist.type} name ${checklist.name} for partner " +
                "${getPartnerName(partner)} and partner report R.${reportId} was deleted"
        )
    )
}

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
            description = checklists.joinToString(", ") {
                "[${it.id}] [${it.type}] [${it.name}] set to visibility ${it.visible}"
            }
        )
    )

fun projectVerificationReportChecklistStatusChanged(
    context: Any,
    checklist: ChecklistInstance,
    oldStatus: ChecklistInstanceStatus,
    projectId: Long,
    reportId: Long
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_STATUS_CHANGE,
            project = AuditProject(id = projectId.toString()),
            description = "Checklist ${checklist.id} type ${checklist.type} name ${checklist.name} for project report R.${reportId} changed status from '$oldStatus' to '${checklist.status}'"
        )
    )
}

fun projectVerificationReportChecklistDeleted(
    context: Any,
    checklist: ChecklistInstanceDetail,
    projectId: Long,
    reportId: Long
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_DELETED,
            project = AuditProject(id = projectId.toString()),
            description = "Checklist ${checklist.id} type ${checklist.type} name ${checklist.name} for project report R.${reportId} was deleted"
        )
    )
}

fun projectClosureChecklistStatusChanged(
    context: Any,
    checklist: ChecklistInstance,
    oldStatus: ChecklistInstanceStatus,
    projectId: Long,
    reportNumber: Int
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_STATUS_CHANGE,
            project = AuditProject(id = projectId.toString()),
            description = "Checklist ${checklist.id} type ${checklist.type} name ${checklist.name} " +
                "for project report R.${reportNumber} changed status from '$oldStatus' to '${checklist.status}'"
        )
    )
}

fun projectClosureChecklistDeleted(
    context: Any,
    checklist: ChecklistInstanceDetail,
    projectId: Long,
    reportNumber: Int
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CHECKLIST_DELETED,
            project = AuditProject(id = projectId.toString()),
            description = "Checklist ${checklist.id} type ${checklist.type} name ${checklist.name} for project report R.${reportNumber} was deleted"
        )
    )
}

private fun getPartnerName(partner: ProjectPartnerDetail?): String =
    partner?.role?.isLead.let {
        if (it == true) "LP${partner?.sortNumber}" else "PP${partner?.sortNumber}"
    }
