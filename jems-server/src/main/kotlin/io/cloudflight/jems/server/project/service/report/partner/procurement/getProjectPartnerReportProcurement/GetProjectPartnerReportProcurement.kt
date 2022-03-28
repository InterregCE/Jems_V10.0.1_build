package io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.fillThisReportFlag
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurement(
    private val reportPersistence: ProjectReportPersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
) : GetProjectPartnerReportProcurementInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementException::class)
    override fun getProcurement(partnerId: Long, reportId: Long) =
        getProcurementList(partnerId = partnerId, reportId = reportId).fillThisReportFlag(currentReportId = reportId)

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementsForSelectorException::class)
    override fun getProcurementsForSelector(partnerId: Long, reportId: Long): List<IdNamePair> =
        getProcurementList(partnerId = partnerId, reportId = reportId).map { IdNamePair(it.id, it.contractId) }

    private fun getProcurementList(partnerId: Long, reportId: Long): List<ProjectPartnerReportProcurement> {
        val previousReportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = reportId)
        return reportProcurementPersistence.getProcurementsForReportIds(
            reportIds = previousReportIds.plus(reportId)
        )
    }

}
