package io.cloudflight.jems.server.project.controller.report.procurement.subcontract

import io.cloudflight.jems.api.project.dto.report.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChangeDTO
import io.cloudflight.jems.api.project.report.procurement.ProjectPartnerReportProcurementSubcontractApi
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract.GetProjectPartnerReportProcurementSubcontractInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.updateProjectPartnerReportProcurementSubcontract.UpdateProjectPartnerReportProcurementSubcontractInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportProcurementSubcontractController(
    private val getSubcontract: GetProjectPartnerReportProcurementSubcontractInteractor,
    private val updateSubcontract: UpdateProjectPartnerReportProcurementSubcontractInteractor,
) : ProjectPartnerReportProcurementSubcontractApi {

    override fun getSubcontractors(partnerId: Long, reportId: Long, procurementId: Long) =
        getSubcontract.getSubcontract(partnerId, reportId = reportId, procurementId = procurementId).toDto()

    override fun updateSubcontractors(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        subcontracts: List<ProjectPartnerReportProcurementSubcontractChangeDTO>,
    ) = updateSubcontract.update(partnerId, reportId = reportId, procurementId = procurementId, subcontracts.toModel()).toDto()

}
