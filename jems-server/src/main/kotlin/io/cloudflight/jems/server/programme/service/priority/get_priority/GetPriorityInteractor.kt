package io.cloudflight.jems.server.programme.service.priority.get_priority

import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriorityAvailableSetup

interface GetPriorityInteractor {

    fun getAllPriorities(): List<ProgrammePriority>

    fun getPriority(priorityId: Long): ProgrammePriority

    fun getAvailableSetup(): ProgrammePriorityAvailableSetup

}
