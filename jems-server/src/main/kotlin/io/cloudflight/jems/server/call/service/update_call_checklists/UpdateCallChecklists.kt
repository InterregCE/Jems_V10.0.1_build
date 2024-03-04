package io.cloudflight.jems.server.call.service.update_call_checklists

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.callSelectedChecklistsChanged
import io.cloudflight.jems.server.call.service.model.CallChecklist
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCallChecklists(
    private val persistence: CallPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : UpdateCallChecklistsInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UpdateCallChecklistsException::class)
    override fun updateCallChecklists(callId: Long, checklistIds: Set<Long>) {
        val checklists = persistence.getCallChecklists(callId, Sort.unsorted())
        val call = persistence.getCallById(callId)

        if (hasChange(checklists, checklistIds)) {
            persistence.updateCallChecklistSelection(callId, checklistIds)

            if (call.isPublished()) {
                auditPublisher.publishEvent(callSelectedChecklistsChanged(this, checklists.filter { it.selected }, call))
            }
        }
    }

    private fun hasChange(checklists: List<CallChecklist>, newSelectedIds: Set<Long>): Boolean {
        return checklists.filter { it.selected }.mapNotNull { it.id }.toSet() != newSelectedIds
    }
}
