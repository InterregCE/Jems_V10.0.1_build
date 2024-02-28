package io.cloudflight.jems.server.call.service.update_call_checklists

interface UpdateCallChecklistsInteractor {
    fun updateCallChecklists(callId: Long, checklistIds: Set<Long>)
}
