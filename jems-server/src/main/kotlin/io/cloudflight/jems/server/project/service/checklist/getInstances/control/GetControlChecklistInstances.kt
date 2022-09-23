package io.cloudflight.jems.server.project.service.checklist.getInstances.control

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.checklist.ControlChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetControlChecklistInstances(
    private val persistence: ControlChecklistInstancePersistence,
) : GetControlChecklistInstancesInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetControlChecklistInstanceException::class)
    override fun getControlChecklistInstances(
        partnerId: Long,
        reportId: Long
    ): List<ChecklistInstance> {
        return persistence.findChecklistInstances(
            ChecklistInstanceSearchRequest(
            type = ProgrammeChecklistType.CONTROL,
            relatedToId = reportId
        ))
    }
}