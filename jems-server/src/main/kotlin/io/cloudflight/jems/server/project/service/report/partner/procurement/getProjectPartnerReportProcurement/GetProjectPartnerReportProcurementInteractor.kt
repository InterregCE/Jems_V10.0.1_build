package io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetProjectPartnerReportProcurementInteractor {

    fun getProcurement(partnerId: Long, reportId: Long, pageable: Pageable): Page<ProjectPartnerReportProcurementSummary>

    fun getProcurementById(partnerId: Long, reportId: Long, procurementId: Long): ProjectPartnerReportProcurement

    fun getProcurementsForSelector(partnerId: Long, reportId: Long): List<IdNamePair>

}
