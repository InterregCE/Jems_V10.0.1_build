package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract

import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.ProjectReportProcurementSubcontractPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.fillThisReportFlag
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurementSubcontractService(
    private val reportPersistence: ProjectReportPersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
    private val reportProcurementSubcontractPersistence: ProjectReportProcurementSubcontractPersistence,
) {

    @Transactional(readOnly = true)
    fun getSubcontract(
        partnerId: Long,
        reportId: Long,
        procurementId: Long
    ): List<ProjectPartnerReportProcurementSubcontract> {
        // we need to fetch those because of security to make sure those connections really exist
        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)
        val procurement = reportProcurementPersistence.getById(partnerId, procurementId = procurementId)

        return reportProcurementSubcontractPersistence
            .getSubcontractBeforeAndIncludingReportId(procurement.id, reportId = report.id)
            .fillThisReportFlag(currentReportId = reportId)
    }

}
