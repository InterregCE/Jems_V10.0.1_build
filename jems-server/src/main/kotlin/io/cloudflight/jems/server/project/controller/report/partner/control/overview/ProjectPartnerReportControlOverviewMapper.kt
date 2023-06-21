package io.cloudflight.jems.server.project.controller.report.partner.control.overview

import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlDeductionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlWorkOverviewDTO
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportControlOverviewMapper::class.java)

fun ControlWorkOverview.toDto() = mapper.map(this)
fun ControlDeductionOverview.toDto() = mapper.map(this)

fun ControlOverview.toDto() = mapper.map(this)

fun ControlOverviewDTO.toModel(): ControlOverview =
    ControlOverview(
        startDate = startDate,
        requestsForClarifications = requestsForClarifications,
        receiptOfSatisfactoryAnswers = receiptOfSatisfactoryAnswers,
        endDate = endDate,
        findingDescription = findingDescription,
        followUpMeasuresFromLastReport = followUpMeasuresFromLastReport,
        conclusion = conclusion,
        followUpMeasuresForNextReport = followUpMeasuresForNextReport,
        previousFollowUpMeasuresFromLastReport = previousFollowUpMeasuresFromLastReport,
        lastCertifiedReportNumber = lastCertifiedReportNumber
    )

@Mapper
interface ProjectPartnerReportControlOverviewMapper {
    fun map(overview: ControlWorkOverview): ControlWorkOverviewDTO
    fun map(overview: ControlOverview): ControlOverviewDTO
    fun map(overview: ControlDeductionOverview): ControlDeductionOverviewDTO
}
