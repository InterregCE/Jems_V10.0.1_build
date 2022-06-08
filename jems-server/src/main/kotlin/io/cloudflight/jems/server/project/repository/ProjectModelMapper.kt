package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationEntity
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.ProjectCallStateAidEntity
import io.cloudflight.jems.server.call.repository.toModel
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.repository.costoption.toModel
import io.cloudflight.jems.server.programme.repository.costoption.toProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.service.toOutputProgrammePrioritySimple
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodRow
import io.cloudflight.jems.server.project.entity.ProjectRow
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import io.cloudflight.jems.server.project.entity.ProjectVersionId
import io.cloudflight.jems.server.project.entity.ProjectVersionRow
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentEligibilityEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentQualityEntity
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun Set<ProgrammeUnitCostEntity>.toModel() = map { it.toProgrammeUnitCost() }

fun Collection<ProjectPeriodEntity>.toProjectPeriods() = map { it.toProjectPeriod() }

fun ProjectPeriodEntity.toProjectPeriod() = ProjectPeriod(number = id.number, start = start, end = end)

fun ProjectVersionEntity.toProjectVersionSummary() =
    ProjectVersionSummary(version = id.version, projectId = id.projectId, createdAt = createdAt, user = user)

fun List<ProjectVersionRow>.toProjectVersion() =
    this.groupBy { ProjectVersionId(it.version, it.projectId) }.map { groupedRows ->
        ProjectVersion(
            groupedRows.value.first().version,
            groupedRows.value.first().projectId,
            ZonedDateTime.of(groupedRows.value.first().createdAt.toLocalDateTime(), ZoneOffset.UTC),
            UserEntity(
                groupedRows.value.first().userId,
                groupedRows.value.first().email,
                groupedRows.value.first().name,
                groupedRows.value.first().surname,
                UserRoleEntity(groupedRows.value.first().roleId, groupedRows.value.first().roleName),
                "",
                groupedRows.value.first().userStatus
            ),
            groupedRows.value.first().status,
            current = groupedRows.value.first().rowEnd == null
        )
    }

fun CallEntity.toSettingsModel(
    stateAidEntities: MutableSet<ProjectCallStateAidEntity>,
    applicationFormFieldConfigurationEntities: MutableSet<ApplicationFormFieldConfigurationEntity>
) = ProjectCallSettings(
    callId = id,
    callName = name,
    callType = type,
    startDate = startDate,
    endDate = endDate,
    endDateStep1 = endDateStep1,
    lengthOfPeriod = lengthOfPeriod,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    flatRates = flatRates.toModel(),
    lumpSums = lumpSums.map { it.toModel() }.sortedBy { it.id },
    unitCosts = unitCosts.toProgrammeUnitCost(),
    stateAids = stateAidEntities.toModel(),
    applicationFormFieldConfigurations = applicationFormFieldConfigurationEntities.toModel(),
    preSubmissionCheckPluginKey = preSubmissionCheckPluginKey,
    firstStepPreSubmissionCheckPluginKey = firstStepPreSubmissionCheckPluginKey
)

fun ProjectEntity.toModel(
    assessmentStep1: ProjectAssessmentEntity?,
    assessmentStep2: ProjectAssessmentEntity?,
    stateAidEntities: MutableSet<ProjectCallStateAidEntity>,
    applicationFormFieldConfigurationEntities: MutableSet<ApplicationFormFieldConfigurationEntity>
) = ProjectFull(
    id = id,
    customIdentifier = customIdentifier,
    callSettings = call.toSettingsModel(stateAidEntities, applicationFormFieldConfigurationEntities),
    acronym = acronym,
    applicant = applicant.toUserSummary(),
    projectStatus = currentStatus.toProjectStatus(),
    firstSubmission = firstSubmission?.toProjectStatus(),
    firstSubmissionStep1 = firstSubmissionStep1?.toProjectStatus(),
    lastResubmission = lastResubmission?.toProjectStatus(),
    contractedDecision = contractedDecision?.toProjectStatus(),
    assessmentStep1 = assessmentStep1?.toModel(),
    assessmentStep2 = assessmentStep2?.toModel(),
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

fun ProjectEntity.toDetailModel(
    assessmentStep1: ProjectAssessmentEntity?,
    assessmentStep2: ProjectAssessmentEntity?,
    stateAidEntities: MutableSet<ProjectCallStateAidEntity>,
    applicationFormFieldConfigurationEntities: MutableSet<ApplicationFormFieldConfigurationEntity>
) = ProjectDetail(
    id = id,
    customIdentifier = customIdentifier,
    callSettings = call.toSettingsModel(stateAidEntities, applicationFormFieldConfigurationEntities),
    acronym = acronym,
    title = projectData?.translatedValues?.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.title)
    } ?: emptySet(),
    applicant = applicant.toUserSummary(),
    projectStatus = currentStatus.toProjectStatus(),
    firstSubmission = firstSubmission?.toProjectStatus(),
    lastResubmission = lastResubmission?.toProjectStatus(),
    assessmentStep1 = assessmentStep1?.toModel(),
    assessmentStep2 = assessmentStep2?.toModel(),
    specificObjective = priorityPolicy?.toOutputProgrammePriorityPolicy(),
    programmePriority = priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple(),
)

fun ProjectEntity.toSummaryModel() = ProjectSummary(
    id = id,
    customIdentifier = customIdentifier,
    callName = call.name,
    acronym = acronym,
    status = currentStatus.status,
    firstSubmissionDate = firstSubmission?.updated,
    lastResubmissionDate = lastResubmission?.updated,
    specificObjectiveCode = priorityPolicy?.code,
    programmePriorityCode = priorityPolicy?.programmePriority?.code,
)

fun Page<ProjectEntity>.toModel() = map { it.toSummaryModel() }

fun List<ProjectRow>.toProjectEntryWithDetailData(
    project: ProjectEntity,
    periods: List<ProjectPeriod>,
    assessmentStep1: ProjectAssessmentEntity,
    assessmentStep2: ProjectAssessmentEntity,
    stateAidEntities: MutableSet<ProjectCallStateAidEntity>,
    applicationFormFieldConfigurationEntities: MutableSet<ApplicationFormFieldConfigurationEntity>,
    priority: ProgrammePriorityEntity?
) =
    this.groupBy { it.id }.map { groupedRows ->
        ProjectFull(
            id = groupedRows.key,
            customIdentifier = groupedRows.value.first().customIdentifier,
            acronym = groupedRows.value.first().acronym,
            title = groupedRows.value.extractField { it.title },
            intro = groupedRows.value.extractField { it.intro },
            duration = groupedRows.value.first().duration,
            periods = periods,
            specificObjective = mapOutputProgrammePriorityPolicy(
                groupedRows.value.first().programmePriorityPolicyObjectivePolicy,
                groupedRows.value.first().programmePriorityPolicyCode),
            // map non historic data
            callSettings = project.call.toSettingsModel(stateAidEntities, applicationFormFieldConfigurationEntities),
            applicant = project.applicant.toUserSummary(),
            programmePriority = priority?.toOutputProgrammePrioritySimple(),
            projectStatus = ProjectStatus(
                groupedRows.value.first().statusId,
                groupedRows.value.first().status,
                UserSummary(
                    groupedRows.value.first().userId,
                    groupedRows.value.first().email,
                    groupedRows.value.first().name,
                    groupedRows.value.first().surname,
                    UserRoleSummary(
                        groupedRows.value.first().roleId,
                        groupedRows.value.first().roleName
                    ),
                    groupedRows.value.first().userStatus
                ),
                ZonedDateTime.of(groupedRows.value.first().updated.toLocalDateTime(), ZoneId.systemDefault()),
                groupedRows.value.first().decisionDate,
                groupedRows.value.first().entryIntoForceDate,
                groupedRows.value.first().note
            ),
            firstSubmission = project.firstSubmission?.toProjectStatus(),
            firstSubmissionStep1 = project.firstSubmissionStep1?.toProjectStatus(),
            lastResubmission = project.lastResubmission?.toProjectStatus(),
            contractedDecision = project.contractedDecision?.toProjectStatus(),
            assessmentStep1 = assessmentStep1.toModel(),
            assessmentStep2 = assessmentStep2.toModel(),
        )
    }.first()

fun mapOutputProgrammePriorityPolicy(policy: String?, code: String?): OutputProgrammePriorityPolicySimpleDTO? {
    if (policy != null && code != null) {
        return OutputProgrammePriorityPolicySimpleDTO(
            programmeObjectivePolicy = ProgrammeObjectivePolicy.valueOf(policy),
            code = code
        )
    }
    return null
}

fun List<ProjectPeriodRow>.toProjectPeriodHistoricalData() = map {
    ProjectPeriod(it.periodNumber!!, it.periodStart!!, it.periodEnd!!)
}.toList()

fun ProjectAssessmentEntity.toModel(): ProjectAssessment? {
    if (getOrNull() == null)
        return null

    return ProjectAssessment(
        assessmentQuality = assessmentQuality?.toModel(),
        assessmentEligibility = assessmentEligibility?.toModel(),
        eligibilityDecision = eligibilityDecision?.toProjectStatus(),
        preFundingDecision = preFundingDecision?.toProjectStatus(),
        fundingDecision = fundingDecision?.toProjectStatus(),
    )
}

private fun ProjectAssessmentQualityEntity.toModel() = ProjectAssessmentQuality(
    projectId = id.project.id,
    step = id.step,
    result = result,
    updated = updated,
    note = note,
)

private fun ProjectAssessmentEligibilityEntity.toModel() = ProjectAssessmentEligibility(
    projectId = id.project.id,
    step = id.step,
    result = result,
    updated = updated,
    note = note,
)

fun ProjectStatusHistoryEntity.toProjectStatus() = ProjectStatus(
    id = id,
    status = status,
    user = user.toUserSummary(),
    updated = updated,
    decisionDate = decisionDate,
    entryIntoForceDate = entryIntoForceDate,
    note = note
)
