package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment

import io.cloudflight.jems.server.project.service.report.model.file.procurement.ProjectReportProcurementFile

const val MAX_AMOUNT_OF_ATTACHMENT = 30

fun List<ProjectReportProcurementFile>.fillThisReportFlag(currentReportId: Long) = map {
    it.apply {
        createdInThisReport = reportId == currentReportId
    }
}
