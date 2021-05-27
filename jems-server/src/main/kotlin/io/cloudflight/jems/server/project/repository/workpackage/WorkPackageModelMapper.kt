package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.workpackage.output.toModel
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageTranslatedValue

fun WorkPackageEntity.toModel(
    getActivitiesForWorkPackageId: (Long) -> Collection<WorkPackageActivityEntity>?,
    getOutputsForWorkPackageId: (Long) -> Collection<WorkPackageOutputEntity>?
) = ProjectWorkPackage (
    id = id,
    workPackageNumber = number ?: throw NullPointerException("There is work package without any number in database."),
    translatedValues = translatedValues.toModel(),
    activities = getActivitiesForWorkPackageId.invoke(id)?.toModel() ?: emptyList(),
    outputs = getOutputsForWorkPackageId.invoke(id)?.toModel() ?: emptyList()
)

fun Set<WorkPackageTransl>.toModel() = mapTo(HashSet()) {
    ProjectWorkPackageTranslatedValue(
        language = it.translationId.language,
        name = it.name,
        specificObjective = it.specificObjective,
        objectiveAndAudience = it.objectiveAndAudience
    )
}

fun WorkPackageEntity.toModelFull(
    getActivitiesForWorkPackageId: (Long) -> Collection<WorkPackageActivityEntity>?,
    getOutputsForWorkPackageId: (Long) -> Collection<WorkPackageOutputEntity>?,
    getInvestmentsForWorkPackageId: (Long) -> Collection<WorkPackageInvestmentEntity>?
) = ProjectWorkPackageFull (
    id = id,
    workPackageNumber = number!!,
    translatedValues = translatedValues.toModel(),
    activities = getActivitiesForWorkPackageId.invoke(id)?.toModel() ?: emptyList(),
    outputs = getOutputsForWorkPackageId.invoke(id)?.toModel() ?: emptyList(),
    investments = getInvestmentsForWorkPackageId.invoke(id)?.toModel() ?: emptyList()
)
