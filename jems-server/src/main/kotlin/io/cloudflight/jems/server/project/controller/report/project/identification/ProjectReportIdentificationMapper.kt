package io.cloudflight.jems.server.project.controller.report.project.identification

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportSpendingProfileDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportSpendingProfileLineDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.UpdateProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator.ProjectReportOutputIndicatorOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator.ProjectReportOutputLineOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator.ProjectReportResultIndicatorOverviewDTO
import io.cloudflight.jems.server.project.controller.report.project.resultPrinciple.toDto
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.project.identification.SpendingProfileLine
import io.cloudflight.jems.server.project.service.report.model.project.identification.SpendingProfileTotal
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorsAndResults
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectReportIdentificationMapper::class.java)

fun ProjectReportIdentification.toDto() = ProjectReportIdentificationDTO(
    targetGroups = this.targetGroups.map { it.toDto() },
    highlights = this.highlights,
    partnerProblems = this.partnerProblems,
    deviations = this.deviations,
    spendingProfilePerPartner = this.spendingProfilePerPartner?.toDto()
)
fun ProjectReportSpendingProfile.toDto() = ProjectReportSpendingProfileDTO(
    lines = this.lines.map { it.toDto() },
    total = this.total.toDto()
)

fun SpendingProfileLine.toDto() = ProjectReportSpendingProfileLineDTO(
    partnerRole = if (this.partnerRole != null) ProjectPartnerRoleDTO.valueOf(this.partnerRole.name) else null,
    partnerNumber = this.partnerNumber,
    partnerCountry =  this.partnerCountry,
    partnerAbbreviation = this.partnerAbbreviation,
    periodBudget = this.periodDetail?.periodBudget,
    periodBudgetCumulative = this.periodDetail?.periodBudgetCumulative,
    totalEligibleBudget = this.totalEligibleBudget,
    totalReportedSoFar = this.totalReportedSoFar,
    previouslyReported = this.previouslyReported,
    remainingBudget = this.remainingBudget,
    totalReportedSoFarPercentage = this.totalReportedSoFarPercentage,
    differenceFromPlanPercentage = this.differenceFromPlanPercentage,
    currentReport = this.currentReport,
    differenceFromPlan = this.differenceFromPlan,
    nextReportForecast = this.nextReportForecast
)

fun SpendingProfileTotal.toDto() = ProjectReportSpendingProfileLineDTO(
    partnerRole = null,
    partnerNumber = null,
    partnerCountry =  null,
    partnerAbbreviation = null,
    periodBudget = this.periodBudget,
    periodBudgetCumulative = this.periodBudgetCumulative,
    totalEligibleBudget = this.totalEligibleBudget,
    totalReportedSoFar = this.totalReportedSoFar,
    previouslyReported = this.previouslyReported,
    remainingBudget = this.remainingBudget,
    totalReportedSoFarPercentage = this.totalReportedSoFarPercentage,
    differenceFromPlanPercentage = this.differenceFromPlanPercentage,
    currentReport = this.currentReport,
    differenceFromPlan = this.differenceFromPlan,
    nextReportForecast = this.nextReportForecast
)


fun ProjectReportIdentificationTargetGroup.toDto() = ProjectReportIdentificationTargetGroupDTO(
    type = ProjectTargetGroupDTO.valueOf(this.type.name),
    sortNumber = this.sortNumber,
    description = this.description
)
fun UpdateProjectReportIdentificationDTO.toModel() = mapper.map(this)
fun ProjectReportOutputLineOverview.toDto() = mapper.map(this)

fun Map<ProjectReportResultIndicatorOverview, ProjectReportOutputIndicatorsAndResults>.toResultDto() =
    map { (resultIndicator, outputIndicatorsAndResults) ->
        ProjectReportResultIndicatorOverviewDTO(
            id = resultIndicator.id,
            identifier = resultIndicator.identifier,
            name = resultIndicator.name,
            measurementUnit = resultIndicator.measurementUnit,
            baselineIndicator = resultIndicator.baselineIndicator,
            baselines = resultIndicator.baselines,
            targetValue = resultIndicator.targetValue,
            previouslyReported = resultIndicator.previouslyReported,
            currentReport = resultIndicator.currentReport,
            outputIndicators = outputIndicatorsAndResults.outputIndicators.toOutputDto(),
            results = outputIndicatorsAndResults.results.map { it.toDto() },
        )
    }.sortedBy { it.id ?: Long.MAX_VALUE }

fun Map<ProjectReportOutputIndicatorOverview, List<ProjectReportOutputLineOverview>>.toOutputDto() = map { (outputIndicator, outputs) ->
    ProjectReportOutputIndicatorOverviewDTO(
        id = outputIndicator.id,
        identifier = outputIndicator.identifier,
        name = outputIndicator.name,
        measurementUnit = outputIndicator.measurementUnit,
        targetValue = outputIndicator.targetValue,
        previouslyReported = outputIndicator.previouslyReported,
        currentReport = outputIndicator.currentReport,
        outputs = outputs.map { it.toDto() },
    )
}.sortedBy { it.id ?: Long.MAX_VALUE }

@Mapper
interface ProjectReportIdentificationMapper {
    fun map(dto: UpdateProjectReportIdentificationDTO): ProjectReportIdentificationUpdate
    fun map(model: ProjectReportOutputLineOverview): ProjectReportOutputLineOverviewDTO
    fun map(model: ProjectReportSpendingProfile): ProjectReportSpendingProfileDTO
}
