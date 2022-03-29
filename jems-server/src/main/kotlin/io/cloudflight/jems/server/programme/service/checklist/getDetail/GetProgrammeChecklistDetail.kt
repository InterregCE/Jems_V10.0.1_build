package io.cloudflight.jems.server.programme.service.checklist.getDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.getList.GetProgrammeChecklistDetailNotFoundException
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProgrammeChecklistDetail(
    private val persistence: ProgrammeChecklistPersistence,
) : GetProgrammeChecklistDetailInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProgrammeChecklistDetailNotFoundException::class)
    override fun getProgrammeChecklistDetail(id: Long): ProgrammeChecklistDetail =
        persistence.getChecklistDetail(id)

}
