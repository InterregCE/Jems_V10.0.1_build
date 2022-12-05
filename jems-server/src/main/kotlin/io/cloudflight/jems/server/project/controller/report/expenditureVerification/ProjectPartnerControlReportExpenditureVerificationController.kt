package io.cloudflight.jems.server.project.controller.report.expenditureVerification

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationUpdateDTO
import io.cloudflight.jems.api.project.report.control.ProjectPartnerControlReportExpenditureVerificationApi
import io.cloudflight.jems.server.project.service.report.partner.expenditure.control.getProjectPartnerReportExpenditureVerification.GetProjectPartnerControlReportExpenditureVerificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.control.updateProjectPartnerReportExpenditureVerification.UpdateProjectPartnerControlReportExpenditureVerificationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerControlReportExpenditureVerificationController(
    private val getReportExpenditureVerification: GetProjectPartnerControlReportExpenditureVerificationInteractor,
    private val updateReportExpenditureVerification: UpdateProjectPartnerControlReportExpenditureVerificationInteractor,
) : ProjectPartnerControlReportExpenditureVerificationApi {

    override fun getProjectPartnerExpenditureVerification(partnerId: Long, reportId: Long) =
        getReportExpenditureVerification.getExpenditureVerification(
            partnerId = partnerId,
            reportId = reportId,
        ).toDto()

    override fun updatePartnerReportExpendituresVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ProjectPartnerControlReportExpenditureVerificationUpdateDTO>
    ): List<ProjectPartnerControlReportExpenditureVerificationDTO> =
        updateReportExpenditureVerification.updatePartnerReportExpenditureVerification(
            partnerId = partnerId,
            reportId = reportId,
            expenditureVerification = expenditureVerification.toModel()
        ).toDto()
}
