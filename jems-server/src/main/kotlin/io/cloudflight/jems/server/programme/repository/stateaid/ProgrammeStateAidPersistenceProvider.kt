package io.cloudflight.jems.server.programme.repository.stateaid

import io.cloudflight.jems.server.programme.service.stateaid.ProgrammeStateAidPersistence
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import org.springframework.stereotype.Repository

@Repository
class ProgrammeStateAidPersistenceProvider(
    private val repository: ProgrammeStateAidRepository
) : ProgrammeStateAidPersistence {

    override fun getStateAidList(): List<ProgrammeStateAid> =
        repository.findTop20ByOrderById().toModel()

    override fun updateStateAids(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeStateAid>
    ): List<ProgrammeStateAid> {
        repository.deleteInBatch(repository.findAllById(toDeleteIds))
        repository.saveAll(toPersist.toEntity()).toModel()
        return repository.findTop20ByOrderById().toModel()
    }
}
