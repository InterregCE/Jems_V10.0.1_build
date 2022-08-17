package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial

import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner

interface GetProjectPartnerReportProcurementBeneficialInteractor {

    fun getBeneficialOwner(partnerId: Long, reportId: Long, procurementId: Long): List<ProjectPartnerReportProcurementBeneficialOwner>

}
