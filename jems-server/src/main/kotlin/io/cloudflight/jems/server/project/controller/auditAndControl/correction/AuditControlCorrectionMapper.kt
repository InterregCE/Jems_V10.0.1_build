package io.cloudflight.jems.server.project.controller.auditAndControl.correction

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionCostItemDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionLineDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationUpdateDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.AvailableCorrectionsForPaymentDTO
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.AvailableCorrectionsForPayment
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

fun List<AvailableCorrectionsForPayment>.toDto() = map {
    AvailableCorrectionsForPaymentDTO(
        partnerId = it.partnerId,
        corrections = it.corrections.map { it.toSimpleDto() }
    )
}

private val mapper = Mappers.getMapper(AuditControlCorrectionMapper::class.java)

fun AuditControlCorrectionDetail.toDto() = mapper.map(this)
fun ProjectCorrectionIdentificationUpdateDTO.toModel() = mapper.map(this)
fun List<AuditControlCorrection>.toSimpleDto() = map { it.toSimpleDto() }
fun AuditControlCorrection.toSimpleDto() = mapper.mapSimple(this)

fun Page<AuditControlCorrectionLine>.toDto() = map { mapper.map(it) }
fun Page<CorrectionCostItem>.toCorrectionCostItemDTO() = map { mapper.map(it) }

@Mapper
interface AuditControlCorrectionMapper {
    @Mapping(source = "auditControlNr", target = "auditControlNumber")
    fun map(model: AuditControlCorrectionDetail): ProjectAuditControlCorrectionDTO
    fun map(model: ProjectCorrectionIdentificationUpdateDTO): AuditControlCorrectionUpdate
    fun map(model: AuditControlCorrectionLine): ProjectAuditControlCorrectionLineDTO

    @Mapping(source = "auditControlNr", target = "auditControlNumber")
    fun mapSimple(model: AuditControlCorrection): AuditControlCorrectionDTO
    fun map(dto: CorrectionCostItem): CorrectionCostItemDTO
}
