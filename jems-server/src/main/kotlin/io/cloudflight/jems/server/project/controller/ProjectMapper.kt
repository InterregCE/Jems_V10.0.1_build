package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.plugin.dto.MessageTypeDTO
import io.cloudflight.jems.api.plugin.dto.PreConditionCheckMessageDTO
import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.project.dto.ApplicationActionInfoDTO
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.ProjectDataDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailFormDTO
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.ProjectVersionDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.plugin.contract.models.common.I18nMessageData
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.call.controller.CallDTOMapper
import io.cloudflight.jems.server.call.controller.toDto
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectForm
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.user.controller.toDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

fun ApplicationStatus.toDTO() = projectMapper.map(this)
fun ApplicationActionInfoDTO.toModel() = projectMapper.map(this)
fun PreConditionCheckResult.toDTO() = projectMapper.map(this).also { result ->
    result.messages.forEach {
        setIssuesCount(it)
    }
}

fun setIssuesCount(message: PreConditionCheckMessageDTO) {
    if (message.subSectionMessages.isEmpty()) {
        if (message.messageType == MessageTypeDTO.ERROR) message.issueCount = 1
    } else {
        message.subSectionMessages.forEach { setIssuesCount(it) }
        message.issueCount = message.subSectionMessages.map { it.issueCount }.sum()
    }
}

fun PartnerBudget.toDTO() = projectMapper.map(this)

fun ProjectVersion.toDTO() = projectMapper.map(this)
fun Collection<ProjectVersion>.toDTOs() = map { it.toDTO() }

fun Collection<PartnerBudget>.toDTO() = map { it.toDTO() }
    .sortedBy { it.partner.sortNumber }

fun ProjectCallSettings.toDto() = projectMapper.map(this)
fun ProjectPartner.toOutputProjectPartner() = projectMapper.map(this)

fun ProjectDetail.toDto() = ProjectDetailDTO(
    id = id,
    callSettings = callSettings.toDto(),
    acronym = acronym,
    applicant = applicant.toDto(),
    title = title,
    specificObjective = specificObjective,
    programmePriority = programmePriority,
    projectStatus = projectStatus.toDto(),
    firstSubmission = firstSubmission?.toDto(),
    lastResubmission = lastResubmission?.toDto(),
    step2Active = projectStatus.status.isInStep2(),
    firstStepDecision = assessmentStep1?.toDto(),
    secondStepDecision = assessmentStep2?.toDto(),
)

fun ProjectForm.toDto() = ProjectDetailFormDTO(
    id = id,
    callSettings = callSettings.toDto(),
    acronym = acronym,
    title = title ?: emptySet(),
    intro = intro ?: emptySet(),
    duration = duration,
    specificObjective = specificObjective,
    programmePriority = programmePriority,
    periods = periods.toDtos(id),
)

fun Collection<ProjectPeriod>.toDtos(projectId: Long?) = map { it.toDto(projectId) }

fun ProjectPeriod.toDto(projectId: Long?) = ProjectPeriodDTO(
    projectId = projectId ?: 0,
    number = number,
    start = start,
    end = end
)

fun Page<ProjectSummary>.toDto() = map {
    OutputProjectSimple(
        id = it.id,
        callName = it.callName,
        acronym = it.acronym,
        projectStatus = ApplicationStatusDTO.valueOf(it.status.name),
        firstSubmissionDate = it.firstSubmissionDate,
        lastResubmissionDate = it.lastResubmissionDate,
        specificObjectiveCode = it.specificObjectiveCode,
        programmePriorityCode = it.programmePriorityCode,
    )
}

private val projectMapper = Mappers.getMapper(ProjectMapper::class.java)

@Mapper(uses = [CallDTOMapper::class])
abstract class ProjectMapper {

    abstract fun map(applicationStatus: ApplicationStatus): ApplicationStatusDTO
    abstract fun map(applicationActionInfoDTO: ApplicationActionInfoDTO): ApplicationActionInfo
    abstract fun map(projectVersion: ProjectVersion): ProjectVersionDTO

    @Mapping(source = "submissionAllowed", target = "submissionAllowed")
    abstract fun map(preConditionCheckResult: PreConditionCheckResult): PreConditionCheckResultDTO
    abstract fun map(preConditionCheckMessageList: List<PreConditionCheckMessage>): List<PreConditionCheckMessageDTO>
    abstract fun map(messageType: MessageType): MessageTypeDTO

    abstract fun map(projectPartner: ProjectPartner): OutputProjectPartner

    @Mapping(source = "totalCosts", target = "totalSum")
    abstract fun map(partnerBudget: PartnerBudget): ProjectPartnerBudgetDTO

    @Mapping(source = "additionalFundAllowed", target = "additionalFundAllowed")
    abstract fun map(projectCallSettings: ProjectCallSettings): ProjectCallSettingsDTO
    abstract fun mapToLumpSumDTO(programmeLumpSum: List<ProgrammeLumpSum>): List<ProgrammeLumpSumDTO>

    @Mapping(source = "oneCostCategory", target = "oneCostCategory")
    abstract fun mapToUnitCostDTO(programmeUnitCost: ProgrammeUnitCost): ProgrammeUnitCostDTO
    abstract fun mapToUnitCostDTO(programmeUnitCost: List<ProgrammeUnitCost>): List<ProgrammeUnitCostDTO>

    fun map(projectCallFlatRateSet: Set<ProjectCallFlatRate>): FlatRateSetupDTO =
        projectCallFlatRateSet.toDto()

    fun map(i18nMessageData: I18nMessageData): I18nMessage =
        I18nMessage(i18nMessageData.i18nKey, i18nMessageData.i18nArguments)
}
