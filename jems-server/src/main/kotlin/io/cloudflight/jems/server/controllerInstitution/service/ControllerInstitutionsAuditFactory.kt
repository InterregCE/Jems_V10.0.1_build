package io.cloudflight.jems.server.controllerInstitution.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution

fun controllerInstitutionChanged(
    context: Any,
    controllerInstitution: ControllerInstitution,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.CONTROLLER_INSTITUTION_CHANGED,
            description = "Control Institution created/modified: ID: ${controllerInstitution.id}, " +
                "NAME:  ${controllerInstitution.name}, " +
                "NUTS: ${controllerInstitution.institutionNuts}, USERS: ${controllerInstitution.institutionUsers}"
        )
    )
