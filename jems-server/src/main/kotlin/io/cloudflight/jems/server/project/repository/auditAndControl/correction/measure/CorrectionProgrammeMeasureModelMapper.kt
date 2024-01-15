package io.cloudflight.jems.server.project.repository.auditAndControl.correction.measure

import io.cloudflight.jems.server.payments.entity.PaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(CorrectionProgrammeMeasureModelMapper::class.java)

fun AuditControlCorrectionMeasureEntity.toModel(correctionExtension: PaymentToEcCorrectionExtensionEntity?) = mapper.map(this, correctionExtension)

@Mapper
interface CorrectionProgrammeMeasureModelMapper {

    @Mappings(
        Mapping(source = "correctionExtension.paymentApplicationToEc.accountingYear", target = "includedInAccountingYear"),
        Mapping(source = "entity.correctionId", target = "correctionId"),
        Mapping(source = "entity.comment", target = "comment")
    )
    fun map(entity: AuditControlCorrectionMeasureEntity, correctionExtension: PaymentToEcCorrectionExtensionEntity?): ProjectCorrectionProgrammeMeasure
}


