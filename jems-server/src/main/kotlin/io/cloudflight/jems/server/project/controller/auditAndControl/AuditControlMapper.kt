package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ControllingBodyDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ProjectAuditControlUpdateDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData.CorrectionAvailablePartnerDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.CorrectionImpactActionDTO
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(AuditControlMapper::class.java)

fun ProjectAuditControlUpdateDTO.toModel() = AuditControlUpdate(
    controllingBody = ControllingBody.valueOf(controllingBody.name),
    controlType = AuditControlType.valueOf(controlType.name),
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,
    totalControlledAmount = totalControlledAmount,
    comment = comment
)

fun AuditControl.toDto() = AuditControlDTO(
    id = id,
    number = number,
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

fun AuditControlStatus.toDto() = AuditStatusDTO.valueOf(this.name)

fun List<CorrectionAvailablePartner>.toDto() = map { mapper.map(it) }

fun List<CorrectionImpactActionDTO>.toModel() = map { mapper.map(it) }

@Mapper
interface AuditControlMapper {
    fun map(model: CorrectionAvailablePartner): CorrectionAvailablePartnerDTO
    fun map(dto: CorrectionImpactActionDTO): CorrectionImpactAction
}
