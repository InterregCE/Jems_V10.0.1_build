package io.cloudflight.jems.server.project.service.report.partner.identification

import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import java.util.*

interface ProjectPartnerReportVerificationPersistence {
    fun getControlReportVerification(partnerId: Long, reportId: Long): Optional<ReportVerification>

    fun updateReportVerification(
        partnerId: Long,
        reportId: Long,
        reportVerification: ReportVerification
    ): ReportVerification
}
