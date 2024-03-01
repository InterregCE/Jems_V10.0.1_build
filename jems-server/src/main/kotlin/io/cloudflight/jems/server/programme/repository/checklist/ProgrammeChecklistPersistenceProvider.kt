package io.cloudflight.jems.server.programme.repository.checklist

import io.cloudflight.jems.server.call.repository.CallSelectedChecklistRepository
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.getList.GetProgrammeChecklistDetailNotFoundException
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.toJson
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeChecklistPersistenceProvider(
    private val repository: ProgrammeChecklistRepository,
    private val callSelectedChecklistRepository: CallSelectedChecklistRepository,
) : ProgrammeChecklistPersistence {

    @Transactional(readOnly = true)
    override fun getMax100Checklists(sort: Sort): List<ProgrammeChecklist> {
        return repository.findTop100ByOrderByIdDesc(sort).toModel()
    }

    @Transactional(readOnly = true)
    override fun getChecklistDetail(id: Long): ProgrammeChecklistDetail {
        return getProgrammeChecklistOrThrow(id).toDetailModel()
    }

    @Transactional
    override fun createChecklist(checklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail {
        return repository.save(checklist.toEntity()).toDetailModel()
    }

    @Transactional
    override fun updateChecklist(checklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail {
        val checklistEntity = getProgrammeChecklistOrThrow(checklist.id!!).apply {
            name = checklist.name
            minScore = checklist.minScore
            maxScore = checklist.maxScore
            allowsDecimalScore = checklist.allowsDecimalScore
        }
        val toCreate = checklist.components?.filter { it.isNew() } ?: emptyList()
        val byId = checklist.components?.filter { !it.isNew() }?.associateBy { it.id } ?: emptyMap()
        val toDeleteIds = checklistEntity.components?.mapTo(HashSet()) { it.id }?.minus(byId.keys) ?: emptySet()

        checklistEntity.components?.addAll(toCreate.map { it.toEntity(checklistEntity) })
        checklistEntity.components?.removeAll { toDeleteIds.contains(it.id) }
        checklistEntity.components?.filter { it.id in byId.keys }?.associateWith { byId[it.id]!! }?.forEach { (old, new) ->
            old.type = new.type
            old.positionOnTable = new.position
            old.metadata = new.metadata?.toJson()
        }

        return checklistEntity.toDetailModel()
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

    @Transactional(readOnly = true)
    override fun getChecklistsByTypeAndCall(checklistType: ProgrammeChecklistType, callId: Long): List<IdNamePair> {
        return callSelectedChecklistRepository.findAllByIdCallIdAndIdProgrammeChecklistType(callId, checklistType)
            .map { IdNamePair(id = it.id.programmeChecklist.id, name = it.id.programmeChecklist.name!!) }
    }

    private fun getProgrammeChecklistOrThrow(id: Long): ProgrammeChecklistEntity =
        repository.findById(id).orElseThrow { GetProgrammeChecklistDetailNotFoundException() }

    private fun ProgrammeChecklistComponent.isNew(): Boolean {
        return this.id == null || this.id == 0L
    }
}
