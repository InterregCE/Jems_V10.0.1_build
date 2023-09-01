package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.getProjectReportVerificationExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationExpenditure
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportVerificationExpenditure(
    private val projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence
) : GetProjectReportVerificationExpenditureInteractor {

    @CanViewReportVerificationExpenditure
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportVerificationExpenditureException::class)
    override fun getExpenditureVerification(
        reportId: Long
    ): List<ProjectReportVerificationExpenditureLine> =
        projectReportExpenditureVerificationPersistence.getProjectReportExpenditureVerification(reportId)

}
