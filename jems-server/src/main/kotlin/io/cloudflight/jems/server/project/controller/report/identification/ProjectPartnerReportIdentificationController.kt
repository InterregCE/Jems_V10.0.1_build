package io.cloudflight.jems.server.project.controller.report.identification

import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.UpdateProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportIdentificationApi
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.identification.updateProjectPartnerReportIdentification.UpdateProjectPartnerReportIdentificationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportIdentificationController(
    private val getIdentification: GetProjectPartnerReportIdentificationInteractor,
    private val updateIdentification: UpdateProjectPartnerReportIdentificationInteractor,
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

}
