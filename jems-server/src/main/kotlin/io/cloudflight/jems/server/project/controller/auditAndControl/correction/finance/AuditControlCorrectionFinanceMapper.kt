package io.cloudflight.jems.server.project.controller.auditAndControl.correction.finance

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionUpdateDTO
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescriptionUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(AuditControlCorrectionFinanceMapper::class.java)

fun ProjectCorrectionFinancialDescription.toDto() = mapper.map(this)
fun ProjectCorrectionFinancialDescriptionDTO.toModel() = mapper.map(this)
fun ProjectCorrectionFinancialDescriptionUpdateDTO.toModel() = mapper.map(this)

@Mapper
interface AuditControlCorrectionFinanceMapper {
    fun map(model: ProjectCorrectionFinancialDescription): ProjectCorrectionFinancialDescriptionDTO
    fun map(dto: ProjectCorrectionFinancialDescriptionDTO): ProjectCorrectionFinancialDescription
    fun map(dto: ProjectCorrectionFinancialDescriptionUpdateDTO): ProjectCorrectionFinancialDescriptionUpdate
}
