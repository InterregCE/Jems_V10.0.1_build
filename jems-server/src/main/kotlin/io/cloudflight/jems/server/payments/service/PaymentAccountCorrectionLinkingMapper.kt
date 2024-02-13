package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.payments.dto.account.finance.PaymentAccountAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.account.finance.correction.PaymentAccountCorrectionExtensionDTO
import io.cloudflight.jems.api.payments.dto.account.finance.correction.PaymentAccountCorrectionLinkingDTO
import io.cloudflight.jems.api.payments.dto.account.finance.correction.PaymentAccountCorrectionLinkingUpdateDTO
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinkingUpdate
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(PaymentAccountCorrectionLinkingMapper::class.java)

fun PaymentAccountCorrectionLinking.toDto(): PaymentAccountCorrectionLinkingDTO = mapper.map(this)
fun PaymentAccountCorrectionExtension.toDto(): PaymentAccountCorrectionExtensionDTO = mapper.map(this)
fun PaymentAccountAmountSummary.toDto(): PaymentAccountAmountSummaryDTO = mapper.map(this)

fun PaymentAccountCorrectionLinkingUpdateDTO.toModel(): PaymentAccountCorrectionLinkingUpdate = mapper.map(this)

@Mapper
interface PaymentAccountCorrectionLinkingMapper {
    @Mapping(source = "correction.auditControlNr", target = "correction.auditControlNumber")
    fun map(model: PaymentAccountCorrectionLinking): PaymentAccountCorrectionLinkingDTO
    fun map(model: PaymentAccountCorrectionExtension): PaymentAccountCorrectionExtensionDTO
    fun map(model: PaymentAccountAmountSummary): PaymentAccountAmountSummaryDTO

    fun map(dto: PaymentAccountCorrectionLinkingUpdateDTO): PaymentAccountCorrectionLinkingUpdate
}
