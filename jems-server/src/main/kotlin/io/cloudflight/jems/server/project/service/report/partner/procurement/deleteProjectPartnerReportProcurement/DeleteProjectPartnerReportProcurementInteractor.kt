package io.cloudflight.jems.server.project.service.report.partner.procurement.deleteProjectPartnerReportProcurement

interface DeleteProjectPartnerReportProcurementInteractor {

    fun delete(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
    )

}
