package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.TranslationWorkPackageId
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl

fun WorkPackageEntity.toOutputWorkPackageSimple() = OutputWorkPackageSimple (
    id = id,
    number = number,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) }
)

fun WorkPackageEntity.toOutputWorkPackage() = OutputWorkPackage (
    id = id,
    number = number,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    specificObjective = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.specificObjective) },
    objectiveAndAudience = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.objectiveAndAudience) }
)

fun InputWorkPackageCreate.toEntity(project: ProjectEntity) = WorkPackageEntity (
    project = project
)

fun InputWorkPackageCreate.combineTranslatedValues(
    workPackageId: Long
): MutableSet<WorkPackageTransl> {
    val nameMap = name.associateBy( { it.language }, { it.translation } )
    val specificObjectiveMap = specificObjective.associateBy( { it.language }, { it.translation } )
    val objectiveAndAudienceMap = objectiveAndAudience.associateBy( { it.language }, { it.translation } )
    val languages = nameMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        WorkPackageTransl(
            TranslationWorkPackageId(workPackageId, it),
            nameMap[it],
            specificObjectiveMap[it],
            objectiveAndAudienceMap[it]
        )
    }
}

fun InputWorkPackageUpdate.combineTranslatedValues(
    workPackageId: Long
): MutableSet<WorkPackageTransl> {
    val nameMap = name.associateBy( { it.language }, { it.translation } )
    val specificObjectiveMap = specificObjective.associateBy( { it.language }, { it.translation } )
    val objectiveAndAudienceMap = objectiveAndAudience.associateBy( { it.language }, { it.translation } )
    val languages = nameMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        WorkPackageTransl(
            TranslationWorkPackageId(workPackageId, it),
            nameMap[it],
            specificObjectiveMap[it],
            objectiveAndAudienceMap[it]
        )
    }
}
