package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosurePrizeEntity

fun List<ProjectReportProjectClosurePrizeEntity>.toModel() = map { it.translatedValues.extractField { it.prize } }
