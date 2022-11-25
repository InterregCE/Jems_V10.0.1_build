package io.cloudflight.jems.server.project.service.checklist.update.control

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstanceValidator
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectChecklistStatusChanged
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateControlChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val checklistInstanceValidator: ChecklistInstanceValidator,
    private val userAuthorization: UserAuthorization,
    private val partnerPersistence: PartnerPersistence
) : UpdateControlChecklistInstanceInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(UpdateControlChecklistInstanceException::class)
    override fun update(partnerId: Long, reportId: Long, checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        val existing = persistence.getChecklistDetail(checklist.id, ProgrammeChecklistType.CONTROL, reportId)

        if (existing.status != checklist.status || (userAuthorization.getUser().email != existing.creatorEmail))
            throw UpdateControlChecklistInstanceStatusNotAllowedException()

        checklistInstanceValidator.validateChecklistComponents(checklist.components)

        return persistence.update(checklist)
    }

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(UpdateControlChecklistInstanceStatusException::class)
    override fun changeStatus(
        partnerId: Long,
        reportId: Long,
        checklistId: Long,
        status: ChecklistInstanceStatus
    ): ChecklistInstance {
        val partner = partnerPersistence.getById(partnerId)

        val existing = persistence.getChecklistSummary(checklistId, ProgrammeChecklistType.CONTROL, reportId)

        val isReturnToDraft = existing.status == ChecklistInstanceStatus.FINISHED
                && status == ChecklistInstanceStatus.DRAFT

        val userCanFinish = existing.status == ChecklistInstanceStatus.DRAFT
                && status == ChecklistInstanceStatus.FINISHED
                && userAuthorization.getUser().email == existing.creatorEmail

        if (!isReturnToDraft && !userCanFinish)
            throw UpdateControlChecklistInstanceStatusNotAllowedException()

        return persistence.changeStatus(checklistId, status).also {
            auditPublisher.publishEvent(
                projectChecklistStatusChanged(
                    context = this,
                    checklist = it,
                    oldStatus = existing.status,
                    projectId = partner.projectId,
                    partner = partner,
                    reportId = reportId
                )
            )
        }
    }
}
