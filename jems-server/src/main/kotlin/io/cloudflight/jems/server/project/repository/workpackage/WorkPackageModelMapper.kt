package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.workpackage.activity.toModel
import io.cloudflight.jems.server.project.repository.workpackage.output.toModel
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageTranslatedValue

fun WorkPackageEntity.toModel(
    getActivitiesForWorkPackageId: (Long) -> Collection<WorkPackageActivityEntity>?,
    getOutputsForWorkPackageId: (Long) -> Collection<WorkPackageOutputEntity>?,
) = ProjectWorkPackage (
    id = id,
    workPackageNumber = number ?: throw NullPointerException("There is work package without any number in database."),
    name = translatedValues.extractField { it.name },
    specificObjective = translatedValues.extractField { it.specificObjective },
    objectiveAndAudience = translatedValues.extractField { it.objectiveAndAudience },
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
    getInvestmentsForWorkPackageId: (Long) -> Collection<WorkPackageInvestmentEntity>?,
    periods: Collection<ProjectPeriodEntity>,
) = ProjectWorkPackageFull (
    id = id,
    workPackageNumber = number!!,
    name = translatedValues.extractField { it.name },
    specificObjective = translatedValues.extractField { it.specificObjective },
    objectiveAndAudience = translatedValues.extractField { it.objectiveAndAudience },
    activities = getActivitiesForWorkPackageId.invoke(id)?.toModel() ?: emptyList(),
    outputs = getOutputsForWorkPackageId.invoke(id)?.toModel(periods) ?: emptyList(),
    investments = getInvestmentsForWorkPackageId.invoke(id)?.toModel() ?: emptyList()
)
