package io.cloudflight.jems.server.project.service.checklist.getDetail.control

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getInstances.control.GetControlChecklistInstanceException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetControlChecklistInstanceDetail(
    private val persistence: ChecklistInstancePersistence,
) : GetControlChecklistInstanceDetailInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetControlChecklistInstanceException::class)
    override fun getControlChecklistInstanceDetail(
        partnerId: Long,
        reportId: Long,
        checklistId: Long
    ): ChecklistInstanceDetail {
        return persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CONTROL, reportId)
    }
}
