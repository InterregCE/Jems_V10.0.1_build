package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.server.project.service.report.model.file.procurement.ProjectReportProcurementFile

interface GetProjectPartnerReportProcurementAttachmentInteractor {

    fun getAttachment(partnerId: Long, reportId: Long, procurementId: Long): List<ProjectReportProcurementFile>

}
