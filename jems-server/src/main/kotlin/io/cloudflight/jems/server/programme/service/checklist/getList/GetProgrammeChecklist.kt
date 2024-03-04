package io.cloudflight.jems.server.programme.service.checklist.getList

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.service.ProjectPersistence
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
    private val projectPersistence: ProjectPersistence
) : GetProgrammeChecklistInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProgrammeChecklistException::class)
    override fun getProgrammeChecklist(sort: Sort): List<ProgrammeChecklist> =
        persistence.getMax100Checklists(sort)

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProgrammeChecklistException::class)
    override fun getProgrammeChecklistsByType(checklistType: ProgrammeChecklistType, projectId: Long?): List<IdNamePair> {
        return if (projectId == null) {
            persistence.getChecklistsByType(checklistType)
        } else {
            val project = projectPersistence.getProjectSummary(projectId)
            persistence.getChecklistsByTypeAndCall(checklistType, project.callId)
        }
    }
}
