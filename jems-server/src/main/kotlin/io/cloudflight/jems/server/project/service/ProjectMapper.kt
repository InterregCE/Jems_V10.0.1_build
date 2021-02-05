package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProject
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.OutputProject
import io.cloudflight.jems.api.project.dto.OutputProjectData
import io.cloudflight.jems.api.project.dto.OutputProjectPeriod
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.service.toOutputProgrammePrioritySimple
import io.cloudflight.jems.server.project.controller.toDto
import io.cloudflight.jems.server.project.dto.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.entity.ProjectData
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.entity.ProjectTransl
import io.cloudflight.jems.server.project.entity.TranslationId
import io.cloudflight.jems.server.project.repository.toSettingsModel
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.service.toOutputUser

fun InputProject.toEntity(
    call: CallEntity,
    applicant: User,
    status: ProjectStatus
) = ProjectEntity(
    call = call,
    acronym = this.acronym!!,
    applicant = applicant,
    projectStatus = status
)

fun ProjectEntity.toOutputProject() = OutputProject(
    id = id,
    callSettings = call.toSettingsModel().toDto(),
    acronym = acronym,
    applicant = applicant.toOutputUser(),
    projectStatus = projectStatus.toOutputProjectStatus(),
    firstSubmission = firstSubmission?.toOutputProjectStatus(),
    lastResubmission = lastResubmission?.toOutputProjectStatus(),
    qualityAssessment = qualityAssessment?.toOutputProjectQualityAssessment(),
    eligibilityAssessment = eligibilityAssessment?.toOutputProjectEligibilityAssessment(),
    eligibilityDecision = eligibilityDecision?.toOutputProjectStatus(),
    fundingDecision = fundingDecision?.toOutputProjectStatus(),
    projectData = projectData?.toOutputProjectData(priorityPolicy),
    periods = periods.map { it.toOutputPeriod() }
)

fun ProjectEntity.toOutputProjectSimple() = OutputProjectSimple(
    id = id,
    callName = call.name,
    acronym = acronym,
    projectStatus = projectStatus.status,
    firstSubmissionDate = firstSubmission?.updated,
    lastResubmissionDate = lastResubmission?.updated,
    specificObjectiveCode = priorityPolicy?.code,
    programmePriorityCode = priorityPolicy?.programmePriority?.code
)

fun ProjectEntity.toApplicantAndStatus() = ProjectApplicantAndStatus(
    applicantId = applicant.id,
    projectStatus = projectStatus.status
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

fun ProjectData.toOutputProjectData(priorityPolicy: ProgrammeSpecificObjectiveEntity?) = OutputProjectData(
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

fun ProjectPeriodEntity.toOutputPeriod() = OutputProjectPeriod(
    projectId = id.projectId,
    number = id.number,
    start = start,
    end = end
)
