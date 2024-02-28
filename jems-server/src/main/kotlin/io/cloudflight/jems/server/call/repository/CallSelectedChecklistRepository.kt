package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.server.call.entity.CallSelectedChecklistEntity
import io.cloudflight.jems.server.call.entity.CallSelectedChecklistId
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CallSelectedChecklistRepository : JpaRepository<CallSelectedChecklistEntity, CallSelectedChecklistId> {
    fun findAllByIdCallId(callId: Long): MutableList<CallSelectedChecklistEntity>

    fun findAllByIdCallIdAndIdProgrammeChecklistType(callId: Long, type: ProgrammeChecklistType): MutableList<CallSelectedChecklistEntity>
}
