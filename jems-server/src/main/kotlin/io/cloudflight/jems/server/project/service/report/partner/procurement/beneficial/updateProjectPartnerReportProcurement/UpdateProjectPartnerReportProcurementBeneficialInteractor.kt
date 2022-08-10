package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.updateProjectPartnerReportProcurement

import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner

interface UpdateProjectPartnerReportProcurementBeneficialInteractor {

    fun update(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        owners: List<ProjectPartnerReportProcurementBeneficialChange>,
    ): List<ProjectPartnerReportProcurementBeneficialOwner>

}
