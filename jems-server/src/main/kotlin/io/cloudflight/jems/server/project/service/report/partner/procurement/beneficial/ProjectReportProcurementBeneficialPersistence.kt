package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial

import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner

interface ProjectReportProcurementBeneficialPersistence {

    fun getBeneficialOwnersBeforeAndIncludingReportId(procurementId: Long, reportId: Long): List<ProjectPartnerReportProcurementBeneficialOwner>

    fun updateBeneficialOwners(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        owners: List<ProjectPartnerReportProcurementBeneficialChange>,
    ): List<ProjectPartnerReportProcurementBeneficialOwner>

    fun countBeneficialOwnersCreatedBefore(procurementId: Long, reportId: Long): Long

}
