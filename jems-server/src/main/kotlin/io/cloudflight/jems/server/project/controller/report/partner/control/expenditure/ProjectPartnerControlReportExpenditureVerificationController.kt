package io.cloudflight.jems.server.project.controller.report.partner.control.expenditure

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationUpdateDTO
import io.cloudflight.jems.api.project.report.partner.control.ProjectPartnerControlReportExpenditureVerificationApi
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportExpenditureVerification.GetProjectPartnerControlReportExpenditureVerificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportParkedExpenditureIds.GetProjectPartnerControlReportParkedExpendituresInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.updateProjectPartnerReportExpenditureVerification.UpdateProjectPartnerControlReportExpenditureVerificationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerControlReportExpenditureVerificationController(
    private val getReportExpenditureVerification: GetProjectPartnerControlReportExpenditureVerificationInteractor,
    private val updateReportExpenditureVerification: UpdateProjectPartnerControlReportExpenditureVerificationInteractor,
    private val getReportControlParkedExpenditureIds: GetProjectPartnerControlReportParkedExpendituresInteractor,
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

    override fun getParkedExpenditureIds(partnerId: Long, reportId: Long): List<Long> =
        getReportControlParkedExpenditureIds.getParkedExpenditureIds(
            partnerId = partnerId,
            reportId = reportId
        )
}
