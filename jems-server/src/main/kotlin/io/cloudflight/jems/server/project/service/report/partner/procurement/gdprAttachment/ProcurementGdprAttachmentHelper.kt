package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment

import io.cloudflight.jems.server.common.SENSITIVE_FILE_NAME_MAKS
import io.cloudflight.jems.server.common.SENSITIVE_TRANSLATION_MAKS
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile

fun List<ProjectReportProcurementFile>.fillThisReportFlag(currentReportId: Long) = map {
    it.apply {
        createdInThisReport = reportId == currentReportId
    }
}

fun List<ProjectReportProcurementFile>.anonymizeSensitiveData(): List<ProjectReportProcurementFile> =
    this.onEach { procurementFile ->
        procurementFile.anonymize()
    }

fun ProjectReportProcurementFile.anonymize() = apply {
    this.name = SENSITIVE_FILE_NAME_MAKS
    this.description = SENSITIVE_TRANSLATION_MAKS
}

