package io.cloudflight.jems.server.project.controller.controlChecklist

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.controlChecklist.ControlChecklistInstanceApi
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.project.controller.checklist.toDetailDto
import io.cloudflight.jems.server.project.controller.checklist.toDetailModel
import io.cloudflight.jems.server.project.controller.checklist.toDto
import io.cloudflight.jems.server.project.controller.checklist.toModel
import io.cloudflight.jems.server.project.service.checklist.clone.control.CloneControlChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.create.control.CreateControlChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.control.DeleteControlChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.export.control.ExportControlChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.control.GetControlChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.control.GetControlChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.update.control.UpdateControlChecklistInstanceInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ControlChecklistInstanceController(
    private val getControlChecklistInteractor: GetControlChecklistInstancesInteractor,
    private val getControlChecklistDetailInteractor: GetControlChecklistInstanceDetailInteractor,
    private val updateInteractor: UpdateControlChecklistInstanceInteractor,
    private val createInteractor: CreateControlChecklistInstanceInteractor,
    private val deleteInteractor: DeleteControlChecklistInstanceInteractor,
    private val exportInteractor: ExportControlChecklistInstanceInteractor,
    private val cloneInteractor: CloneControlChecklistInstanceInteractor
) : ControlChecklistInstanceApi {

    override fun getAllControlChecklistInstances(partnerId: Long, reportId: Long): List<ChecklistInstanceDTO> =
        getControlChecklistInteractor.getControlChecklistInstances(partnerId, reportId).toDto()

    override fun getControlChecklistInstanceDetail(partnerId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetailDTO =
        getControlChecklistDetailInteractor.getControlChecklistInstanceDetail(partnerId, reportId, checklistId).toDetailDto()

    override fun createControlChecklistInstance(partnerId: Long, reportId: Long, checklist: CreateChecklistInstanceDTO): ChecklistInstanceDetailDTO =
        createInteractor.create(partnerId, reportId, checklist.toModel()).toDetailDto()

    override fun updateControlChecklistInstance(partnerId: Long, reportId: Long, checklist: ChecklistInstanceDetailDTO): ChecklistInstanceDetailDTO =
        updateInteractor.update(partnerId, reportId, checklist.toDetailModel()).toDetailDto()

    override fun changeControlChecklistStatus(partnerId: Long, reportId: Long, checklistId: Long, status: ChecklistInstanceStatusDTO): ChecklistInstanceDTO =
        updateInteractor.changeStatus(partnerId, reportId, checklistId, ChecklistInstanceStatus.valueOf(status.name)).toDto()

    override fun deleteControlChecklistInstance(partnerId: Long, reportId: Long, checklistId: Long) =
        deleteInteractor.deleteById(partnerId, reportId, checklistId)

    override fun updateControlChecklistDescription(
        partnerId: Long,
        reportId: Long,
        checklistId: Long,
        description: String?
    ): ChecklistInstanceDTO =
        updateInteractor.updateDescription(partnerId, reportId, checklistId, description).toDto()

    override fun exportControlChecklistInstance(
        partnerId: Long,
        reportId: Long,
        checklistId: Long,
        exportLanguage: SystemLanguage,
        pluginKey: String?
    ): ResponseEntity<ByteArrayResource> =
        exportInteractor.export(partnerId, reportId, checklistId, exportLanguage, pluginKey).toResponseEntity()

    override fun cloneControlChecklistInstance(partnerId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetailDTO =
        cloneInteractor.clone(partnerId, reportId, checklistId).toDetailDto()
}
