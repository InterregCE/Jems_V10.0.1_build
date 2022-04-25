package io.cloudflight.jems.server.programme.repository.checklist

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.getList.GetProgrammeChecklistDetailNotFoundException
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeChecklistPersistenceProvider(
    private val repository: ProgrammeChecklistRepository
) : ProgrammeChecklistPersistence {

    @Transactional(readOnly = true)
    override fun getMax100Checklists(): List<ProgrammeChecklist> {
        return repository.findTop100ByOrderById().toModel()
    }

    @Transactional(readOnly = true)
    override fun getChecklistDetail(id: Long): ProgrammeChecklistDetail {
        return getProgrammeChecklistOrThrow(id).toDetailModel()
    }

    @Transactional
    override fun createOrUpdate(checklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail {
        return repository.save(checklist.toEntity()).toDetailModel()
    }

    @Transactional
    override fun deleteById(id: Long) {
        repository.delete(getProgrammeChecklistOrThrow(id))
    }

    @Transactional(readOnly = true)
    override fun countAll(): Long {
        return repository.count()
    }

    @Transactional(readOnly = true)
    override fun getChecklistsByType(checklistType: ProgrammeChecklistType): List<IdNamePair> {
        return repository.findByType(checklistType).toList()
    }

    private fun getProgrammeChecklistOrThrow(id: Long): ProgrammeChecklistEntity =
        repository.findById(id).orElseThrow { GetProgrammeChecklistDetailNotFoundException() }
}
