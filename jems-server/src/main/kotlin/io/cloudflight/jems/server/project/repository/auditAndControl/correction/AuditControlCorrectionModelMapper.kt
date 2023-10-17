package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionLine


fun ProjectAuditControlCorrectionEntity.toModel() = ProjectAuditControlCorrection(
    id = id,
    auditControlId = auditControlEntity.id,
    orderNr = orderNr,
    status = status,
    linkedToInvoice = linkedToInvoice,
)

fun ProjectAuditControlCorrectionEntity.toExtendedModel() = ProjectAuditControlCorrectionExtended(
    correction = this.toModel(),
    auditControlNumber = this.auditControlEntity.number,
    projectCustomIdentifier = this.auditControlEntity.projectCustomIdentifier
)

fun ProjectAuditControlCorrection.toLineModel(auditControlNumber: Int, canBeDeleted: Boolean) = ProjectAuditControlCorrectionLine(
    id = id,
    auditControlId = auditControlId,
    orderNr = orderNr,
    status = status,
    linkedToInvoice = linkedToInvoice,
    auditControlNumber = auditControlNumber,
    canBeDeleted = canBeDeleted
)


fun ProjectAuditControlCorrection.toEntity(auditControlResolver: (Long) -> AuditControlEntity) = ProjectAuditControlCorrectionEntity(
    id = id,
    auditControlEntity = auditControlResolver.invoke(auditControlId),
    orderNr = orderNr,
    status = status,
    linkedToInvoice = linkedToInvoice
)



