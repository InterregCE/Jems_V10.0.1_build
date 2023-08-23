package io.cloudflight.jems.server.project.controller.report.project.verification.expenditure

import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdateDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationRiskBasedDTO
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdate
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

val mapper = Mappers.getMapper(ProjectReportVerificationExpenditureMapper::class.java)

fun List<ProjectReportVerificationExpenditureLine>.toDto() = map { it.toDto() }
fun ProjectReportVerificationExpenditureLine.toDto() = mapper.map(this)
fun List<ProjectReportVerificationExpenditureLineUpdateDTO>.toLineUpdateModel() = map { it.toModel() }
fun ProjectReportVerificationExpenditureLineUpdateDTO.toModel() = mapper.map(this)
fun ProjectReportVerificationRiskBased.toDto() = mapper.map(this)
fun ProjectReportVerificationRiskBasedDTO.toModel() = mapper.map(this)

@Mapper
interface ProjectReportVerificationExpenditureMapper {
    fun map(model: ProjectReportVerificationExpenditureLine): ProjectReportVerificationExpenditureLineDTO
    fun map(dto: ProjectReportVerificationExpenditureLineUpdateDTO): ProjectReportVerificationExpenditureLineUpdate
    fun map(dto: ProjectReportVerificationRiskBasedDTO): ProjectReportVerificationRiskBased
    fun map(dto: ProjectReportVerificationRiskBased): ProjectReportVerificationRiskBasedDTO
}
