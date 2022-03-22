package io.cloudflight.jems.server.project.controller.report.procurement

import io.cloudflight.jems.api.project.dto.report.partner.procurement.UpdateProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportProcurementApi
import io.cloudflight.jems.server.common.toIdNamePairDTO
import io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement.GetProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement.UpdateProjectPartnerReportProcurementInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportProcurementController(
    private val getProcurement: GetProjectPartnerReportProcurementInteractor,
    private val updateProcurement: UpdateProjectPartnerReportProcurementInteractor,
) : ProjectPartnerReportProcurementApi {

    override fun getProcurement(partnerId: Long, reportId: Long) =
        getProcurement.getProcurement(partnerId = partnerId, reportId = reportId).toDto()

    override fun updateProcurement(
        partnerId: Long,
        reportId: Long,
        procurementData: List<UpdateProjectPartnerReportProcurementDTO>
    ) =
        updateProcurement.update(
            partnerId = partnerId,
            reportId = reportId,
            procurementNew = procurementData.toModel(),
        ).toDto()

    override fun getProcurementSelectorList(partnerId: Long, reportId: Long) =
        getProcurement.getProcurementsForSelector(partnerId = partnerId, reportId = reportId)
            .map { it.toIdNamePairDTO() }

}
