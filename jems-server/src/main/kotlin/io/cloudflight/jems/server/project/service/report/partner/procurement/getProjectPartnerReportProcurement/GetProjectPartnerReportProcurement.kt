package io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementSummary
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.fillThisReportFlag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurement(
    private val reportPersistence: ProjectReportPersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
) : GetProjectPartnerReportProcurementInteractor {

    companion object {
        private val defaultPageable = PageRequest.of(0, 50, Sort.by(
            Sort.Order(Sort.Direction.DESC, "reportEntity.id"),
            Sort.Order(Sort.Direction.DESC, "id"),
        ))
    }

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementException::class)
    override fun getProcurement(partnerId: Long, reportId: Long, pageable: Pageable) =
        if (reportPersistence.exists(partnerId = partnerId, reportId = reportId))
            getProcurementList(partnerId = partnerId, reportId = reportId, pageable)
                .fillThisReportFlag(currentReportId = reportId)
        else throw PartnerReportNotFound()

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementByIdException::class)
    override fun getProcurementById(partnerId: Long, reportId: Long, procurementId: Long) =
        reportProcurementPersistence.getById(partnerId, procurementId = procurementId)
            .fillThisReportFlag(currentReportId = reportId)

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementsForSelectorException::class)
    override fun getProcurementsForSelector(partnerId: Long, reportId: Long): List<IdNamePair> =
        if (reportPersistence.exists(partnerId = partnerId, reportId = reportId))
            getProcurementList(partnerId = partnerId, reportId = reportId, defaultPageable).content
                .map { IdNamePair(it.id, it.contractName) }
        else throw PartnerReportNotFoundForSelector()

    private fun getProcurementList(partnerId: Long, reportId: Long, pageable: Pageable): Page<ProjectPartnerReportProcurementSummary> {
        val previousReportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = reportId)
        return reportProcurementPersistence.getProcurementsForReportIds(
            reportIds = previousReportIds.plus(reportId),
            pageable = pageable,
        )
    }

}
