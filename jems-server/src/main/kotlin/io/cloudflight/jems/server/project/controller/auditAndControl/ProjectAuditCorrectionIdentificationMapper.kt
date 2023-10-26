package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionAvailablePartnerReportDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationUpdateDTO
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentificationUpdate
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectAuditCorrectionIdentificationMapper::class.java)

fun CorrectionAvailablePartnerReportDTO.toModel() = mapper.map(this)
fun CorrectionAvailablePartnerReport.toDto() = mapper.map(this)
fun List<CorrectionAvailablePartnerReport>.toDto() = map{ it.toDto() }

fun ProjectCorrectionIdentification.toDto() = mapper.map(this)

fun ProjectCorrectionIdentificationUpdateDTO.toModel() = mapper.map(this)

@Mapper
interface ProjectAuditCorrectionIdentificationMapper {
    fun map(model: CorrectionAvailablePartnerReport): CorrectionAvailablePartnerReportDTO
    fun map(dto: CorrectionAvailablePartnerReportDTO): CorrectionAvailablePartnerReport

    @Mapping(source = "correction.id", target="correctionId")
    fun map(model: ProjectCorrectionIdentification): ProjectCorrectionIdentificationDTO
    fun map(dto: ProjectCorrectionIdentificationUpdateDTO): ProjectCorrectionIdentificationUpdate

}
