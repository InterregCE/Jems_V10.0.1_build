package io.cloudflight.jems.server.project.service.checklist.getDetail.verification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanViewReportVerification
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getInstances.verification.GetVerificationChecklistInstanceException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetVerificationChecklistInstanceDetail(
    private val persistence: ChecklistInstancePersistence,
) : GetVerificationChecklistInstanceDetailInteractor {

    @CanViewReportVerification
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetVerificationChecklistInstanceException::class)
    override fun getVerificationChecklistInstanceDetail(
        projectId: Long,
        reportId: Long,
        checklistId: Long
    ): ChecklistInstanceDetail {
        return persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.VERIFICATION, reportId)
    }
}
