package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment

import io.cloudflight.jems.server.project.service.report.model.file.procurement.ProjectReportProcurementFile

interface ProjectReportProcurementAttachmentPersistence {

    fun getAttachmentsBeforeAndIncludingReportId(procurementId: Long, reportId: Long): List<ProjectReportProcurementFile>

    fun countAttachmentsCreatedUpUntilNow(procurementId: Long, reportId: Long): Long

}
