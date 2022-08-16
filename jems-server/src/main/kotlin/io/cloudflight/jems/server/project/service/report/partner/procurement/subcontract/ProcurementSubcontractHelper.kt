package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract

import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract

const val MAX_AMOUNT_OF_SUBCONTRACT = 50

fun List<ProjectPartnerReportProcurementSubcontract>.fillThisReportFlag(currentReportId: Long) = map {
    it.apply {
        createdInThisReport = reportId == currentReportId
    }
}
