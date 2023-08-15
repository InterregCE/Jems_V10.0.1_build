package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.payments.dto.PaymentApplicationsToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationsToEcDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationsToEcSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationsToEcUpdateDTO
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

private val mapper = Mappers.getMapper(PaymentApplicationsToEcMapper::class.java)

fun Page<PaymentApplicationsToEc>.toDto() = map { it.toDto() }
fun PaymentApplicationsToEc.toDto(): PaymentApplicationsToEcDTO = mapper.map(this)
fun PaymentApplicationsToEcDTO.toModel(): PaymentApplicationsToEc = mapper.map(this)
fun PaymentApplicationsToEcUpdateDTO.toModel(): PaymentApplicationsToEcUpdate = mapper.map(this)
fun PaymentApplicationsToEcUpdate.toDto(): PaymentApplicationsToEcUpdateDTO = mapper.map(this)

fun PaymentApplicationsToEcSummary.toDto(): PaymentApplicationsToEcSummaryDTO = mapper.map(this)
fun PaymentApplicationsToEcSummaryDTO.toModel(): PaymentApplicationsToEcSummary = mapper.map(this)

fun PaymentApplicationsToEcDetail.toDto(): PaymentApplicationsToEcDetailDTO = mapper.map(this)
fun PaymentApplicationsToEcDetailDTO.toModel(): PaymentApplicationsToEcDetail = mapper.map(this)

@Mapper
interface PaymentApplicationsToEcMapper {
    fun map(dto: PaymentApplicationsToEcDTO): PaymentApplicationsToEc
    fun map(model: PaymentApplicationsToEc): PaymentApplicationsToEcDTO
    fun map(dto: PaymentApplicationsToEcUpdateDTO): PaymentApplicationsToEcUpdate
    fun map(model: PaymentApplicationsToEcUpdate): PaymentApplicationsToEcUpdateDTO
    fun map(dto: PaymentApplicationsToEcSummaryDTO): PaymentApplicationsToEcSummary
    fun map(model: PaymentApplicationsToEcSummary): PaymentApplicationsToEcSummaryDTO
    fun map(dto: PaymentApplicationsToEcDetailDTO): PaymentApplicationsToEcDetail
    fun map(model: PaymentApplicationsToEcDetail): PaymentApplicationsToEcDetailDTO
}
