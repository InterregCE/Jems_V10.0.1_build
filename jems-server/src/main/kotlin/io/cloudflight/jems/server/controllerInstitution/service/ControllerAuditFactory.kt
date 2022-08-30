package io.cloudflight.jems.server.controllerInstitution.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment

fun controllerInstitutionChanged(
    context: Any,
    controllerInstitution: ControllerInstitution,
    nutsRegion3: Collection<String>
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CONTROLLER_INSTITUTION_CHANGED,
            description = "Control Institution created/modified: ID: ${controllerInstitution.id}, " +
                "NAME:  ${controllerInstitution.name}, " +
                "NUTS: ${nutsRegion3}, USERS: ${controllerInstitution.institutionUsers}"
        )
    )


fun institutionPartnerAssignmentsChanged(
    context: Any,
    institutionPartnerUpdatedAssignments: List<InstitutionPartnerAssignment>,
    institutionPartnerRemovedAssignments: List<InstitutionPartnerAssignment>
): AuditCandidateEvent {
    val institutionPartnerAssignmentsUpdatedAsString = institutionPartnerUpdatedAssignments.asSequence()
        .map { "ProjectID: ${it.partnerProjectId}, PartnerID: ${it.partnerId}, InstitutionID: ${it.institutionId} " }
        .joinToString(",\n")
    val institutionPartnerAssignmentsRemovedAsString = institutionPartnerRemovedAssignments.asSequence()
        .map { "ProjectID: ${it.partnerProjectId}, Partner:ID ${it.partnerId}, InstitutionID: N/A" }
        .joinToString(",\n")
    val institutionAssignmentsAsString =
        "$institutionPartnerAssignmentsUpdatedAsString,\n$institutionPartnerAssignmentsRemovedAsString"
    return AuditCandidateEvent(
        context = context,
        AuditBuilder(AuditAction.INSTITUTION_PARTNER_ASSIGNMENT_CHANGED).description(
            "Assignment of institution to partner changed to : $institutionAssignmentsAsString"
        ).build()
    )
}

fun institutionPartnerAssignmentRemoved(
    context: Any,
    deletedAssignments: List<InstitutionPartnerAssignment>
): AuditCandidateEvent {
    val deletedAssignmentsString =
        deletedAssignments.joinToString("\n") { "InstitutionID: ${it.institutionId} - PartnerID: ${it.partnerId}" }
    return AuditCandidateEvent(
        context = context,
        AuditBuilder(
            AuditAction.INSTITUTION_PARTNER_ASSIGNMENT_DROPPED
        ).description("User ID: 0 User email: System \n $deletedAssignmentsString").build()
    )
}

