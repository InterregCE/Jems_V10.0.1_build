package io.cloudflight.jems.server.project.service.checklist.delete.verification

interface DeleteVerificationChecklistInstanceInteractor {

    fun deleteById(projectId: Long, reportId: Long, checklistId: Long)
}