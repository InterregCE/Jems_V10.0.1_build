package io.cloudflight.jems.server.project.service.checklist.delete.contracting

interface DeleteContractingChecklistInstanceInteractor {

    fun deleteById(projectId: Long, checklistId: Long)
}
