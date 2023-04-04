package io.cloudflight.jems.server.project.controller.report.project.resultPrinciple

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.jems.api.project.dto.report.project.projectResults.ProjectReportProjectResultDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.ProjectReportResultPrincipleDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.UpdateProjectReportProjectResultDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.UpdateProjectReportResultPrincipleDTO
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrincipleUpdate
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import java.math.BigDecimal

private val mapper = Mappers.getMapper(ProjectReportResultPrincipleMapper::class.java)

fun ProjectReportResultPrinciple.toDto() = mapper.map(this)
fun ProjectReportProjectResult.toDto() = mapper.map(this)
fun ProjectHorizontalPrinciples.toDto() = mapper.map(this)

fun JemsFileMetadata.toDto() = mapper.map(this)

fun ProjectReportResultPrincipleDTO.toModel() = mapper.map(this)
fun InputProjectHorizontalPrinciples.toModel() = mapper.map(this)
fun UpdateProjectReportResultPrincipleDTO.toModel() = ProjectReportResultPrincipleUpdate(
    projectResults = projectResults.map {
        Pair(it.resultNumber, ProjectReportResultUpdate(it.achievedInReportingPeriod ?: BigDecimal.ZERO, it.description))
    }.toMap(),
    sustainableDevelopmentDescription = sustainableDevelopmentDescription,
    equalOpportunitiesDescription = equalOpportunitiesDescription,
    sexualEqualityDescription = sexualEqualityDescription,
)
fun UpdateProjectReportProjectResultDTO.toModel() = mapper.map(this)

@Mapper
interface ProjectReportResultPrincipleMapper {

    fun map(model: ProjectReportResultPrinciple): ProjectReportResultPrincipleDTO
    fun map(model: ProjectReportProjectResult): ProjectReportProjectResultDTO
    fun map(model: ProjectHorizontalPrinciples): InputProjectHorizontalPrinciples

    fun map(model: JemsFileMetadata): JemsFileMetadataDTO

    fun map(dto: ProjectReportResultPrincipleDTO): ProjectReportResultPrinciple
    fun map(dto: InputProjectHorizontalPrinciples): ProjectHorizontalPrinciples
    fun map(dto: UpdateProjectReportProjectResultDTO): ProjectReportProjectResult
}
