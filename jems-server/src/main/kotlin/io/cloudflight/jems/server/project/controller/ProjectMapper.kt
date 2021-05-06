package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.plugin.dto.MessageTypeDTO
import io.cloudflight.jems.api.plugin.dto.PreConditionCheckMessageDTO
import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.project.dto.ApplicationActionInfoDTO
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.call.controller.toDto
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

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

fun Collection<PartnerBudget>.toDTO() = map { it.toDTO() }
    .sortedBy { it.partner.sortNumber }

fun ProjectCallSettings.toDto() = projectMapper.map(this)
fun ProjectPartner.toOutputProjectPartner() = projectMapper.map(this)

private val projectMapper = Mappers.getMapper(ProjectMapper::class.java)

@Mapper
abstract class ProjectMapper {

    abstract fun map(applicationStatus: ApplicationStatus): ApplicationStatusDTO
    abstract fun map(applicationActionInfoDTO: ApplicationActionInfoDTO): ApplicationActionInfo

    @Mapping(source = "submissionAllowed", target = "submissionAllowed")
    abstract fun map(preConditionCheckResult: PreConditionCheckResult): PreConditionCheckResultDTO
    abstract fun map(preConditionCheckMessageList: List<PreConditionCheckMessage>): List<PreConditionCheckMessageDTO>
    abstract fun map(messageType: MessageType): MessageTypeDTO

    abstract fun map(projectPartner: ProjectPartner): OutputProjectPartner

    @Mapping(source = "totalCosts", target = "totalSum")
    abstract fun map(partnerBudget: PartnerBudget): ProjectPartnerBudgetDTO

    @Mapping(source = "additionalFundAllowed", target = "isAdditionalFundAllowed")
    abstract fun map(projectCallSettings: ProjectCallSettings): ProjectCallSettingsDTO
    abstract fun mapToLumpSumDTO(programmeLumpSum: List<ProgrammeLumpSum>): List<ProgrammeLumpSumDTO>

    @Mapping(source = "oneCostCategory", target = "isOneCostCategory")
    abstract fun mapToUnitCostDTO(programmeUnitCost: ProgrammeUnitCost): ProgrammeUnitCostDTO
    abstract fun mapToUnitCostDTO(programmeUnitCost: List<ProgrammeUnitCost>): List<ProgrammeUnitCostDTO>

    fun map(projectCallFlatRateSet: Set<ProjectCallFlatRate>): FlatRateSetupDTO =
        projectCallFlatRateSet.toDto()
}
