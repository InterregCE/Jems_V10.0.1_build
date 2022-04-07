package io.cloudflight.jems.server.programme.service.checklist.update

import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail

interface UpdateChecklistInstanceInteractor {
    fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail
}
