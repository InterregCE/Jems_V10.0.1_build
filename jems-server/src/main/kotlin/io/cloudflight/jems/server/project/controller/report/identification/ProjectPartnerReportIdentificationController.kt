package io.cloudflight.jems.server.project.controller.report.identification

import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportPeriodDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.UpdateProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportIdentificationApi
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods.GetProjectPartnerReportAvailablePeriodsInteractor
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.identification.updateProjectPartnerReportIdentification.UpdateProjectPartnerReportIdentificationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportIdentificationController(
    private val getIdentification: GetProjectPartnerReportIdentificationInteractor,
    private val updateIdentification: UpdateProjectPartnerReportIdentificationInteractor,
    private val getAvailablePeriods: GetProjectPartnerReportAvailablePeriodsInteractor,
) : ProjectPartnerReportIdentificationApi {

    override fun getIdentification(partnerId: Long, reportId: Long): ProjectPartnerReportIdentificationDTO =
        getIdentification.getIdentification(partnerId = partnerId, reportId = reportId).toDto()

    override fun updateIdentification(
        partnerId: Long,
        reportId: Long,
        identification: UpdateProjectPartnerReportIdentificationDTO
    ): ProjectPartnerReportIdentificationDTO =
        updateIdentification.updateIdentification(
            partnerId = partnerId,
            reportId = reportId,
            data = identification.toModel(),
        ).toDto()

    override fun getAvailablePeriods(partnerId: Long, reportId: Long): List<ProjectPartnerReportPeriodDTO> =
        getAvailablePeriods.get(partnerId = partnerId, reportId = reportId).toDto()

}
