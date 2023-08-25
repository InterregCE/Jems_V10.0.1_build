package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.updateResultPrinciple

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrincipleUpdate
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectReportResultPrinciple(
    private val projectReportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence,
    private val generalValidatorService: GeneralValidatorService
) : UpdateProjectReportResultPrincipleInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateProjectReportResultPrincipleException::class)
    override fun update(projectId: Long, reportId: Long, resultPrinciple: ProjectReportResultPrincipleUpdate): ProjectReportResultPrinciple {
        validateInputs(resultPrinciple)

        return projectReportResultPrinciplePersistence.updateProjectReportResultPrinciple(
            projectId = projectId,
            reportId = reportId,
            newResultsAndPrinciples = resultPrinciple
        )
    }

    private fun validateInputs(resultPrinciple: ProjectReportResultPrincipleUpdate) {
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.maxLength(resultPrinciple.sustainableDevelopmentDescription, 2000, "principlesSustainable"),
            generalValidatorService.maxLength(resultPrinciple.equalOpportunitiesDescription, 2000, "principlesEquality"),
            generalValidatorService.maxLength(resultPrinciple.sexualEqualityDescription, 2000, "principlesEquality"),
            *resultPrinciple.projectResults.map { generalValidatorService.maxLength(it.value.description, 2000, "description") }
                .toTypedArray(),
            *resultPrinciple.projectResults.map {
                generalValidatorService.numberBetween(
                    it.value.currentValue, BigDecimal.valueOf(-999_999_999_99, 2), BigDecimal.valueOf(999_999_999_99, 2), "achievedInReportingPeriod"
                )
            }.toTypedArray(),
        )
    }
}
