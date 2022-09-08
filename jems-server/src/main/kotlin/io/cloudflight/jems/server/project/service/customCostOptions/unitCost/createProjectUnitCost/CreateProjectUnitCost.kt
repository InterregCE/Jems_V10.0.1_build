package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.createProjectUnitCost

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.getStaticValidationResults
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.costoption.validateCreateUnitCost
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectFormOnlyBeforeApproved
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.projectUnitCostCreated
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProjectUnitCost(
    private val callPersistence: CallPersistence,
    private val programmeUnitCostPersistence: ProgrammeUnitCostPersistence,
    private val projectUnitCostPersistence: ProjectUnitCostPersistence,
    private val projectPersistence: ProjectPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val generalValidator: GeneralValidatorService,
) : CreateProjectUnitCostInteractor {

    @CanUpdateProjectFormOnlyBeforeApproved
    @Transactional
    @ExceptionWrapper(CreateProjectUnitCostException::class)
    override fun createProjectUnitCost(projectId: Long, unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        if (!callPersistence.getCallCostOptionForProject(projectId).projectDefinedUnitCostAllowed) {
            throw ProjectDefinedUnitCostAreForbiddenForThisCall()
        }

        unitCost.validateInputFields()
        validateCreateUnitCost(
            unitCostToValidate = unitCost,
            currentCount = projectUnitCostPersistence.getCount(projectId),
            maxAllowed = 10,
        )

        unitCost.projectId = projectId
        return programmeUnitCostPersistence.createUnitCost(unitCost).also {
            auditPublisher.publishEvent(
                projectUnitCostCreated(this, it, projectPersistence.getProjectSummary(projectId))
            )
        }
    }

    private fun ProgrammeUnitCost.validateInputFields() {
        generalValidator.throwIfAnyIsInvalid(
            *getStaticValidationResults(generalValidator).toTypedArray(),
        )
    }

}
