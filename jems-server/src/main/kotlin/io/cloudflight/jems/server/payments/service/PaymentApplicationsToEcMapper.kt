package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcUpdateDTO
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

private val mapper = Mappers.getMapper(PaymentApplicationsToEcMapper::class.java)

fun Page<PaymentApplicationToEc>.toDto() = map { it.toDto() }
fun PaymentApplicationToEc.toDto(): PaymentApplicationToEcDTO = mapper.map(this)
fun PaymentApplicationToEcDTO.toModel(): PaymentApplicationToEc = mapper.map(this)
fun PaymentApplicationToEcUpdateDTO.toModel(): PaymentApplicationToEcUpdate = mapper.map(this)
fun PaymentApplicationToEcUpdate.toDto(): PaymentApplicationToEcUpdateDTO = mapper.map(this)

fun PaymentApplicationToEcSummary.toDto(): PaymentApplicationToEcSummaryDTO = mapper.map(this)
fun PaymentApplicationToEcSummaryDTO.toModel(): PaymentApplicationToEcSummary = mapper.map(this)

fun PaymentApplicationToEcDetail.toDto(): PaymentApplicationToEcDetailDTO = mapper.map(this)
fun PaymentApplicationToEcDetailDTO.toModel(): PaymentApplicationToEcDetail = mapper.map(this)

@Mapper
interface PaymentApplicationsToEcMapper {
    fun map(dto: PaymentApplicationToEcDTO): PaymentApplicationToEc
    fun map(model: PaymentApplicationToEc): PaymentApplicationToEcDTO
    fun map(dto: PaymentApplicationToEcUpdateDTO): PaymentApplicationToEcUpdate
    fun map(model: PaymentApplicationToEcUpdate): PaymentApplicationToEcUpdateDTO
    fun map(dto: PaymentApplicationToEcSummaryDTO): PaymentApplicationToEcSummary
    fun map(model: PaymentApplicationToEcSummary): PaymentApplicationToEcSummaryDTO
    fun map(dto: PaymentApplicationToEcDetailDTO): PaymentApplicationToEcDetail
    fun map(model: PaymentApplicationToEcDetail): PaymentApplicationToEcDetailDTO
}
