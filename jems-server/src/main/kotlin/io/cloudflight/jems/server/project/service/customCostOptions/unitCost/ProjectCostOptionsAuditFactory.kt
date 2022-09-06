package io.cloudflight.jems.server.project.service.customCostOptions.unitCost

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.model.ProjectSummary

fun projectUnitCostCreated(context: Any, unitCost: ProgrammeUnitCost, project: ProjectSummary) =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROGRAMME_UNIT_COST_ADDED)
            .project(project)
            .entityRelatedId(project.id)
            .description("Project unit cost (id=${unitCost.id}) '${unitCost.name}' has been added")
            .build()
    )

fun projectUnitCostChanged(context: Any, unitCost: ProgrammeUnitCost, project: ProjectSummary) =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROGRAMME_UNIT_COST_CHANGED)
            .project(project)
            .entityRelatedId(project.id)
            .description("Project unit cost (id=${unitCost.id}) '${unitCost.name}' has been changed")
            .build()
    )
