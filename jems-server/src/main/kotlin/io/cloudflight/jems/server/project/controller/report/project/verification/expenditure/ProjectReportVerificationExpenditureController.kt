package io.cloudflight.jems.server.project.controller.report.project.verification.expenditure

import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdateDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationRiskBasedDTO
import io.cloudflight.jems.api.project.report.project.verification.ProjectReportVerificationExpenditureAPI
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.getProjectReportVerificationExpenditure.GetProjectReportVerificationExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.getProjectReportVerificationExpenditureRiskBased.GetProjectReportVerificationExpenditureRiskBasedInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditure.UpdateProjectReportVerificationExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased.UpdateProjectReportVerificationExpenditureRiskBasedInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportVerificationExpenditureController(
    private val getExpenditureVerification: GetProjectReportVerificationExpenditureInteractor,
    private val getProjectReportVerificationExpenditureRiskBased: GetProjectReportVerificationExpenditureRiskBasedInteractor,
    private val updateExpenditureVerification: UpdateProjectReportVerificationExpenditureInteractor,
    private val updateProjectReportVerificationExpenditure: UpdateProjectReportVerificationExpenditureRiskBasedInteractor
): ProjectReportVerificationExpenditureAPI {
    override fun getProjectReportExpenditureVerification(
        projectId: Long,
        reportId: Long
    ): List<ProjectReportVerificationExpenditureLineDTO> =
        getExpenditureVerification.getExpenditureVerification(reportId).toDto()

    override fun updateProjectReportExpendituresVerification(
        projectId: Long,
        reportId: Long,
        expenditureVerificationList: List<ProjectReportVerificationExpenditureLineUpdateDTO>
    ): List<ProjectReportVerificationExpenditureLineDTO> =
        updateExpenditureVerification.updateExpenditureVerification(reportId, expenditureVerificationList.toLineUpdateModel()).toDto()

    override fun getProjectReportExpenditureVerificationRiskBased(
        projectId: Long,
        reportId: Long
    ): ProjectReportVerificationRiskBasedDTO =
        getProjectReportVerificationExpenditureRiskBased.getExpenditureVerificationRiskBasedData(projectId, reportId).toDto()

    override fun updateProjectReportExpenditureVerificationRiskBased(
        projectId: Long,
        reportId: Long,
        riskBasedData: ProjectReportVerificationRiskBasedDTO
    ): ProjectReportVerificationRiskBasedDTO =
        updateProjectReportVerificationExpenditure.updateExpenditureVerificationRiskBased(reportId, riskBasedData.toModel()).toDto()
}
