package io.cloudflight.jems.server.project.controller.verificationChecklist

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.cloudflight.jems.api.project.verificationChecklist.VerificationChecklistInstanceApi
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.project.controller.checklist.toDetailDto
import io.cloudflight.jems.server.project.controller.checklist.toDetailModel
import io.cloudflight.jems.server.project.controller.checklist.toDto
import io.cloudflight.jems.server.project.controller.checklist.toModel
import io.cloudflight.jems.server.project.service.checklist.create.verification.CreateVerificationChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.verification.DeleteVerificationChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.export.verification.ExportVerificationChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.verification.GetVerificationChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.verification.GetVerificationChecklistsInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.update.verification.UpdateVerificationChecklistInstanceInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class VerificationChecklistInstanceController(
    private val getVerificationChecklistInteractor: GetVerificationChecklistsInstancesInteractor,
    private val getVerificationChecklistDetailInteractor: GetVerificationChecklistInstanceDetailInteractor,
    private val updateInteractor: UpdateVerificationChecklistInstanceInteractor,
    private val createInteractor: CreateVerificationChecklistInstanceInteractor,
    private val deleteInteractor: DeleteVerificationChecklistInstanceInteractor,
    private val exportInteractor: ExportVerificationChecklistInstanceInteractor,
) : VerificationChecklistInstanceApi {

    override fun getAllVerificationChecklistInstances(projectId: Long, reportId: Long): List<ChecklistInstanceDTO> =
        getVerificationChecklistInteractor.getVerificationChecklistInstances(projectId, reportId).toDto()

    override fun getVerificationChecklistInstanceDetail(projectId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetailDTO =
        getVerificationChecklistDetailInteractor.getVerificationChecklistInstanceDetail(projectId, reportId, checklistId).toDetailDto()

    override fun createVerificationChecklistInstance(projectId: Long, reportId: Long, checklist: CreateChecklistInstanceDTO): ChecklistInstanceDetailDTO =
        createInteractor.create(projectId, reportId, checklist.toModel()).toDetailDto()

    override fun updateVerificationChecklistInstance(projectId: Long, reportId: Long, checklist: ChecklistInstanceDetailDTO): ChecklistInstanceDetailDTO =
        updateInteractor.update(projectId, reportId, checklist.toDetailModel()).toDetailDto()

    override fun changeVerificationChecklistStatus(
        projectId: Long,
        reportId: Long,
        checklistId: Long,
        status: ChecklistInstanceStatusDTO
    ): ChecklistInstanceDTO =
        updateInteractor.changeStatus(projectId, reportId, checklistId, ChecklistInstanceStatus.valueOf(status.name)).toDto()

    override fun deleteVerificationChecklistInstance(projectId: Long, reportId: Long, checklistId: Long) =
        deleteInteractor.deleteById(projectId, reportId, checklistId)

    override fun updateVerificationChecklistDescription(
        projectId: Long,
        reportId: Long,
        checklistId: Long,
        description: String?
    ): ChecklistInstanceDTO =
        updateInteractor.updateDescription(projectId, reportId, checklistId, description).toDto()

    override fun exportVerificationChecklistInstance(
        projectId: Long,
        reportId: Long,
        checklistId: Long,
        exportLanguage: SystemLanguage,
        pluginKey: String?
    ): ResponseEntity<ByteArrayResource> =
        exportInteractor.export(projectId, reportId, checklistId, exportLanguage, pluginKey).toResponseEntity()

}
