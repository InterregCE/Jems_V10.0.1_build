package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProject
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.ProjectDataDTO
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ProjectDecisionDTO
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.service.toOutputProgrammePrioritySimple
import io.cloudflight.jems.server.project.controller.toDto
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.entity.ProjectData
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.ProjectTransl
import io.cloudflight.jems.server.project.entity.TranslationId
import io.cloudflight.jems.server.project.repository.toSettingsModel
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.toOutputUser

fun InputProject.toEntity(
    call: CallEntity,
    applicant: UserEntity,
    statusHistoryEntity: ProjectStatusHistoryEntity
) = ProjectEntity(
    call = call,
    acronym = this.acronym!!,
    applicant = applicant,
    currentStatus = statusHistoryEntity,
)

fun ProjectEntity.toOutputProject(decisionStep1: ProjectDecisionDTO?, decisionStep2: ProjectDecisionDTO?, applicationFormConfiguration: ApplicationFormConfiguration) = ProjectDetailDTO(
    id = id,
    callSettings = call.toSettingsModel(applicationFormConfiguration).toDto(),
    acronym = acronym,
    applicant = applicant.toOutputUser(),
    projectStatus = currentStatus.toOutputProjectStatus(),
    firstSubmission = firstSubmission?.toOutputProjectStatus(),
    lastResubmission = lastResubmission?.toOutputProjectStatus(),
    step2Active = currentStatus.status.isInStep2(),
    firstStepDecision = decisionStep1,
    secondStepDecision = decisionStep2,
    projectData = projectData?.toOutputProjectData(priorityPolicy),
    periods = periods.map { it.toOutputPeriod() }
)

fun ProjectEntity.toOutputProjectSimple() = OutputProjectSimple(
    id = id,
    callName = call.name,
    acronym = acronym,
    projectStatus = ApplicationStatusDTO.valueOf(currentStatus.status.name),
    firstSubmissionDate = firstSubmission?.updated,
    lastResubmissionDate = lastResubmission?.updated,
    specificObjectiveCode = priorityPolicy?.code,
    programmePriorityCode = priorityPolicy?.programmePriority?.code
)

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

fun ProjectData.toOutputProjectData(priorityPolicy: ProgrammeSpecificObjectiveEntity?) = ProjectDataDTO(
    title = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.title)
    },
    intro = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.intro)
    },
    duration = duration,
    specificObjective = priorityPolicy?.toOutputProgrammePriorityPolicy(),
    programmePriority = priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple()
)

fun ProjectPeriodEntity.toOutputPeriod() = ProjectPeriodDTO(
    projectId = id.projectId,
    number = id.number,
    start = start,
    end = end
)
