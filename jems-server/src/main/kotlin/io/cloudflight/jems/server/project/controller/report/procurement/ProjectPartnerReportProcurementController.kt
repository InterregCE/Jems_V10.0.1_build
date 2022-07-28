package io.cloudflight.jems.server.project.controller.report.procurement

import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementChangeDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportProcurementApi
import io.cloudflight.jems.server.common.toIdNamePairDTO
import io.cloudflight.jems.server.project.service.report.partner.procurement.createProjectPartnerReportProcurement.CreateProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.deleteProjectPartnerReportProcurement.DeleteProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement.GetProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement.UpdateProjectPartnerReportProcurementInteractor
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportProcurementController(
    private val getProcurement: GetProjectPartnerReportProcurementInteractor,
    private val updateProcurement: UpdateProjectPartnerReportProcurementInteractor,
    private val createProcurement: CreateProjectPartnerReportProcurementInteractor,
    private val deleteProcurement: DeleteProjectPartnerReportProcurementInteractor,
) : ProjectPartnerReportProcurementApi {

    override fun getProcurement(partnerId: Long, reportId: Long, pageable: Pageable) =
        getProcurement.getProcurement(partnerId = partnerId, reportId = reportId, pageable).toDto()

    override fun getProcurementById(partnerId: Long, reportId: Long, procurementId: Long) =
        getProcurement.getProcurementById(partnerId = partnerId, reportId = reportId, procurementId).toDto()

    override fun addNewProcurement(partnerId: Long, reportId: Long, procurementData: ProjectPartnerReportProcurementChangeDTO) =
        createProcurement.create(
            partnerId = partnerId,
            reportId = reportId,
            procurementData = procurementData.toModel(),
        ).toDto()

    override fun updateProcurement(partnerId: Long, reportId: Long, procurementData: ProjectPartnerReportProcurementChangeDTO) =
        updateProcurement.update(
            partnerId = partnerId,
            reportId = reportId,
            procurementData = procurementData.toModel(),
        ).toDto()

    override fun deleteProcurement(partnerId: Long, reportId: Long, procurementId: Long) =
        deleteProcurement.delete(
            partnerId = partnerId,
            reportId = reportId,
            procurementId = procurementId,
        )

    override fun getProcurementSelectorList(partnerId: Long, reportId: Long) =
        getProcurement.getProcurementsForSelector(partnerId = partnerId, reportId = reportId)
            .map { it.toIdNamePairDTO() }

}
