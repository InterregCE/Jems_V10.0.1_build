package io.cloudflight.jems.server.project.controller.auditAndControl.correction

import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionCostItemDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionLineDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationUpdateDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page
import java.math.BigDecimal

private val mapper = Mappers.getMapper(AuditControlCorrectionMapper::class.java)

fun AuditControlCorrectionDetail.toDto() = mapper.map(this)
fun ProjectCorrectionIdentificationUpdateDTO.toModel() = mapper.map(this)
fun List<AuditControlCorrection>.toSimpleDto() = map { mapper.mapSimple(it) }

fun Page<AuditControlCorrectionLine>.toDto() = map {
    ProjectAuditControlCorrectionLineDTO(
        id = it.id,
        auditControlId = it.auditControlId,
        orderNr = it.orderNr,
        status = AuditStatusDTO.valueOf(it.status.name),
        type = AuditControlCorrectionTypeDTO.valueOf(it.type.name),
        auditControlNumber = it.auditControlNr,
        canBeDeleted = !it.status.isClosed(),

        partnerRoleDTO = ProjectPartnerRoleDTO.PARTNER,
        partnerNumber = 0,
        partnerDisabled = false,
        partnerReport = "",
        initialAuditNUmber = 0,
        initialCorrectionNumber = 0,
        fundName = "",
        fundAmount = BigDecimal.ZERO,
        publicContribution = BigDecimal.ZERO,
        autoPublicContribution = BigDecimal.ZERO,
        privateContribution = BigDecimal.ZERO,
        total = BigDecimal.ZERO,
        impactProjectLevel = "",
        scenario = 0,
    )
}

fun Page<CorrectionCostItem>.toCorrectionCostItemDTO() = map { mapper.map(it) }

@Mapper
interface AuditControlCorrectionMapper {
    @Mapping(source = "auditControlNr", target = "auditControlNumber")
    fun map(model: AuditControlCorrectionDetail): ProjectAuditControlCorrectionDTO
    fun map(model: ProjectCorrectionIdentificationUpdateDTO): AuditControlCorrectionUpdate
    @Mapping(source = "auditControlNr", target = "auditControlNumber")
    fun mapSimple(model: AuditControlCorrection): AuditControlCorrectionDTO
    fun map(dto: CorrectionCostItem): CorrectionCostItemDTO
}
