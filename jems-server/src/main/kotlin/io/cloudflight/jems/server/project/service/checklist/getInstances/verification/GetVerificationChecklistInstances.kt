package io.cloudflight.jems.server.project.service.checklist.getInstances.verification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanViewReportVerification
import io.cloudflight.jems.server.project.service.checklist.VerificationChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetVerificationChecklistInstances(
    private val persistence: VerificationChecklistInstancePersistence,
) : GetVerificationChecklistsInstancesInteractor {

    @CanViewReportVerification
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetVerificationChecklistInstanceException::class)
    override fun getVerificationChecklistInstances(
        projectId: Long,
        reportId: Long
    ): List<ChecklistInstance> {
        return persistence.findChecklistInstances(
            ChecklistInstanceSearchRequest(
            type = ProgrammeChecklistType.VERIFICATION,
            relatedToId = reportId
        ))
    }
}