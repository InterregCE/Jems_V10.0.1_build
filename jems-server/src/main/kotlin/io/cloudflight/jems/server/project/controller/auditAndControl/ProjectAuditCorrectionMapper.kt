package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionAvailablePartnerDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionExtendedDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionLineDTO
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionLine
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectAuditCorrectionMapper::class.java)

fun ProjectAuditControlCorrectionDTO.toModel() = mapper.map(this)
fun ProjectAuditControlCorrection.toDto() = mapper.map(this)
fun ProjectAuditControlCorrectionLine.toDto() = mapper.map(this)
fun ProjectAuditControlCorrectionLineDTO.toModel() = mapper.map(this)

fun ProjectAuditControlCorrectionExtended.toDto() = mapper.map(this)
fun ProjectAuditControlCorrectionExtendedDTO.toModel() = mapper.map(this)

fun CorrectionAvailablePartner.toDto() = mapper.map(this)

fun CorrectionStatus.toDto() = mapper.map(this)

@Mapper
interface ProjectAuditCorrectionMapper {
    fun map(model: ProjectAuditControlCorrection): ProjectAuditControlCorrectionDTO
    fun map(dto: ProjectAuditControlCorrectionDTO): ProjectAuditControlCorrection
    fun map(dto: ProjectAuditControlCorrectionLineDTO): ProjectAuditControlCorrectionLine
    fun map(model: ProjectAuditControlCorrectionLine): ProjectAuditControlCorrectionLineDTO
    fun map(model: ProjectAuditControlCorrectionExtended): ProjectAuditControlCorrectionExtendedDTO
    fun map(dto: ProjectAuditControlCorrectionExtendedDTO): ProjectAuditControlCorrectionExtended
    fun map(model: CorrectionAvailablePartner): CorrectionAvailablePartnerDTO
    fun map(model: CorrectionStatus): CorrectionStatusDTO

}
