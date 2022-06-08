package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods

import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.lumpsum.model.PREPARATION_PERIOD_NUMBER
import io.cloudflight.jems.server.project.service.lumpsum.model.CLOSURE_PERIOD_NUMBER

fun List<ProjectPartnerReportPeriod>.filterOutPreparationAndClosure() = filter {
    it.number != PREPARATION_PERIOD_NUMBER && it.number != CLOSURE_PERIOD_NUMBER
}
