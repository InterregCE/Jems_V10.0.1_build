package io.cloudflight.jems.server.programme.service.checklist.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.authorization.CanUpdateChecklistAssessment
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.checklistStatusChanged
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val generalValidator: GeneralValidatorService
) : UpdateChecklistInstanceInteractor {

    @CanUpdateChecklistAssessment
    @Transactional
    @ExceptionWrapper(UpdateChecklistInstanceException::class)
    override fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        val oldStatus = persistence.getStatus(checklist.id);
        if (oldStatus == ChecklistInstanceStatus.FINISHED)
            throw UpdateChecklistInstanceStatusNotAllowedException()

        val context = this
        return checklist.components?.forEach {
            ifCheckListComponentsAreValid(it)
        }.run {
            persistence.update(checklist).also {
                if (oldStatus !== checklist.status)
                    auditPublisher.publishEvent(
                        checklistStatusChanged(
                            context = context,
                            checklist = it,
                            oldStatus = oldStatus,
                        )
                    )
            }
        }
    }

    private fun ifCheckListComponentsAreValid(component: ChecklistComponentInstance) {
        if (component.type === ProgrammeChecklistComponentType.TEXT_INPUT) {
            generalValidator.throwIfAnyIsInvalid(
                generalValidator.maxLength(
                    (component.instanceMetadata as TextInputInstanceMetadata).explanation,
                    (component.programmeMetadata as TextInputMetadata).explanationMaxLength,
                    "explanation"
                )
            )
        }
    }
}
