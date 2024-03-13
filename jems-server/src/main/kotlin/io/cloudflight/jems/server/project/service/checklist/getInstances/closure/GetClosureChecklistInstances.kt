package io.cloudflight.jems.server.project.service.checklist.getInstances.closure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.checklist.ClosureChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetClosureChecklistInstances(
    private val closureChecklistInstancePersistence: ClosureChecklistInstancePersistence,
    private val projectReportPersistence: ProjectReportPersistence
): GetClosureChecklistInstancesInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetClosureChecklistInstanceException::class)
    override fun getClosureChecklistInstances(projectId: Long, reportId: Long): List<ChecklistInstance> =
        closureChecklistInstancePersistence.findChecklistInstances(
            ChecklistInstanceSearchRequest(
                type = ProgrammeChecklistType.CLOSURE,
                relatedToId = projectReportPersistence.getReportById(projectId, reportId).id
            )
        )
}
