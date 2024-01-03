package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest

interface ControlChecklistInstancePersistence {
    fun findChecklistInstances(searchRequest: ChecklistInstanceSearchRequest): List<ChecklistInstance>
}
