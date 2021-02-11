package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageTranslatedValue

fun WorkPackageEntity.toModel(
    getActivitiesForWorkPackageId: (Long) -> Collection<WorkPackageActivityEntity>?,
    getOutputsForWorkPackageId: (Long) -> Collection<WorkPackageOutputEntity>?,
) = ProjectWorkPackage (
    id = id,
    translatedValues = translatedValues.toModel(),
    activities = getActivitiesForWorkPackageId.invoke(id)?.toModel() ?: emptyList(),
    outputs = getOutputsForWorkPackageId.invoke(id)?.toModel() ?: emptyList()
)

fun Set<WorkPackageTransl>.toModel() = mapTo(HashSet()) {
    ProjectWorkPackageTranslatedValue(
        language = it.translationId.language,
        name = it.name,
    )
}
