package io.cloudflight.jems.server.project.service.report.partner.identification

import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.partner.identification.UpdateProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ProjectPartnerControlReportChange
import java.util.Optional

interface ProjectPartnerReportIdentificationPersistence {

    fun getPartnerReportIdentification(partnerId: Long, reportId: Long): Optional<ProjectPartnerReportIdentification>

    fun updatePartnerReportIdentification(
        partnerId: Long,
        reportId: Long,
        data: UpdateProjectPartnerReportIdentification,
    ): ProjectPartnerReportIdentification

    fun getAvailablePeriods(partnerId: Long, reportId: Long): List<ProjectPartnerReportPeriod>

    fun updatePartnerControlReportIdentification(
        partnerId: Long,
        reportId: Long,
        data: ProjectPartnerControlReportChange,
    ): ProjectPartnerReportIdentification

}
