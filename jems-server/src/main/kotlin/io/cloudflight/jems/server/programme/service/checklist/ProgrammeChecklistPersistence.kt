package io.cloudflight.jems.server.programme.service.checklist

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import org.springframework.data.domain.Sort

interface ProgrammeChecklistPersistence {

    fun getMax100Checklists(sort: Sort): List<ProgrammeChecklist>

    fun getChecklistDetail(id: Long): ProgrammeChecklistDetail

    fun createOrUpdate(checklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail

    fun deleteById(id: Long)

    fun countAll(): Long

    fun getChecklistsByType(checklistType: ProgrammeChecklistType): List<IdNamePair>

}
