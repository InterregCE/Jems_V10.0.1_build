package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeUnitCostPersistenceProvider(
    private val repository: ProgrammeUnitCostRepository,
) : ProgrammeUnitCostPersistence {

    @Transactional(readOnly = true)
    override fun getUnitCosts(pageable: Pageable): Page<ProgrammeUnitCost> =
        repository.findAll(pageable).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getUnitCost(unitCostId: Long): ProgrammeUnitCost =
        getUnitCostOrThrow(unitCostId).toModel()

    @Transactional
    override fun createUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        val created = repository.save(unitCost.toEntity())
        return repository.save(created.copy(
            categories = unitCost.categories.toBudgetCategoryEntity(created.id)
        )).toModel()
    }

    @Transactional
    override fun updateUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        val unitCostEntity = getUnitCostOrThrow(unitCostId = unitCost.id!!)
        unitCostEntity.name = unitCost.name!!
        unitCostEntity.description = unitCost.description
        unitCostEntity.type = unitCost.type!!
        unitCostEntity.costPerUnit = unitCost.costPerUnit!!
        unitCostEntity.categories.clear()
        unitCostEntity.categories.addAll(unitCost.categories.toBudgetCategoryEntity(unitCost.id))
        return unitCostEntity.toModel()
    }

    @Transactional
    override fun deleteUnitCost(unitCostId: Long) =
        repository.delete(getUnitCostOrThrow(unitCostId))

    private fun getUnitCostOrThrow(unitCostId: Long): ProgrammeUnitCostEntity =
        repository.findById(unitCostId).orElseThrow { ResourceNotFoundException("programmeUnitCost") }

}
