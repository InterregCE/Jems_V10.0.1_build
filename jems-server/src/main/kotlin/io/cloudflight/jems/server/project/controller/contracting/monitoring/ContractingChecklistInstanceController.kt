package io.cloudflight.jems.server.project.controller.contracting.monitoring

import io.cloudflight.jems.api.project.contracting.ContractingChecklistInstanceApi
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.cloudflight.jems.server.project.controller.checklist.toDetailDto
import io.cloudflight.jems.server.project.controller.checklist.toDetailModel
import io.cloudflight.jems.server.project.controller.checklist.toDto
import io.cloudflight.jems.server.project.controller.checklist.toModel
import io.cloudflight.jems.server.project.service.checklist.create.contracting.CreateContractingChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.contracting.DeleteContractingChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.contracting.GetContractingChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.contracting.GetContractingChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.update.contracting.UpdateContractingChecklistInstanceInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingChecklistInstanceController(
    private val getContractingChecklistInteractor: GetContractingChecklistInstancesInteractor,
    private val getContractingChecklistDetailInteractor: GetContractingChecklistInstanceDetailInteractor,
    private val updateInteractor: UpdateContractingChecklistInstanceInteractor,
    private val createInteractor: CreateContractingChecklistInstanceInteractor,
    private val deleteInteractor: DeleteContractingChecklistInstanceInteractor,
) : ContractingChecklistInstanceApi {

    override fun getAllContractingChecklistInstances(projectId: Long): List<ChecklistInstanceDTO> =
        getContractingChecklistInteractor.getContractingChecklistInstances(projectId).toDto()

    override fun getContractingChecklistInstanceDetail(projectId: Long, checklistId: Long): ChecklistInstanceDetailDTO =
        getContractingChecklistDetailInteractor.getContractingChecklistInstanceDetail(projectId, checklistId)
            .toDetailDto()

    override fun createContractingChecklistInstance(
        projectId: Long,
        checklist: CreateChecklistInstanceDTO
    ): ChecklistInstanceDetailDTO =
        createInteractor.create(projectId, checklist.toModel()).toDetailDto()

    override fun updateContractingChecklistInstance(
        projectId: Long,
        checklist: ChecklistInstanceDetailDTO
    ): ChecklistInstanceDetailDTO =
        updateInteractor.update(projectId, checklist.toDetailModel()).toDetailDto()

    override fun changeContractingChecklistStatus(
        projectId: Long,
        checklistId: Long,
        status: ChecklistInstanceStatusDTO
    ): ChecklistInstanceDTO =
        updateInteractor.changeStatus(projectId, checklistId, ChecklistInstanceStatus.valueOf(status.name)).toDto()

    override fun deleteContractingChecklistInstance(projectId: Long, checklistId: Long) =
        deleteInteractor.deleteById(projectId, checklistId)
}
