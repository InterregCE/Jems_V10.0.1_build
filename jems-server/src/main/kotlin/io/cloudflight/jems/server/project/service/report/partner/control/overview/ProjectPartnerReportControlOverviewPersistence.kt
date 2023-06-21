package io.cloudflight.jems.server.project.service.report.partner.control.overview

import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import java.time.LocalDate

interface ProjectPartnerReportControlOverviewPersistence {

    fun getPartnerControlReportOverview(partnerId: Long, reportId: Long): ControlOverview

    fun createPartnerControlReportOverview(partnerId: Long, reportId: Long, lastCertifiedReportId: Long?): ControlOverview

    fun updatePartnerControlReportOverview(partnerId: Long, reportId: Long, controlOverview: ControlOverview): ControlOverview

    fun updatePartnerControlReportOverviewEndDate(partnerId: Long, reportId: Long, endDate: LocalDate): ControlOverview
}
