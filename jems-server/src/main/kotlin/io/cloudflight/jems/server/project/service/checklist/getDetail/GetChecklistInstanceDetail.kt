package io.cloudflight.jems.server.project.service.checklist.getDetail

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanViewChecklistAssessment
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistDetailNotAllowedException
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetChecklistInstanceDetail(
    private val persistence: ChecklistInstancePersistence,
    private val checklistAuthorization: ProjectChecklistAuthorization,
    private val securityService: SecurityService
) : GetChecklistInstanceDetailInteractor {

    @CanViewChecklistAssessment
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetChecklistInstanceDetailNotFoundException::class)
    override fun getChecklistInstanceDetail(id: Long, relatedToId: Long): ChecklistInstanceDetail {
        val checklistDetail = persistence.getChecklistDetail(id = id, type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT, relatedToId = relatedToId)
        // relatedToId corresponds to projectId only if:
        //   checklistDetail.type == ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
        // also when another type was added, use relatedToId of checklistDetail instead of method parameter
        if (checklistDetail.relatedToId != relatedToId
            || (isNotAllowedToUpdate(relatedToId, checklistDetail) && (isNotVisible(checklistDetail) || canNotViewChecklist(relatedToId)))
        ) {
            throw GetChecklistDetailNotAllowedException()
        }
        return checklistDetail
    }

    private fun isNotAllowedToUpdate(relatedToId: Long, checklistDetail: ChecklistInstanceDetail) =
        !(canInstantiateChecklistAndIsOwnerOfChecklist(relatedToId, checklistDetail) || canConsolidateChecklist(relatedToId) ||
                checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedUpdate))

    private fun canInstantiateChecklistAndIsOwnerOfChecklist(relatedToId: Long, checklistDetail: ChecklistInstanceDetail): Boolean =
        checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistUpdate, relatedToId) &&
                securityService.getUserIdOrThrow() == checklistDetail.creatorId

    private fun canConsolidateChecklist(relatedToId: Long) =
        checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistConsolidate, relatedToId)

    private fun canNotViewChecklist(relatedToId: Long) =
        !checklistAuthorization.hasPermissionOrAsController(UserRolePermission.ProjectAssessmentChecklistSelectedRetrieve, relatedToId)

    private fun isNotVisible(checklistDetail: ChecklistInstanceDetail) = !checklistDetail.visible
}
