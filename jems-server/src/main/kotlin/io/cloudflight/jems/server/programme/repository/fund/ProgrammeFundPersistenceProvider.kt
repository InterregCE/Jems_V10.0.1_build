package io.cloudflight.jems.server.programme.repository.fund

import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeFundPersistenceProvider(
    private val repository: ProgrammeFundRepository
) : ProgrammeFundPersistence {

    @Transactional(readOnly = true)
    override fun getMax20Funds(): List<ProgrammeFund> =
        repository.findTop20ByOrderById().toModel()

    @Transactional
    override fun updateFunds(toDeleteIds: Set<Long>, funds: Set<ProgrammeFund>): List<ProgrammeFund> {
        repository.deleteAllByIdInBatch(toDeleteIds)
        return repository.saveAll(funds.toEntity()).toModel()
    }

}
