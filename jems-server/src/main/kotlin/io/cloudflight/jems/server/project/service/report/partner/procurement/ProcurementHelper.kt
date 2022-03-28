package io.cloudflight.jems.server.project.service.report.partner.procurement

import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement

fun List<ProjectPartnerReportProcurement>.fillThisReportFlag(currentReportId: Long) = map {
    it.apply {
        createdInThisReport = reportId == currentReportId
    }
}
