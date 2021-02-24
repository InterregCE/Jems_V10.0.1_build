package io.cloudflight.jems.server.programme.repository.legalstatus

import io.cloudflight.jems.server.call.repository.flatrate.CallRepository
import io.cloudflight.jems.server.programme.repository.ProgrammePersistenceProvider
import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeLegalStatusPersistenceProvider(
    private val repository: ProgrammeLegalStatusRepository,
    private val callRepository: CallRepository,
) : ProgrammeLegalStatusPersistence, ProgrammePersistenceProvider(callRepository) {

    @Transactional(readOnly = true)
    override fun getMax20Statuses(): List<ProgrammeLegalStatus> =
        repository.findTop20ByOrderById().toModel()

    @Transactional
    override fun updateLegalStatuses(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeLegalStatus>
    ): List<ProgrammeLegalStatus> {
        if (!isProgrammeSetupRestricted())
            repository.deleteInBatch(repository.findAllById(toDeleteIds))
        repository.saveAll(toPersist.toEntity()).toModel()
        return repository.findTop20ByOrderById().toModel()
    }

    @Transactional(readOnly = true)
    override fun getCount(): Long = repository.count()

}
