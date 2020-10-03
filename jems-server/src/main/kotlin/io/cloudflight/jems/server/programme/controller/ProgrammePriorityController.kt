package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.programme.ProgrammePriorityApi
import io.cloudflight.jems.api.programme.dto.InputProgrammePriorityCreate
import io.cloudflight.jems.api.programme.dto.InputProgrammePriorityUpdate
import io.cloudflight.jems.api.programme.dto.OutputProgrammePriority
import io.cloudflight.jems.api.programme.dto.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.ProgrammePriorityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
class ProgrammePriorityController(
    private val programmePriorityService: ProgrammePriorityService
) : ProgrammePriorityApi {

    override fun get(pageable: Pageable): Page<OutputProgrammePriority> {
        return programmePriorityService.getAll(pageable)
    }

    override fun create(priority: InputProgrammePriorityCreate): OutputProgrammePriority {
        return programmePriorityService.create(priority)
    }

    override fun update(priority: InputProgrammePriorityUpdate): OutputProgrammePriority {
        return programmePriorityService.update(priority)
    }

    override fun delete(id: Long) {
        programmePriorityService.delete(programmePriorityId = id)
    }

    override fun getFreePrioritiesWithPolicies(): Map<ProgrammeObjective, List<ProgrammeObjectivePolicy>> {
        return programmePriorityService.getFreePrioritiesWithPolicies()
    }

}
