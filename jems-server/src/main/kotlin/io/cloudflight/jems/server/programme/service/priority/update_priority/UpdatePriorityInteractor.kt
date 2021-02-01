package io.cloudflight.jems.server.programme.service.priority.update_priority

import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority

interface UpdatePriorityInteractor {
    fun updatePriority(priorityId: Long, priority: ProgrammePriority): ProgrammePriority
}
