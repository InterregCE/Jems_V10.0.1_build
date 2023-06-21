package io.cloudflight.jems.server.controllerInstitution.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.project.service.model.ProjectSummary

fun controllerInstitutionChanged(
    context: Any,
    controllerInstitution: ControllerInstitution,
    nutsRegion3: Collection<String>
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CONTROLLER_INSTITUTION_CHANGED,
            description = "Control Institution created/modified: " +
                    "ID: ${controllerInstitution.id}, NAME:  ${controllerInstitution.name}, " +
                    "NUTS: ${nutsRegion3}, USERS: ${controllerInstitution.institutionUsers}"
        )
    )

fun institutionPartnerAssignmentsChanged(
    context: Any,
    institutionPartnerUpdatedAssignments: List<InstitutionPartnerAssignment>,
    institutionPartnerRemovedAssignments: List<InstitutionPartnerAssignment>,
    projectResolver: (Long) -> ProjectSummary,
): Set<AuditCandidateEvent> {
    val assignmentsUpdated = institutionPartnerUpdatedAssignments.map {
        it.partnerProjectId to "Assignment of institution to partner changed to:\n" +
                "ProjectID: ${it.partnerProjectId}, PartnerID: ${it.partnerId}, InstitutionID: ${it.institutionId}"
    }
    val assignmentsDeleted = institutionPartnerRemovedAssignments.map {
        it.partnerProjectId to "Assignment of institution to partner changed to:\n" +
                "ProjectID: ${it.partnerProjectId}, PartnerID: ${it.partnerId}, InstitutionID: N/A"
    }

    return assignmentsUpdated.plus(assignmentsDeleted).map { (projectId, description) ->
        AuditCandidateEvent(
            context = context,
            auditCandidate = AuditBuilder(AuditAction.INSTITUTION_PARTNER_ASSIGNMENT_CHANGED)
                .project(projectResolver(projectId))
                .description(description)
                .build()
        )
    }.toSet()
}

fun institutionPartnerAssignmentRemoved(
    context: Any,
    deletedAssignments: List<InstitutionPartnerAssignment>,
    projectResolver: (Long) -> ProjectSummary,
): Set<AuditCandidateEvent> {
    return deletedAssignments.map {
        AuditCandidateEvent(
            context = context,
            auditCandidate = AuditBuilder(AuditAction.INSTITUTION_PARTNER_ASSIGNMENT_DROPPED)
                .project(projectResolver(it.partnerProjectId))
                .description("User ID: 0 User email: System\nInstitutionID: ${it.institutionId} - PartnerID: ${it.partnerId}")
                .build()
        )
    }.toSet()
}
