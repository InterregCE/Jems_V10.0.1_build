package io.cloudflight.jems.server.programme.service.checklist.delete

interface DeleteChecklistInstanceInteractor {

    fun deleteById(checklistId: Long)
}
