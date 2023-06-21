package io.cloudflight.jems.server.project.service.checklist.getInstances.contracting

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.checklist.ContractingChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingChecklistInstances(
    private val persistence: ContractingChecklistInstancePersistence,
) : GetContractingChecklistInstancesInteractor {

    @CanRetrieveProjectContractingMonitoring
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetContractingChecklistInstanceException::class)
    override fun getContractingChecklistInstances(projectId: Long): List<ChecklistInstance> {
        return persistence.findChecklistInstances(
            ChecklistInstanceSearchRequest(
                type = ProgrammeChecklistType.CONTRACTING,
                relatedToId = projectId
            )
        )
    }
}
