package io.cloudflight.jems.server.project.repository.auditAndControl.correction.programmeMeasure

import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionProgrammeMeasureEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(CorrectionProgrammeMeasureModelMapper::class.java)

fun ProjectCorrectionProgrammeMeasureEntity.toModel() = mapper.map(this)

@Mapper
interface CorrectionProgrammeMeasureModelMapper {

    fun map(entity: ProjectCorrectionProgrammeMeasureEntity): ProjectCorrectionProgrammeMeasure
}


