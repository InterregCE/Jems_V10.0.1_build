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
        repository.findTop25ByOrderById().map { it.toProgrammeLumpSum() }

    @Transactional(readOnly = true)
    override fun getLumpSum(lumpSumId: Long): ProgrammeLumpSum =
        getLumpSumOrThrow(lumpSumId).toProgrammeLumpSum()

    @Transactional(readOnly = true)
    override fun getCount(): Long = repository.count()

    @Transactional
    override fun createLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum {
        val created = repository.save(lumpSum.toEntity())
        return repository.save(created.copy(
            translatedValues = combineLumpSumTranslatedValues(created.id, lumpSum.name, lumpSum.description),
            categories = lumpSum.categories.toEntity(created.id)
        )).toProgrammeLumpSum()
    }

    @Transactional
    override fun updateLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum {
        val lumpSumEntity = getLumpSumOrThrow(lumpSumId = lumpSum.id!!)
        lumpSumEntity.translatedValues = combineLumpSumTranslatedValues(lumpSum.id, lumpSum.name, lumpSum.description)
        lumpSumEntity.cost = lumpSum.cost!!
        lumpSumEntity.splittingAllowed = lumpSum.splittingAllowed
        lumpSumEntity.phase = lumpSum.phase!!
        lumpSumEntity.categories.clear()
        lumpSumEntity.categories.addAll(lumpSum.categories.toEntity(lumpSum.id))
        return lumpSumEntity.toProgrammeLumpSum()
    }

    @Transactional
    override fun deleteLumpSum(lumpSumId: Long) =
        repository.delete(getLumpSumOrThrow(lumpSumId))

    private fun getLumpSumOrThrow(lumpSumId: Long): ProgrammeLumpSumEntity =
        repository.findById(lumpSumId).orElseThrow { ResourceNotFoundException("programmeLumpSum") }

}
