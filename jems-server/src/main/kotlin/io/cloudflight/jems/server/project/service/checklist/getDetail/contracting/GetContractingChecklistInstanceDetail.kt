package io.cloudflight.jems.server.project.service.checklist.getDetail.contracting

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getInstances.contracting.GetContractingChecklistInstanceException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingChecklistInstanceDetail(
    private val persistence: ChecklistInstancePersistence,
) : GetContractingChecklistInstanceDetailInteractor {

    @CanRetrieveProjectContractingMonitoring
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetContractingChecklistInstanceException::class)
    override fun getContractingChecklistInstanceDetail(projectId: Long, checklistId: Long): ChecklistInstanceDetail {
        return persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CONTRACTING, projectId)
    }
}
