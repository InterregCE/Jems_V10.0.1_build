package io.cloudflight.jems.server.project.controller.closureChecklist

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.closureChecklist.ClosureChecklistInstanceApi
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.project.controller.checklist.toDetailDto
import io.cloudflight.jems.server.project.controller.checklist.toDetailModel
import io.cloudflight.jems.server.project.controller.checklist.toDto
import io.cloudflight.jems.server.project.service.checklist.clone.closure.CloneClosureChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.create.closure.CreateClosureChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.closure.DeleteClosureChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.export.closure.ExportClosureChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.closure.GetClosureChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.closure.GetClosureChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.update.closure.UpdateClosureChecklistInstanceInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ClosureChecklistInstanceController(
    private val getChecklistsInteractor: GetClosureChecklistInstancesInteractor,
    private val getChecklistDetailInteractor: GetClosureChecklistInstanceDetailInteractor,
    private val updateInteractor: UpdateClosureChecklistInstanceInteractor,
    private val createInteractor: CreateClosureChecklistInstanceInteractor,
    private val deleteInteractor: DeleteClosureChecklistInstanceInteractor,
    private val exportInteractor: ExportClosureChecklistInstanceInteractor,
    private val cloneInteractor: CloneClosureChecklistInstanceInteractor,
): ClosureChecklistInstanceApi {

    override fun getAllClosureChecklistInstances(projectId: Long, reportId: Long): List<ChecklistInstanceDTO> =
        getChecklistsInteractor.getClosureChecklistInstances(projectId, reportId).toDto()

    override fun getClosureChecklistInstanceDetail(
        projectId: Long,
        reportId: Long,
        checklistId: Long
    ): ChecklistInstanceDetailDTO =
        getChecklistDetailInteractor.getClosureChecklistInstanceDetail(projectId, reportId, checklistId).toDetailDto()

    override fun createClosureChecklistInstance(
        projectId: Long,
        reportId: Long,
        programmeChecklistId: Long
    ): ChecklistInstanceDetailDTO =
        createInteractor.create(reportId, programmeChecklistId).toDetailDto()

    override fun updateClosureChecklistInstance(
        projectId: Long,
        reportId: Long,
        checklist: ChecklistInstanceDetailDTO
    ): ChecklistInstanceDetailDTO =
        updateInteractor.update(reportId, checklist.toDetailModel()).toDetailDto()

    override fun changeClosureChecklistStatus(
        projectId: Long,
        reportId: Long,
        checklistId: Long,
        status: ChecklistInstanceStatusDTO
    ): ChecklistInstanceDTO =
        updateInteractor.changeStatus(reportId, checklistId, ChecklistInstanceStatus.valueOf(status.name)).toDto()

    override fun updateClosureChecklistDescription(
        projectId: Long,
        reportId: Long,
        checklistId: Long,
        description: String?
    ): ChecklistInstanceDTO =
        updateInteractor.updateDescription(reportId, checklistId, description).toDto()

    override fun deleteClosureChecklistInstance(projectId: Long, reportId: Long, checklistId: Long) =
        deleteInteractor.deleteById(reportId, checklistId)

    override fun exportClosureChecklistInstance(
        projectId: Long,
        reportId: Long,
        checklistId: Long,
        exportLanguage: SystemLanguage,
        pluginKey: String?
    ): ResponseEntity<ByteArrayResource> =
        exportInteractor.export(projectId, reportId, checklistId, exportLanguage, pluginKey).toResponseEntity()

    override fun cloneClosureChecklistInstance(
        projectId: Long,
        reportId: Long,
        checklistId: Long
    ): ChecklistInstanceDetailDTO =
        cloneInteractor.clone(reportId, checklistId).toDetailDto()

}
