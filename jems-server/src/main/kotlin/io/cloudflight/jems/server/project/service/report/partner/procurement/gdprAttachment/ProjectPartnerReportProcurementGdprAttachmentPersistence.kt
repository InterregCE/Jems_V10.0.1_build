package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment

import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile

interface ProjectPartnerReportProcurementGdprAttachmentPersistence {

    fun getGdprAttachmentsBeforeAndIncludingReportId(procurementId: Long, reportId: Long): List<ProjectReportProcurementFile>

    fun countGdprAttachmentsCreatedUpUntilNow(procurementId: Long, reportId: Long): Long
}
