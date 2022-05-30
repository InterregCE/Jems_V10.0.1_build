package io.cloudflight.jems.server.project.service.checklist.getInstances

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanViewChecklistAssessmentSelection
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.consolidateInstance.ConsolidateChecklistNotAllowed
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetChecklistInstances(
    private val persistence: ChecklistInstancePersistence,
    private val securityService: SecurityService,
    private val checklistAuthorization: ProjectChecklistAuthorization
) : GetChecklistInstancesInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetChecklistInstanceException::class)
    override fun getChecklistInstancesOfCurrentUserByTypeAndRelatedId(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance> =
        persistence.findChecklistInstances(ChecklistInstanceSearchRequest(
            relatedToId = relatedToId,
            creatorId = securityService.currentUser?.user?.id!!,
            type = type
        ))

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetChecklistInstanceException::class)
    override fun getChecklistInstancesByTypeAndRelatedId(
        relatedToId: Long, type: ProgrammeChecklistType
    ): List<ChecklistInstance> {
        if (!checklistAuthorization.canConsolidate(relatedToId)) {
            throw ConsolidateChecklistNotAllowed()
        }

        return persistence.findChecklistInstances(ChecklistInstanceSearchRequest(
            relatedToId = relatedToId,
            type = type
        ))
    }

    @CanViewChecklistAssessmentSelection
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetChecklistInstanceException::class)
    override fun getChecklistInstancesForSelection(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance> {
        return if (checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedUpdate))
            persistence.findChecklistInstances(ChecklistInstanceSearchRequest(
                relatedToId = relatedToId,
                type = type,
                status = ChecklistInstanceStatus.FINISHED
            ))
        else
            persistence.findChecklistInstances(ChecklistInstanceSearchRequest(
                relatedToId = relatedToId,
                type = type,
                visible = true,
                status = ChecklistInstanceStatus.FINISHED
            ))
    }
}
