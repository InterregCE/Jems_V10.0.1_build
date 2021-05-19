package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.repository.toModel
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.repository.costoption.toModel
import io.cloudflight.jems.server.programme.repository.costoption.toProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.service.toOutputProgrammePrioritySimple
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectRow
import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.toProjectDecision
import io.cloudflight.jems.server.project.service.toProjectStatus
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import org.springframework.data.domain.Page

fun Set<ProgrammeUnitCostEntity>.toModel() = map { it.toProgrammeUnitCost() }

fun Collection<ProjectPeriodEntity>.toProjectPeriods() = map { it.toProjectPeriod() }

fun ProjectPeriodEntity.toProjectPeriod() = ProjectPeriod(number = id.number, start = start, end = end)

fun ProjectVersionEntity.toProjectVersion() =
    ProjectVersion(version = id.version, projectId = id.projectId, createdAt = createdAt, user = user, status = status)

fun List<ProjectVersionEntity>.toProjectVersions() = map { it.toProjectVersion() }

fun CallEntity.toSettingsModel() = ProjectCallSettings(
    callId = id,
    callName = name,
    startDate = startDate,
    endDate = endDate,
    endDateStep1 = endDateStep1,
    lengthOfPeriod = lengthOfPeriod,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    flatRates = flatRates.toModel(),
    lumpSums = lumpSums.map { it.toModel() }.sortedBy { it.id },
    unitCosts = unitCosts.toProgrammeUnitCost()
)

fun ProjectEntity.toModel() = Project(
    id = id,
    callSettings = call.toSettingsModel(),
    acronym = acronym,
    applicant = applicant.toUserSummary(),
    projectStatus = currentStatus.toProjectStatus(),
    firstSubmission = firstSubmission?.toProjectStatus(),
    lastResubmission = lastResubmission?.toProjectStatus(),
    step2Active = step2Active,
    firstStepDecision = firstStepDecision?.toProjectDecision(),
    secondStepDecision = secondStepDecision?.toProjectDecision(),
    title = projectData?.translatedValues?.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.title)
    },
    intro = projectData?.translatedValues?.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.intro)
    },
    duration = projectData?.duration,
    specificObjective = priorityPolicy?.toOutputProgrammePriorityPolicy(),
    programmePriority = priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple(),
    periods = periods.toProjectPeriods()
)

fun ProjectEntity.toSummaryModel() = ProjectSummary(
    id = id,
    callName = call.name,
    acronym = acronym,
    status = currentStatus.status,
    firstSubmissionDate = firstSubmission?.updated,
    lastResubmissionDate = lastResubmission?.updated,
    specificObjectiveCode = priorityPolicy?.code,
    programmePriorityCode = priorityPolicy?.programmePriority?.code,
)

fun Page<ProjectEntity>.toModel() = map { it.toSummaryModel() }

fun List<ProjectRow>.toProjectEntryApplyNonHistoricalData(project: ProjectEntity) =
    this.groupBy { it.id }.map { groupedRows ->
        Project(
            id = groupedRows.key,
            acronym = groupedRows.value.first().acronym,
            title = groupedRows.value.extractField { it.title },
            intro = groupedRows.value.extractField { it.intro },
            duration = groupedRows.value.first().duration,
            step2Active = groupedRows.value.first().step2Active,
            periods = groupedRows.value.filter { it.periodNumber != null }
                .map {
                    ProjectPeriod(it.periodNumber!!, it.periodStart!!, it.periodEnd!!)
                },
            // map non historic data
            callSettings = project.call.toSettingsModel(),
            applicant = project.applicant.toUserSummary(),
            specificObjective = project.priorityPolicy?.toOutputProgrammePriorityPolicy(),
            programmePriority = project.priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple(),
            projectStatus = project.currentStatus.toProjectStatus(),
            firstSubmission = project.firstSubmission?.toProjectStatus(),
            lastResubmission = project.lastResubmission?.toProjectStatus(),
            firstStepDecision = project.firstStepDecision?.toProjectDecision(),
            secondStepDecision = project.secondStepDecision?.toProjectDecision()
        )
    }.first()
