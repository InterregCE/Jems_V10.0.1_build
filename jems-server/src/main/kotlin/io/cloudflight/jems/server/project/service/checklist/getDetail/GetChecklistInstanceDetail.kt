package io.cloudflight.jems.server.project.service.checklist.getDetail

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
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
        val checklistDetail = persistence.getChecklistDetail(id)
        if ((isNotAllowedToUpdate(relatedToId, checklistDetail) &&
                (checkListIsNotVisible(checklistDetail) || !canViewSelectedAssessmentsList(relatedToId)))  ||
                checklistDetail.relatedToId != relatedToId
        ) {
            throw GetChecklistDetailNotAllowedException()
        }
        return checklistDetail
    }

    private fun isNotAllowedToUpdate(relatedToId: Long, checklistDetail :ChecklistInstanceDetail) =
        !(canInstantiateChecklistAndIsOwnerOfChecklist(relatedToId, checklistDetail) || canConsolidateChecklist(relatedToId) ||
            checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedUpdate))

    private fun canInstantiateChecklistAndIsOwnerOfChecklist(relatedToId: Long, checklistDetail :ChecklistInstanceDetail): Boolean =
        checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistUpdate, relatedToId) &&
        securityService.getUserIdOrThrow() == checklistDetail.creatorId

    private fun canConsolidateChecklist(relatedToId: Long) =
        checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistConsolidate, relatedToId)

    private fun canViewSelectedAssessmentsList(relatedToId: Long) =
        checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedRetrieve, relatedToId)

    private fun checkListIsNotVisible(checklistDetail :ChecklistInstanceDetail) = !checklistDetail.visible
}
