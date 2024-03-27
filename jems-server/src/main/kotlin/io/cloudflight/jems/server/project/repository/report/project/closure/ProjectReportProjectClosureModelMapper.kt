package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportClosurePrizeEntity

fun List<ProjectReportClosurePrizeEntity>.toModel() = map { entity -> entity.translatedValues.extractField { it.prize } }
