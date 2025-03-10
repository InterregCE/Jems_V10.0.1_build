package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.checklist.ChecklistInstanceApi
import io.cloudflight.jems.api.project.dto.checklist.ChecklistConsolidatorOptionsDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceSelectionDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.programme.controller.checklist.toModel
import io.cloudflight.jems.server.project.service.checklist.clone.CloneChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.consolidateInstance.ConsolidateChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.create.CreateChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.DeleteChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.export.ExportChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.GetChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.update.UpdateChecklistInstanceInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ChecklistInstanceController(
    private val getChecklistInteractor: GetChecklistInstancesInteractor,
    private val getChecklistDetailInteractor: GetChecklistInstanceDetailInteractor,
    private val updateInteractor: UpdateChecklistInstanceInteractor,
    private val createInteractor: CreateChecklistInstanceInteractor,
    private val deleteInteractor: DeleteChecklistInstanceInteractor,
    private val consolidateInteractor: ConsolidateChecklistInstanceInteractor,
    private val exportInteractor: ExportChecklistInstanceInteractor,
    private val cloneInteractor: CloneChecklistInstanceInteractor
) : ChecklistInstanceApi {

    override fun getMyChecklistInstances(relatedToId: Long, type: ProgrammeChecklistTypeDTO): List<ChecklistInstanceDTO> =
        getChecklistInteractor.getChecklistInstancesOfCurrentUserByTypeAndRelatedId(relatedToId, type.toModel()).toDto()

    override fun getAllChecklistInstances(relatedToId: Long, type: ProgrammeChecklistTypeDTO): List<ChecklistInstanceDTO> =
        getChecklistInteractor.getChecklistInstancesByTypeAndRelatedId(relatedToId, type.toModel()).toDto()

    override fun getChecklistInstancesForSelection(
        relatedToId: Long,
        type: ProgrammeChecklistTypeDTO
    ): List<ChecklistInstanceSelectionDTO> =
        getChecklistInteractor.getChecklistInstancesForSelection(relatedToId, type.toModel()).toSelectionDto()

    override fun getChecklistInstanceDetail(checklistId: Long, relatedToId: Long): ChecklistInstanceDetailDTO =
        getChecklistDetailInteractor.getChecklistInstanceDetail(checklistId, relatedToId).toDetailDto()

    override fun createChecklistInstance(checklist: CreateChecklistInstanceDTO): ChecklistInstanceDetailDTO =
        createInteractor.create(checklist.toModel()).toDetailDto()

    override fun cloneChecklistInstance(checklistId: Long): ChecklistInstanceDetailDTO =
        cloneInteractor.clone(checklistId).toDetailDto()

    override fun updateChecklistInstance(checklist: ChecklistInstanceDetailDTO): ChecklistInstanceDetailDTO =
        updateInteractor.update(checklist.toDetailModel()).toDetailDto()

    override fun changeChecklistStatus(checklistId: Long, status: ChecklistInstanceStatusDTO): ChecklistInstanceDTO =
        updateInteractor.changeStatus(checklistId, ChecklistInstanceStatus.valueOf(status.name)).toDto()

    override fun consolidateChecklistInstance(checklistId: Long, options: ChecklistConsolidatorOptionsDTO): Boolean =
        consolidateInteractor.consolidateChecklistInstance(checklistId, options.consolidated)

    override fun updateChecklistInstanceSelection(selection: Map<Long, Boolean>) =
        updateInteractor.updateSelection(selection)

    override fun updateChecklistDescription(checklistId: Long, description: String?): ChecklistInstanceDTO =
        updateInteractor.updateDescription(checklistId, description).toDto()

    override fun deleteChecklistInstance(checklistId: Long, projectId: Long) =
        deleteInteractor.deleteById(checklistId, projectId)

    override fun exportChecklistInstance(
        projectId: Long,
        checklistId: Long,
        exportLanguage: SystemLanguage,
        pluginKey: String?,
    ): ResponseEntity<ByteArrayResource> =
        exportInteractor.export(relatedToId = projectId, checklistId, exportLanguage, pluginKey).toResponseEntity()
}
