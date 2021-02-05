package io.cloudflight.jems.server.programme.controller.priority

import io.cloudflight.jems.api.programme.priority.ProgrammePriorityApi
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityAvailableSetupDTO
import io.cloudflight.jems.server.programme.service.priority.create_priority.CreatePriorityInteractor
import io.cloudflight.jems.server.programme.service.priority.delete_priority.DeletePriorityInteractor
import io.cloudflight.jems.server.programme.service.priority.get_priority.GetPriorityInteractor
import io.cloudflight.jems.server.programme.service.priority.update_priority.UpdatePriorityInteractor
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammePriorityController(
    private val createPriority: CreatePriorityInteractor,
    private val deletePriority: DeletePriorityInteractor,
    private val getPriority: GetPriorityInteractor,
    private val updatePriority: UpdatePriorityInteractor,
) : ProgrammePriorityApi {

    override fun get(): List<ProgrammePriorityDTO> =
        getPriority.getAllPriorities().map { it.toDto() }

    override fun getById(id: Long): ProgrammePriorityDTO =
        getPriority.getPriority(id).toDto()

    override fun create(priority: ProgrammePriorityDTO): ProgrammePriorityDTO =
        createPriority.createPriority(priority.toModel()).toDto()

    override fun update(id: Long, priority: ProgrammePriorityDTO): ProgrammePriorityDTO =
        updatePriority.updatePriority(id, priority.toModel()).toDto()

    override fun delete(id: Long) =
        deletePriority.deletePriority(priorityId = id)

    override fun getAvailableSetup() =
        getPriority.getAvailableSetup().toDto()

}
