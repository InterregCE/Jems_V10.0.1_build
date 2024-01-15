package io.cloudflight.jems.server.programme.service.checklist.clone

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklistCreated
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CloneProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : CloneProgrammeChecklistInteractor {

    companion object {
        const val MAX_NUMBER_OF_CHECKLIST = 100
    }

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(CloneProgrammeChecklistException::class)
    override fun clone(checklistId: Long): ProgrammeChecklistDetail {
        if (persistence.countAll().toInt() >= MAX_NUMBER_OF_CHECKLIST)
            throw MaxAmountOfProgrammeChecklistReached(maxAmount = MAX_NUMBER_OF_CHECKLIST)

        val existingChecklist = persistence.getChecklistDetail(id = checklistId)
        val newChecklist = existingChecklist.copy(
            id = null,
            name = existingChecklist.name.plus(" - COPY"),
            components = existingChecklist.components?.map { it.copy(id = null) }
        )

        return persistence.createChecklist(newChecklist).also {
            auditPublisher.publishEvent(
                checklistCreated(context = this, checklist = it)
            )
        }
    }
}
