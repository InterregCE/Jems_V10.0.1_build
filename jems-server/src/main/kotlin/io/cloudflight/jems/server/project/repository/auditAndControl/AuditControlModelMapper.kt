package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControlUpdate
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import java.math.BigDecimal



fun ProjectAuditControlUpdate.toCreateModel(projectSummary: ProjectSummary) = ProjectAuditControl(
    id = 0L,
    projectId = projectSummary.id,
    projectCustomIdentifier = projectSummary.customIdentifier,
    status = AuditStatus.Ongoing,
    controllingBody = controllingBody,
    controlType = controlType,
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,
    totalControlledAmount = totalControlledAmount,
    totalCorrectionsAmount = BigDecimal.ZERO,
    comment = comment,
)

fun ProjectAuditControl.toUpdatedModel(newData: ProjectAuditControlUpdate) = ProjectAuditControl(
    id = id,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    status = status,
    totalCorrectionsAmount = totalCorrectionsAmount,
    controllingBody = newData.controllingBody,
    controlType = newData.controlType,
    startDate = newData.startDate,
    endDate = newData.endDate,
    finalReportDate = newData.finalReportDate,
    totalControlledAmount = newData.totalControlledAmount,
    comment = newData.comment,
)

fun  ProjectAuditControl.toEntity() = AuditControlEntity(
    id = id,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    status = status,
    controllingBody = controllingBody,
    controlType = controlType,
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,
    totalControlledAmount = totalControlledAmount,
    totalCorrectionsAmount = totalCorrectionsAmount,
    comment = comment
)


fun AuditControlEntity.toModel() = ProjectAuditControl(
    id = id,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    status = status,
    controllingBody = controllingBody,
    controlType = controlType,
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,
    totalControlledAmount = totalControlledAmount,
    totalCorrectionsAmount = totalCorrectionsAmount,
    comment = comment
)