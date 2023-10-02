package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ControllingBodyDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ProjectAuditControlUpdateDTO
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControlUpdate


fun ProjectAuditControlUpdateDTO.toModel() = ProjectAuditControlUpdate(
    controllingBody = ControllingBody.valueOf(controllingBody.name),
    controlType = AuditControlType.valueOf(controlType.name),
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,
    totalControlledAmount = totalControlledAmount,
    comment = comment
)

fun ProjectAuditControl.toDto() = AuditControlDTO(
    id = id,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    status = AuditStatusDTO.valueOf(status.name),
    controllingBody = ControllingBodyDTO.valueOf(controllingBody.name),
    controlType = AuditControlTypeDTO.valueOf(controlType.name),
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,
    totalControlledAmount = totalControlledAmount,
    totalCorrectionsAmount = totalCorrectionsAmount,
    comment = comment
)
