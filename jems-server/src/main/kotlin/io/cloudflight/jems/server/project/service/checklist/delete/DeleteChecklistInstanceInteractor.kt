package io.cloudflight.jems.server.project.service.checklist.delete

interface DeleteChecklistInstanceInteractor {

    fun deleteById(checklistId: Long, projectId: Long)
}
