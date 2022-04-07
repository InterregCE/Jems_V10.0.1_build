package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.project.checklist.ChecklistInstanceApi
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.cloudflight.jems.server.programme.controller.checklist.toModel
import io.cloudflight.jems.server.programme.service.checklist.create.CreateChecklistInstanceInteractor
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteChecklistInstanceInteractor
import io.cloudflight.jems.server.programme.service.checklist.getDetail.GetChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.programme.service.checklist.getList.GetChecklistInstanceInteractor
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateChecklistInstanceInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ChecklistInstanceController(
    private val getChecklistInteractor: GetChecklistInstanceInteractor,
    private val getChecklistDetailInteractor: GetChecklistInstanceDetailInteractor,
    private val updateInteractor: UpdateChecklistInstanceInteractor,
    private val createInteractor: CreateChecklistInstanceInteractor,
    private val deleteInteractor: DeleteChecklistInstanceInteractor,
) : ChecklistInstanceApi {

    override fun getChecklistInstances(relatedToId: Long, type: ProgrammeChecklistTypeDTO): List<ChecklistInstanceDTO> =
        getChecklistInteractor.getChecklistInstancesOfCurrentUserByTypeAndRelatedId(relatedToId, type.toModel()).toDto()

    override fun getChecklistInstanceDetail(checklistId: Long): ChecklistInstanceDetailDTO =
        getChecklistDetailInteractor.getChecklistInstanceDetail(checklistId).toDetailDto()

    override fun createChecklistInstance(checklist: CreateChecklistInstanceDTO): ChecklistInstanceDetailDTO =
        createInteractor.create(checklist.toModel()).toDetailDto()

    override fun updateChecklistInstance(checklist: ChecklistInstanceDetailDTO): ChecklistInstanceDetailDTO =
        updateInteractor.update(checklist.toDetailModel()).toDetailDto()

    override fun deleteChecklistInstance(checklistId: Long) =
        deleteInteractor.deleteById(checklistId)

}
