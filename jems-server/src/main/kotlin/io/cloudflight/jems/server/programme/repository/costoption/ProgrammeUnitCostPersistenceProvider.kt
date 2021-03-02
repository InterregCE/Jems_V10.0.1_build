package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.repository.ProgrammePersistenceProvider
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeUnitCostPersistenceProvider(
    private val repository: ProgrammeUnitCostRepository,
    private val callRepository: CallRepository
) : ProgrammeUnitCostPersistence, ProgrammePersistenceProvider(callRepository) {

    @Transactional(readOnly = true)
    override fun getUnitCosts(): List<ProgrammeUnitCost> =
        repository.findTop25ByOrderById().toProgrammeUnitCost()

    @Transactional(readOnly = true)
    override fun getUnitCost(unitCostId: Long): ProgrammeUnitCost =
        getUnitCostOrThrow(unitCostId).toProgrammeUnitCost()

    @Transactional(readOnly = true)
    override fun getCount(): Long = repository.count()

    @Transactional
    override fun createUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        val created = repository.save(unitCost.toEntity())
        return repository.save(created.copy(
            translatedValues =
                combineUnitCostTranslatedValues(created.id, unitCost.name, unitCost.description, unitCost.type),
            categories = unitCost.categories.toBudgetCategoryEntity(created.id)
        )).toProgrammeUnitCost()
    }

    @Transactional
    override fun updateUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        if (repository.existsById(unitCost.id!!)) {
            return repository.save(
                unitCost.toEntity().copy(
                    translatedValues =
                        combineUnitCostTranslatedValues(unitCost.id, unitCost.name, unitCost.description, unitCost.type),
                    categories = unitCost.categories.toBudgetCategoryEntity(unitCost.id)
                )).toProgrammeUnitCost()
        }
        else throw ResourceNotFoundException("programmeUnitCost")
    }

    @Transactional
    override fun deleteUnitCost(unitCostId: Long) =
        repository.delete(getUnitCostOrThrow(unitCostId))

    private fun getUnitCostOrThrow(unitCostId: Long): ProgrammeUnitCostEntity =
        repository.findById(unitCostId).orElseThrow { ResourceNotFoundException("programmeUnitCost") }

}
