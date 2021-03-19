package io.cloudflight.jems.server.programme.repository.legalstatus

import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeLegalStatusPersistenceProvider(
    private val repository: ProgrammeLegalStatusRepository
) : ProgrammeLegalStatusPersistence {

    @Transactional(readOnly = true)
    override fun getMax20Statuses(): List<ProgrammeLegalStatus> =
        repository.findTop20ByOrderById().toModel()

    @Transactional
    override fun updateLegalStatuses(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeLegalStatus>
    ): List<ProgrammeLegalStatus> {
        repository.deleteInBatch(repository.findAllById(toDeleteIds))
        repository.saveAll(toPersist.toEntity()).toModel()
        return repository.findTop20ByOrderById().toModel()
    }

    @Transactional(readOnly = true)
    override fun getByType(types: List<ProgrammeLegalStatusType>): List<ProgrammeLegalStatus> =
        repository.findByTypeIn(types).toModel()


    @Transactional(readOnly = true)
    override fun getCount(): Long = repository.count()

}
