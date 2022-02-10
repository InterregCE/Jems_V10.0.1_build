package io.cloudflight.jems.server.programme.repository.stateaid

import io.cloudflight.jems.server.programme.service.stateaid.ProgrammeStateAidPersistence
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeStateAidPersistenceProvider(
    private val repository: ProgrammeStateAidRepository
) : ProgrammeStateAidPersistence {

    @Transactional(readOnly = true)
    override fun getStateAidList(): List<ProgrammeStateAid> =
        repository.findAllByOrderById().toModel()

    @Transactional
    override fun updateStateAids(
        toDeleteIds: Set<Long>, toPersist: Collection<ProgrammeStateAid>
    ): List<ProgrammeStateAid> {
        repository.deleteAllByIdInBatch(toDeleteIds)
        repository.saveAll(toPersist.toEntity()).toModel()
        return repository.findAllByOrderById().toModel()
    }
}
