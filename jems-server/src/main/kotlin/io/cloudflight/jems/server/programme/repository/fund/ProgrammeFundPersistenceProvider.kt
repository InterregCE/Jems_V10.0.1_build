package io.cloudflight.jems.server.programme.repository.fund

import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.programme.repository.ProgrammePersistenceProvider
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeFundPersistenceProvider(
    private val repository: ProgrammeFundRepository,
    private val callRepository: CallRepository,
) : ProgrammeFundPersistence, ProgrammePersistenceProvider(callRepository) {

    @Transactional(readOnly = true)
    override fun getMax20Funds(): List<ProgrammeFund> =
        repository.findTop20ByOrderById().toModel()

    @Transactional
    override fun updateFunds(toDeleteIds: Set<Long>, funds: Set<ProgrammeFund>): List<ProgrammeFund> {
        repository.deleteInBatch(repository.findAllById(toDeleteIds))
        return repository.saveAll(funds.toEntity()).toModel()
    }

}
