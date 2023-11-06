package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionProgrammeMeasureDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionProgrammeMeasureScenarioDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionProgrammeMeasureUpdateDTO
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectCorrectionProgrammeMeasureMapper::class.java)

fun ProjectCorrectionProgrammeMeasure.toDto() = mapper.map(this)
fun ProjectCorrectionProgrammeMeasureScenario.toDto() = mapper.map(this)
fun ProjectCorrectionProgrammeMeasureUpdateDTO.toModel() = mapper.map(this)

@Mapper
interface ProjectCorrectionProgrammeMeasureMapper {

    fun map(model: ProjectCorrectionProgrammeMeasure): ProjectCorrectionProgrammeMeasureDTO
    fun map(model: ProjectCorrectionProgrammeMeasureScenario): ProjectCorrectionProgrammeMeasureScenarioDTO
    fun map(dto: ProjectCorrectionProgrammeMeasureUpdateDTO): ProjectCorrectionProgrammeMeasureUpdate
}
