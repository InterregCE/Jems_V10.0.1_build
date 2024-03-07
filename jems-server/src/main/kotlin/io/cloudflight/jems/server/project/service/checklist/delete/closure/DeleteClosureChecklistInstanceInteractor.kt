package io.cloudflight.jems.server.project.service.checklist.delete.closure

interface DeleteClosureChecklistInstanceInteractor {

    fun deleteById(reportId: Long, checklistId: Long)
}
