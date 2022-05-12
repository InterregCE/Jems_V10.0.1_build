package io.cloudflight.jems.server.project.service.checklist.getDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetChecklistInstanceDetail(
    private val persistence: ChecklistInstancePersistence,
) : GetChecklistInstanceDetailInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetChecklistInstanceDetailNotFoundException::class)
    override fun getChecklistInstanceDetail(id: Long): ChecklistInstanceDetail =
        persistence.getChecklistDetail(id)
}
