package io.cloudflight.jems.server.project.controller.auditAndControl.correction.measure

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureScenarioDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureUpdateDTO
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasureUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(AuditControlCorrectionMeasureMapper::class.java)

fun AuditControlCorrectionMeasure.toDto() = mapper.map(this)
fun ProjectCorrectionProgrammeMeasureUpdateDTO.toModel() = mapper.map(this)

@Mapper
interface AuditControlCorrectionMeasureMapper {
    fun map(model: AuditControlCorrectionMeasure): ProjectCorrectionProgrammeMeasureDTO
    fun map(model: ProjectCorrectionProgrammeMeasureScenario): ProjectCorrectionProgrammeMeasureScenarioDTO
    fun map(dto: ProjectCorrectionProgrammeMeasureUpdateDTO): AuditControlCorrectionMeasureUpdate
}
