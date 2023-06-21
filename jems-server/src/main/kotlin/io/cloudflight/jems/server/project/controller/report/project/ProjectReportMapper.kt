package io.cloudflight.jems.server.project.controller.report.project

import io.cloudflight.jems.api.project.dto.report.project.ProjectReportDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportStatusDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportUpdateDTO
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page
import org.springframework.web.multipart.MultipartFile

fun Page<ProjectReportSummary>.toDto() = map { it.toDto() }
fun ProjectReportSummary.toDto() = mapper.map(this)

fun ProjectReport.toDto() = mapper.map(this)

fun ProjectReportUpdateDTO.toModel() = mapper.map(this)

fun ProjectReportStatus.toDto() = mapper.map(this)

fun MultipartFile.toProjectFile() = ProjectFile(inputStream, originalFilename ?: name, size)

val mapper = Mappers.getMapper(ProjectReportMapper::class.java)

@Mapper
interface ProjectReportMapper {
    fun map(model: ProjectReport): ProjectReportDTO
    fun map(model: ProjectReportSummary): ProjectReportSummaryDTO
    fun map(model: ProjectReportStatus): ProjectReportStatusDTO
    fun map(dto: ProjectReportUpdateDTO): ProjectReportUpdate
}
