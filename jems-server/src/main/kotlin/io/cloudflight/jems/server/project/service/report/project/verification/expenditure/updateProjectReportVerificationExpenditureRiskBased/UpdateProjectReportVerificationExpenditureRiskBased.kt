package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReportVerificationByReportId
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportVerificationExpenditureRiskBased(
    private val projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence,
): UpdateProjectReportVerificationExpenditureRiskBasedInteractor {

    @CanEditProjectReportVerificationByReportId
    @Transactional
    @ExceptionWrapper(UpdateProjectReportVerificationExpenditureRiskBasedException::class)
    override fun updateExpenditureVerificationRiskBased(
        projectId: Long,
        projectReportId: Long,
        riskBasedData: ProjectReportVerificationRiskBased
    ): ProjectReportVerificationRiskBased =
        projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerificationRiskBased(projectId, projectReportId, riskBasedData)

}
