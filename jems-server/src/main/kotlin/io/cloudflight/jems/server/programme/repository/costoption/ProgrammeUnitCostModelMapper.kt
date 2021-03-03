package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslId
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

fun ProgrammeUnitCostEntity.toProgrammeUnitCost() = ProgrammeUnitCost(
    id = id,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    description = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) },
    type = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.type) },
    costPerUnit = costPerUnit,
    isOneCostCategory = isOneCostCategory,
    categories = categories.mapTo(HashSet()) { it.category }
)

fun Iterable<ProgrammeUnitCostEntity>.toProgrammeUnitCost() = map { it.toProgrammeUnitCost() }

fun ProgrammeUnitCost.toEntity() = ProgrammeUnitCostEntity(
    id = id ?: 0,
    // translatedValues - needs programmeUnitCostId
    costPerUnit = costPerUnit!!,
    isOneCostCategory = isOneCostCategory,
    categories = if (id == 0L) mutableSetOf() else categories.toBudgetCategoryEntity(id)
)

fun Collection<BudgetCategory>.toBudgetCategoryEntity(programmeUnitCostId: Long) = mapTo(HashSet()) {
    ProgrammeUnitCostBudgetCategoryEntity(programmeUnitCostId = programmeUnitCostId, category = it)
}

fun combineUnitCostTranslatedValues(
    programmeUnitCostId: Long,
    name: Set<InputTranslation>,
    description: Set<InputTranslation>,
    type: Set<InputTranslation>
): MutableSet<ProgrammeUnitCostTranslEntity> {
    val nameMap = name.associateBy( { it.language }, { it.translation } )
    val descriptionMap = description.associateBy( { it.language }, { it.translation } )
    val typeMap = type.associateBy( { it.language }, { it.translation } )

    val languages = nameMap.keys.toMutableSet()
    languages.addAll(descriptionMap.keys)
    languages.addAll(typeMap.keys)

    return languages.mapTo(HashSet()) {
        ProgrammeUnitCostTranslEntity(
            ProgrammeUnitCostTranslId(programmeUnitCostId, it),
            nameMap[it],
            descriptionMap[it],
            typeMap[it]
        )
    }
}
