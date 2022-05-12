package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.project.checklist.ChecklistInstanceApi
import io.cloudflight.jems.api.project.dto.checklist.*
import io.cloudflight.jems.server.programme.controller.checklist.toModel
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.GetChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.consolidateInstance.ConsolidateChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.create.CreateChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.update.UpdateChecklistInstanceInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ChecklistInstanceController(
    private val getChecklistInteractor: GetChecklistInstancesInteractor,
    private val getChecklistDetailInteractor: GetChecklistInstanceDetailInteractor,
    private val updateInteractor: UpdateChecklistInstanceInteractor,
    private val createInteractor: CreateChecklistInstanceInteractor,
    private val deleteInteractor: DeleteChecklistInstanceInteractor,
    private val consolidateInteractor: ConsolidateChecklistInstanceInteractor
) : ChecklistInstanceApi {

    override fun getMyChecklistInstances(relatedToId: Long, type: ProgrammeChecklistTypeDTO): List<ChecklistInstanceDTO> =
        getChecklistInteractor.getChecklistInstancesOfCurrentUserByTypeAndRelatedId(relatedToId, type.toModel()).toDto()

    override fun getAllChecklistInstances(relatedToId: Long, type: ProgrammeChecklistTypeDTO): List<ChecklistInstanceDTO> =
        getChecklistInteractor.getChecklistInstancesByTypeAndRelatedId(relatedToId, type.toModel()).toDto()

    override fun getChecklistInstancesForSelection(
        relatedToId: Long,
        type: ProgrammeChecklistTypeDTO
    ): List<ChecklistInstanceSelectionDTO> =
        getChecklistInteractor.getChecklistInstancesOfCurrentUserByTypeAndRelatedId(relatedToId, type.toModel()).toSelectionDto()

    override fun getChecklistInstanceDetail(checklistId: Long): ChecklistInstanceDetailDTO =
        getChecklistDetailInteractor.getChecklistInstanceDetail(checklistId).toDetailDto()

    override fun createChecklistInstance(checklist: CreateChecklistInstanceDTO): ChecklistInstanceDetailDTO =
        createInteractor.create(checklist.toModel()).toDetailDto()

    override fun updateChecklistInstance(checklist: ChecklistInstanceDetailDTO): ChecklistInstanceDetailDTO =
        updateInteractor.update(checklist.toDetailModel()).toDetailDto()

    override fun changeChecklistStatus(checklistId: Long, status: ChecklistInstanceStatusDTO): ChecklistInstanceDTO =
        updateInteractor.changeStatus(checklistId, ChecklistInstanceStatus.valueOf(status.name)).toDto()

    override fun consolidateChecklistInstance(checklistId: Long, options: ChecklistConsolidatorOptionsDTO): Boolean =
        consolidateInteractor.consolidateChecklistInstance(checklistId, options.consolidated)

    override fun updateChecklistInstanceSelection(checklistId: Long, visible: Boolean) =
        updateInteractor.updateSelection(checklistId, visible)

    override fun deleteChecklistInstance(checklistId: Long) =
        deleteInteractor.deleteById(checklistId)

}
