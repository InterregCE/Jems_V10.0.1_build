package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.getProjectPartnerReportProcurementGdprAttachment

import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile

interface GetProjectPartnerReportProcurementGdprAttachmentInteractor {

    fun getGdprAttachment(partnerId: Long, reportId: Long, procurementId: Long): List<ProjectReportProcurementFile>
}
