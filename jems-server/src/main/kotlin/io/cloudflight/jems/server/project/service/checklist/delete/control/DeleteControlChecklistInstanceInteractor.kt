package io.cloudflight.jems.server.project.service.checklist.delete.control

interface DeleteControlChecklistInstanceInteractor {

    fun deleteById(partnerId: Long, reportId: Long, checklistId: Long)
}