package io.cloudflight.jems.server.project.service.checklist.consolidateInstance

interface ConsolidateChecklistInstanceInteractor {
    fun consolidateChecklistInstance(checklistId: Long, consolidated: Boolean): Boolean
}
