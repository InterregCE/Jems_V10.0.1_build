package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import java.math.BigDecimal

fun AuditControlUpdate.toCreateModel(sortNumber: Int) = AuditControlCreate(
    number = sortNumber,
    status = AuditControlStatus.Ongoing,
    controllingBody = controllingBody,
    controlType = controlType,
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,
    totalControlledAmount = totalControlledAmount,
    comment = comment,
)

fun AuditControlCreate.toEntity(project: ProjectEntity) = AuditControlEntity(
    project = project,
    number = number,
    status = status,
    controllingBody = controllingBody,
    controlType = controlType,
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,
    totalControlledAmount = totalControlledAmount,
    comment = comment
)

fun AuditControlEntity.toModel(
    totalCorrectionsResolver: (Long) -> BigDecimal = { BigDecimal.ZERO }
) = AuditControl(
    id = id,
    number = number,

    projectId = project.id,
    projectCustomIdentifier = project.customIdentifier,
    projectAcronym = project.acronym,

    status = status,
    controllingBody = controllingBody,
    controlType = controlType,
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,

    totalControlledAmount = totalControlledAmount,
    totalCorrectionsAmount = totalCorrectionsResolver(id),

    comment = comment,
)
