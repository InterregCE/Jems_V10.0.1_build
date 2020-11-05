package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProject
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.OutputProject
import io.cloudflight.jems.api.project.dto.OutputProjectData
import io.cloudflight.jems.api.project.dto.OutputProjectPeriod
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.call.service.toOutputCallWithDates
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.service.toOutputProgrammePrioritySimple
import io.cloudflight.jems.server.project.dto.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.ProjectData
import io.cloudflight.jems.server.project.entity.ProjectPeriod
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.service.toOutputUser

fun InputProject.toEntity(
    call: Call,
    applicant: User,
    status: ProjectStatus
) = Project(
    id = null,
    call = call,
    acronym = this.acronym!!,
    applicant = applicant,
    projectStatus = status
)

fun Project.toOutputProject() = OutputProject(
    id = id,
    call = call.toOutputCallWithDates(),
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

fun Project.toOutputProjectSimple() = OutputProjectSimple(
    id = id,
    callName = call.name,
    acronym = acronym,
    projectStatus = projectStatus.status,
    firstSubmissionDate = firstSubmission?.updated,
    lastResubmissionDate = lastResubmission?.updated,
    specificObjectiveCode = priorityPolicy?.code,
    programmePriorityCode = priorityPolicy?.programmePriority?.code
)

fun Project.toApplicantAndStatus() = ProjectApplicantAndStatus(
    applicantId = applicant.id!!,
    projectStatus = projectStatus.status
)

fun InputProjectData.toEntity() = ProjectData(
    title = title,
    duration = duration,
    intro = intro,
    introProgrammeLanguage = introProgrammeLanguage
)

fun ProjectData.toOutputProjectData(priorityPolicy: ProgrammePriorityPolicy?) = OutputProjectData(
    title = title,
    duration = duration,
    intro = intro,
    introProgrammeLanguage = introProgrammeLanguage,
    specificObjective = priorityPolicy?.toOutputProgrammePriorityPolicy(),
    programmePriority = priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple()
)

fun ProjectPeriod.toOutputPeriod() = OutputProjectPeriod(
    projectId = id.projectId,
    number = id.number,
    start = start,
    end = end
)
