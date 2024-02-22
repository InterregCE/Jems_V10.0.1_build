package io.cloudflight.jems.server.project.controller.report.project.closure

import io.cloudflight.jems.api.project.dto.report.project.projectClosure.ProjectReportProjectClosureDTO
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectReportProjectClosureMapper::class.java)

fun ProjectReportProjectClosure.toDto() = mapper.map(this)

fun ProjectReportProjectClosureDTO.toModel() = mapper.map(this)

@Mapper
interface ProjectReportProjectClosureMapper {

    fun map(model: ProjectReportProjectClosure): ProjectReportProjectClosureDTO

    fun map(dto: ProjectReportProjectClosureDTO): ProjectReportProjectClosure
}
