package io.cloudflight.jems.server.programme.service.checklist.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ChecklistTemplateValidator
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
    private val checklistInstancePersistence: ChecklistInstancePersistence,
    private val checklistTemplateValidator: ChecklistTemplateValidator,
) : UpdateProgrammeChecklistInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(UpdateProgrammeChecklistException::class)
    override fun update(programmeChecklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail {
        checklistTemplateValidator.validateInput(programmeChecklist)
        programmeChecklist.components?.forEach { checklistTemplateValidator.validateCheckListComponents(it) }

        val checklistInstancesCount = checklistInstancePersistence.countAllByChecklistTemplateId(programmeChecklist.id ?: 0)
        if (checklistInstancesCount > 0) {
            throw ChecklistLockedException()
        }
        return persistence.createOrUpdate(programmeChecklist)
    }
}
