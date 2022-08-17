package io.cloudflight.jems.server.project.controller.report.procurement.beneficial

import io.cloudflight.jems.api.project.dto.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChangeDTO
import io.cloudflight.jems.api.project.report.procurement.ProjectPartnerReportProcurementBeneficialOwnerApi
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial.GetProjectPartnerReportProcurementBeneficialInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.updateProjectPartnerReportProcurement.UpdateProjectPartnerReportProcurementBeneficialInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportProcurementBeneficialController(
    private val getBeneficialOwner: GetProjectPartnerReportProcurementBeneficialInteractor,
    private val updateBeneficialOwner: UpdateProjectPartnerReportProcurementBeneficialInteractor,
) : ProjectPartnerReportProcurementBeneficialOwnerApi {

    override fun getBeneficialOwners(partnerId: Long, reportId: Long, procurementId: Long) =
        getBeneficialOwner.getBeneficialOwner(partnerId, reportId = reportId, procurementId = procurementId).toDto()

    override fun updateBeneficialOwners(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        owners: List<ProjectPartnerReportProcurementBeneficialChangeDTO>,
    ) = updateBeneficialOwner.update(partnerId, reportId = reportId, procurementId = procurementId, owners.toModel()).toDto()

}
