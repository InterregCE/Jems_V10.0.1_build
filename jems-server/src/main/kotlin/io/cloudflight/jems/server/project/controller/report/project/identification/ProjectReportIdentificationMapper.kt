package io.cloudflight.jems.server.project.controller.report.project.identification

import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.UpdateProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator.ProjectReportOutputIndicatorOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator.ProjectReportOutputLineOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator.ProjectReportResultIndicatorOverviewDTO
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import java.math.BigDecimal

private val mapper = Mappers.getMapper(ProjectReportIdentificationMapper::class.java)

fun ProjectReportIdentification.toDto() = mapper.map(this)
fun UpdateProjectReportIdentificationDTO.toModel() = mapper.map(this)
fun ProjectReportOutputLineOverview.toDto() = mapper.map(this)

fun Map<ProjectReportResultIndicatorOverview, Map<ProjectReportOutputIndicatorOverview, List<ProjectReportOutputLineOverview>>>.toResultDto() =
    map { (resultIndicator, outputIndicators) ->
        ProjectReportResultIndicatorOverviewDTO(
            id = resultIndicator.id,
            identifier = resultIndicator.identifier,
            name = resultIndicator.name,
            measurementUnit = resultIndicator.measurementUnit,
            baseline = resultIndicator.baseline ?: BigDecimal.ZERO,
            targetValue = resultIndicator.targetValue,
            previouslyReported = resultIndicator.previouslyReported,
            currentReport = resultIndicator.currentReport,
            outputOverviews = outputIndicators.toOutputDto()
        )
    }

fun Map<ProjectReportOutputIndicatorOverview, List<ProjectReportOutputLineOverview>>.toOutputDto() = map { (outputIndicator, outputs) ->
    ProjectReportOutputIndicatorOverviewDTO(
        id = outputIndicator.id,
        identifier = outputIndicator.identifier,
        name = outputIndicator.name,
        measurementUnit = outputIndicator.measurementUnit,
        targetValue = outputIndicator.targetValue,
        previouslyReported = outputIndicator.previouslyReported,
        currentReport = outputIndicator.currentReport,
        outputs = outputs.map { it.toDto() },
    )
}

@Mapper
interface ProjectReportIdentificationMapper {
    fun map(model: ProjectReportIdentification): ProjectReportIdentificationDTO
    fun map(dto: UpdateProjectReportIdentificationDTO): ProjectReportIdentificationUpdate
    fun map(model: ProjectReportOutputLineOverview): ProjectReportOutputLineOverviewDTO
}
