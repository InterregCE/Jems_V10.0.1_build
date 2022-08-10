package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.ProjectReportProcurementBeneficialPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.fillThisReportFlag
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurementBeneficial(
    private val reportPersistence: ProjectReportPersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
    private val reportProcurementBeneficialPersistence: ProjectReportProcurementBeneficialPersistence,
) : GetProjectPartnerReportProcurementBeneficialInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementBeneficialException::class)
    override fun getBeneficialOwner(
        partnerId: Long,
        reportId: Long,
        procurementId: Long
    ): List<ProjectPartnerReportProcurementBeneficialOwner> {
        // we need to fetch those because of security to make sure those connections really exist
        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)
        val procurement = reportProcurementPersistence.getById(partnerId, procurementId = procurementId)

        return reportProcurementBeneficialPersistence
            .getBeneficialOwnersBeforeAndIncludingReportId(procurement.id, reportId = report.id)
            .fillThisReportFlag(currentReportId = reportId)
    }

}
