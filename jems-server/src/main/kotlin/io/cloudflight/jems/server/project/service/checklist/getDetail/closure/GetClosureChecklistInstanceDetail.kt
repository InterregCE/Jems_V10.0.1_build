package io.cloudflight.jems.server.project.service.checklist.getDetail.closure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getInstances.closure.GetClosureChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetClosureChecklistInstanceDetail(
    private val persistence: ChecklistInstancePersistence
): GetClosureChecklistInstanceDetailInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetClosureChecklistInstanceDetailNotFoundException::class)
    override fun getClosureChecklistInstanceDetail(
        projectId: Long,
        reportId: Long,
        checklistId: Long
    ): ChecklistInstanceDetail =
        persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CLOSURE, reportId)
}
