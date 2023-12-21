package io.cloudflight.jems.server.programme.service.checklist.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ChecklistTemplateValidator
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklistUpdated
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
    private val checklistInstancePersistence: ChecklistInstancePersistence,
    private val checklistTemplateValidator: ChecklistTemplateValidator,
    private val applicationEventPublisher: ApplicationEventPublisher
) : UpdateProgrammeChecklistInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(UpdateProgrammeChecklistException::class)
    override fun update(programmeChecklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail {
        checklistTemplateValidator.validateInput(programmeChecklist)
        programmeChecklist.components?.forEach { checklistTemplateValidator.validateCheckListComponents(it) }

        val isProgrammeChecklistInstantiated = checklistInstancePersistence.countAllByChecklistTemplateId(programmeChecklist.id!!) > 0
        val checklist = persistence.getChecklistDetail(programmeChecklist.id)
        if (isProgrammeChecklistInstantiated) {
            checklistTemplateValidator.validateAllowedChanges(checklist, programmeChecklist)
        }

        return persistence.updateChecklist(programmeChecklist).also {
            if (isProgrammeChecklistInstantiated)
                applicationEventPublisher.publishEvent(checklistUpdated(this, it, checklist.name.toString()))
        }
    }
}
