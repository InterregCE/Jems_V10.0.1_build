package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.setDescriptionToProjectPartnerReportProcurementGdprFile

interface SetDescriptionToProjectPartnerReportProcurementGdprFileInteractor {

    fun setDescription(partnerId: Long, reportId: Long, fileId: Long, procurementId: Long, description: String)

}
