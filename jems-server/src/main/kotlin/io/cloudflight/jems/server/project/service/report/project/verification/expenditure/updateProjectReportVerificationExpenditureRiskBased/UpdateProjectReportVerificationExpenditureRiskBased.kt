package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationPrivilegedByReportId
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportVerificationExpenditureRiskBased(
    private val generalValidatorService: GeneralValidatorService,
    private val projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence,
): UpdateProjectReportVerificationExpenditureRiskBasedInteractor {

    @CanEditReportVerificationPrivilegedByReportId
    @Transactional
    @ExceptionWrapper(UpdateProjectReportVerificationExpenditureRiskBasedException::class)
    override fun updateExpenditureVerificationRiskBased(
        projectId: Long,
        projectReportId: Long,
        riskBasedData: ProjectReportVerificationRiskBased
    ): ProjectReportVerificationRiskBased {
        validateRiskBasedDescription(riskBasedData)

        return projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerificationRiskBased(projectId, projectReportId, riskBasedData)
    }

    private fun validateRiskBasedDescription(riskBasedData: ProjectReportVerificationRiskBased) {
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.maxLength(riskBasedData.riskBasedVerificationDescription, 5000, "riskBasedVerificationDescription")
        )
    }

}
