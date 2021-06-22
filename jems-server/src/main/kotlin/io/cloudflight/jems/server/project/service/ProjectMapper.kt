package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.entity.ProjectData
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectTransl
import io.cloudflight.jems.server.project.entity.TranslationId


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
