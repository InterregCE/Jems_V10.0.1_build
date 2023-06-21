package io.cloudflight.jems.server.project.service.report.partner.identification

import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportDesignatedController

interface ProjectPartnerReportDesignatedControllerPersistence {

    fun getControlReportDesignatedController(partnerId: Long, reportId: Long): ReportDesignatedController

    fun create(partnerId: Long, reportId: Long, institutionId: Long)

    fun updateWithInstitutionName(partnerId: Long, reportId: Long, institutionName: String)

    fun updateDesignatedController(
        partnerId: Long,
        reportId: Long,
        designatedController: ReportDesignatedController
    ): ReportDesignatedController
}
