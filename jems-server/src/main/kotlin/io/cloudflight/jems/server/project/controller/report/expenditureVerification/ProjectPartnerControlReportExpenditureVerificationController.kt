package io.cloudflight.jems.server.project.controller.report.expenditureVerification

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationUpdateDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerControlReportExpenditureVerificationApi
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerControlReportExpenditureVerification.GetProjectPartnerControlReportExpenditureVerificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerControlReportExpenditureVerification.UpdateProjectPartnerControlReportExpenditureVerificationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerControlReportExpenditureVerificationController(
    private val getProjectPartnerControlReportExpenditureVerificationInteractor: GetProjectPartnerControlReportExpenditureVerificationInteractor,
    private val updateProjectPartnerControlReportExpenditureVerificationInteractor: UpdateProjectPartnerControlReportExpenditureVerificationInteractor
) : ProjectPartnerControlReportExpenditureVerificationApi {

    override fun getProjectPartnerExpenditureVerification(
        partnerId: Long,
        reportId: Long
    ): List<ProjectPartnerControlReportExpenditureVerificationDTO> =
        getProjectPartnerControlReportExpenditureVerificationInteractor.getExpenditureVerification(
            partnerId = partnerId,
            reportId = reportId,
        ).toDto()

    override fun updatePartnerReportExpendituresVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ProjectPartnerControlReportExpenditureVerificationUpdateDTO>
    ): List<ProjectPartnerControlReportExpenditureVerificationDTO> =
        updateProjectPartnerControlReportExpenditureVerificationInteractor.updatePartnerReportExpenditureVerification(
            partnerId = partnerId,
            reportId = reportId,
            expenditureVerification = expenditureVerification.toModel()
        ).toDto()
}
