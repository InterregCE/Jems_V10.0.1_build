package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.entity.ProjectData
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectTransl
import io.cloudflight.jems.server.project.entity.TranslationId
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectForm
import io.cloudflight.jems.server.project.service.model.ProjectFull


fun ProjectEntity.toApplicantAndStatus() = ProjectApplicantAndStatus(
    applicantId = applicant.id,
    projectStatus = currentStatus.status,
)

fun InputProjectData.toEntity(projectId: Long) = ProjectData(
    duration = duration,
    translatedValues = combineTranslatedValuesProject(projectId, title, intro)
)

fun combineTranslatedValuesProject(
    projectId: Long,
    title: Set<InputTranslation>,
    intro: Set<InputTranslation>
): Set<ProjectTransl> {
    val titleMap = title.associateBy({ it.language }, { it.translation })
    val introMap = intro.associateBy({ it.language }, { it.translation })
    val languages = titleMap.keys.toMutableSet()
    languages.addAll(introMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectTransl(
            TranslationId(projectId, it),
            titleMap[it],
            introMap[it]
        )
    }
}

fun ProjectFull.getProjectWithoutFormData() = ProjectDetail(
    id = id,
    customIdentifier = customIdentifier,
    callSettings = callSettings,
    acronym = acronym,
    applicant = applicant,
    title = title ?: emptySet(),
    specificObjective = specificObjective,
    programmePriority = programmePriority,
    projectStatus = projectStatus,
    firstSubmission = firstSubmission,
    lastResubmission = lastResubmission,
    assessmentStep1 = assessmentStep1,
    assessmentStep2 = assessmentStep2,
)

fun ProjectFull.getOnlyFormRelatedData() = ProjectForm(
    id = id!!,
    callSettings = callSettings,
    acronym = acronym,
    title = title,
    intro = intro,
    duration = duration,
    specificObjective = specificObjective,
    programmePriority = programmePriority,
    periods = periods,
)
