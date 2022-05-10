package io.cloudflight.jems.server.project.service.checklist.getMyInstances

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyChecklistInstances(
    private val persistence: ChecklistInstancePersistence,
    private val securityService: SecurityService
) : GetMyChecklistInstancesInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetChecklistInstanceException::class)
    override fun getChecklistInstancesOfCurrentUserByTypeAndRelatedId(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance> =
        persistence.getChecklistsByRelationAndCreatorAndType(
            relatedToId = relatedToId,
            creatorId = securityService.currentUser?.user?.id!!,
            type = type
        )
}
