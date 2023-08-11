package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.getProjectReportVerificationExpenditureRiskBased

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationPrivilegedByReportId
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportVerificationExpenditureRiskBased(
    private val projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence
) : GetProjectReportVerificationExpenditureRiskBasedInteractor {

    @CanViewReportVerificationPrivilegedByReportId
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportVerificationExpenditureRiskBasedException::class)
    override fun getExpenditureVerificationRiskBasedData(
        projectId: Long,
        projectReportId: Long
    ): ProjectReportVerificationRiskBased =
        projectReportExpenditureVerificationPersistence.getExpenditureVerificationRiskBasedData(projectId, projectReportId)
}


