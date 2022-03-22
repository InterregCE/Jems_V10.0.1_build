package io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement

interface GetProjectPartnerReportProcurementInteractor {

    fun getProcurement(partnerId: Long, reportId: Long): List<ProjectPartnerReportProcurement>

    fun getProcurementsForSelector(partnerId: Long, reportId: Long): List<IdNamePair>

}
