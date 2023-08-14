package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationExpenditure
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportVerificationExpenditureRiskBased(
    private val reportPersistence: ProjectReportPersistence,
    private val generalValidatorService: GeneralValidatorService,
    private val projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence,
): UpdateProjectReportVerificationExpenditureRiskBasedInteractor {

    @CanEditReportVerificationExpenditure
    @Transactional
    @ExceptionWrapper(UpdateProjectReportVerificationExpenditureRiskBasedException::class)
    override fun updateExpenditureVerificationRiskBased(
        reportId: Long,
        riskBasedData: ProjectReportVerificationRiskBased,
    ): ProjectReportVerificationRiskBased {
        validateRiskBasedDescription(riskBasedData)

        val report = reportPersistence.getReportByIdUnSecured(reportId)
        if (report.status.verificationNotStartedYet() || report.status.isFinalized())
            throw VerificationNotOpen()

        return projectReportExpenditureVerificationPersistence
            .updateProjectReportExpenditureVerificationRiskBased(reportId, riskBasedData)
    }

    private fun validateRiskBasedDescription(riskBasedData: ProjectReportVerificationRiskBased) {
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.maxLength(riskBasedData.riskBasedVerificationDescription, 5000, "riskBasedVerificationDescription")
        )
    }

}
