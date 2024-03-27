package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionExtensionDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionLinkingUpdateDTO
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate
import io.cloudflight.jems.server.project.controller.auditAndControl.correction.toSimpleDto
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(PaymentApplicationsToEcCorrectionLinkingMapper::class.java)

fun PaymentToEcCorrectionLinkingUpdateDTO.toModel(): PaymentToEcCorrectionLinkingUpdate = mapper.map(this)
fun PaymentToEcCorrectionLinking.toDto(): PaymentToEcCorrectionLinkingDTO = mapper.map(this)
fun PaymentToEcCorrectionExtension.toDto(): PaymentToEcCorrectionExtensionDTO = mapper.map(this)

@Mapper
interface PaymentApplicationsToEcCorrectionLinkingMapper {
    @Named("toCorrectionDto")
    fun toDto(model: AuditControlCorrection) = model.toSimpleDto()
    @Mapping(source = "correction", target = "correction", qualifiedByName = ["toCorrectionDto"])
    fun map(model: PaymentToEcCorrectionLinking): PaymentToEcCorrectionLinkingDTO
    fun map(dto: PaymentToEcCorrectionLinkingUpdateDTO): PaymentToEcCorrectionLinkingUpdate
    fun map(model: PaymentToEcCorrectionExtension): PaymentToEcCorrectionExtensionDTO
}
