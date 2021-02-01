package io.cloudflight.jems.server.programme.service.priority.create_priority

import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority

interface CreatePriorityInteractor {
    fun createPriority(priority: ProgrammePriority): ProgrammePriority
}
