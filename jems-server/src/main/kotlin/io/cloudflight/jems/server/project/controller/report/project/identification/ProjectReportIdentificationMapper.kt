package io.cloudflight.jems.server.project.controller.report.project.identification

import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.UpdateProjectReportIdentificationDTO
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectReportIdentificationMapper::class.java)

fun ProjectReportIdentification.toDto() =
    mapper.map(this)

fun UpdateProjectReportIdentificationDTO.toModel() =
    mapper.map(this)

@Mapper
interface ProjectReportIdentificationMapper {
    fun map(dto: ProjectReportIdentification): ProjectReportIdentificationDTO

    fun map(dto: UpdateProjectReportIdentificationDTO): ProjectReportIdentificationUpdate
}
