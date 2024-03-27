package io.cloudflight.jems.server.project.controller.report.project.closure

import io.cloudflight.jems.api.project.dto.report.project.projectClosure.ProjectReportProjectClosureDTO
import io.cloudflight.jems.api.project.dto.report.project.projectClosure.ProjectReportProjectClosurePrizeDTO
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure

fun ProjectReportProjectClosure.toDto() = ProjectReportProjectClosureDTO(
    story = story,
    prizes = prizes.mapIndexed {index, it -> ProjectReportProjectClosurePrizeDTO(it, index.plus(1))  }
)

fun ProjectReportProjectClosureDTO.toModel() = ProjectReportProjectClosure(
    story = story,
    prizes = prizes.map { it.prize }
)

