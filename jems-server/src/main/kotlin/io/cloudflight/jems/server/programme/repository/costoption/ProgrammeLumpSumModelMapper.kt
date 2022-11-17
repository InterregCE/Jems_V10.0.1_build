package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumTranslEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumTranslId
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum

fun ProgrammeLumpSumEntity.toModel() = ProgrammeLumpSum(
    id = id,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    description = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) },
    cost = cost,
    splittingAllowed = splittingAllowed,
    fastTrack = isFastTrack,
    phase = phase,
    categories = categories.mapTo(HashSet()) { it.category }
)

fun Iterable<ProgrammeLumpSumEntity>.toModel() = map { it.toModel() }

fun ProgrammeLumpSum.toEntity() = ProgrammeLumpSumEntity(
    id = id,
    // translatedValues - needs programmeLumpSumId
    cost = cost!!,
    splittingAllowed = splittingAllowed,
    isFastTrack = fastTrack,
    phase = phase!!,
    categories = if (id == 0L) mutableSetOf() else categories.toEntity(id)
)

fun Collection<BudgetCategory>.toEntity(programmeLumpSumId: Long) = mapTo(HashSet()) {
    ProgrammeLumpSumBudgetCategoryEntity(programmeLumpSumId = programmeLumpSumId, category = it)
}

fun combineLumpSumTranslatedValues(
    programmeLumpSumId: Long,
    name: Set<InputTranslation>,
    description: Set<InputTranslation>
): MutableSet<ProgrammeLumpSumTranslEntity> {
    val nameMap = name.associateBy( { it.language }, { it.translation } )
    val descriptionMap = description.associateBy( { it.language }, { it.translation } )

    val languages = nameMap.keys.toMutableSet()
    languages.addAll(descriptionMap.keys)

    return languages.mapTo(HashSet()) {
        ProgrammeLumpSumTranslEntity(
            ProgrammeLumpSumTranslId(programmeLumpSumId, it),
            nameMap[it],
            descriptionMap[it]
        )
    }
}
