package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeLumpSumPersistenceProvider(
    private val repository: ProgrammeLumpSumRepository,
) : ProgrammeLumpSumPersistence {

    @Transactional(readOnly = true)
    override fun getLumpSums(): List<ProgrammeLumpSum> =
        repository.findTop25ByOrderById().map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getLumpSum(lumpSumId: Long): ProgrammeLumpSum =
        getLumpSumOrThrow(lumpSumId).toModel()

    @Transactional(readOnly = true)
    override fun getCount(): Long = repository.count()

    @Transactional
    override fun createLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum {
        val created = repository.save(lumpSum.toEntity())
        return repository.save(created.copy(
            categories = lumpSum.categories.toEntity(created.id)
        )).toModel()
    }

    @Transactional
    override fun updateLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum {
        val lumpSumEntity = getLumpSumOrThrow(lumpSumId = lumpSum.id!!)
        lumpSumEntity.name = lumpSum.name!!
        lumpSumEntity.description = lumpSum.description
        lumpSumEntity.cost = lumpSum.cost!!
        lumpSumEntity.splittingAllowed = lumpSum.splittingAllowed
        lumpSumEntity.phase = lumpSum.phase!!
        lumpSumEntity.categories.clear()
        lumpSumEntity.categories.addAll(lumpSum.categories.toEntity(lumpSum.id))
        return lumpSumEntity.toModel()
    }

    @Transactional
    override fun deleteLumpSum(lumpSumId: Long) =
        repository.delete(getLumpSumOrThrow(lumpSumId))

    private fun getLumpSumOrThrow(lumpSumId: Long): ProgrammeLumpSumEntity =
        repository.findById(lumpSumId).orElseThrow { ResourceNotFoundException("programmeLumpSum") }

}
