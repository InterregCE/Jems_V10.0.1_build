package io.cloudflight.jems.server.project.service.checklist.getDetail

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
    private val checklistAuthorization: ProjectChecklistAuthorization
) : GetChecklistInstanceDetailInteractor {

    @CanViewChecklistAssessment
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetChecklistInstanceDetailNotFoundException::class)
    override fun getChecklistInstanceDetail(id: Long, relatedToId: Long): ChecklistInstanceDetail {
        val checklistDetail = persistence.getChecklistDetail(id)
        if (relatedToId != checklistDetail.relatedToId ||
            (checklistIsNotVisible(checklistDetail) && isNotAllowedToUpdate(relatedToId))
        ) {
            throw GetChecklistDetailNotAllowedException()
        }
        return checklistDetail
    }

    private fun checklistIsNotVisible(checklistDetail :ChecklistInstanceDetail
    ): Boolean = !checklistDetail.visible

    private fun isNotAllowedToUpdate(relatedToId: Long) =
        !(checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedUpdate) ||
            checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistUpdate, relatedToId))
}
