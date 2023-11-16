package io.cloudflight.jems.server.project.controller.auditAndControl.correction.impact

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.AuditControlCorrectionImpactDTO
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(AuditControlCorrectionImpactMapper::class.java)

fun AuditControlCorrectionImpact.toDto() = mapper.map(this)
fun AuditControlCorrectionImpactDTO.toModel() = mapper.map(this)

@Mapper
interface AuditControlCorrectionImpactMapper {
    fun map(model: AuditControlCorrectionImpact): AuditControlCorrectionImpactDTO
    fun map(dto: AuditControlCorrectionImpactDTO): AuditControlCorrectionImpact
}
