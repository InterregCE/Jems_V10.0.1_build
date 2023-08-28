package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcSummaryUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentEcStatusDTO
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

private val mapper = Mappers.getMapper(PaymentApplicationsToEcMapper::class.java)

fun Page<PaymentApplicationToEc>.toDto() = map { it.toDto() }
fun PaymentApplicationToEc.toDto(): PaymentApplicationToEcDTO = mapper.map(this)
fun PaymentApplicationToEcDTO.toModel(): PaymentApplicationToEc = mapper.map(this)
fun PaymentApplicationToEcSummaryUpdateDTO.toModel(): PaymentApplicationToEcSummaryUpdate = mapper.map(this)
fun PaymentApplicationToEcSummaryUpdate.toDto(): PaymentApplicationToEcSummaryUpdateDTO = mapper.map(this)

fun PaymentApplicationToEcSummary.toDto(): PaymentApplicationToEcSummaryDTO = mapper.map(this)
fun PaymentApplicationToEcSummaryDTO.toModel(): PaymentApplicationToEcSummary = mapper.map(this)

fun PaymentApplicationToEcDetail.toDto(): PaymentApplicationToEcDetailDTO = mapper.map(this)
fun PaymentApplicationToEcDetailDTO.toModel(): PaymentApplicationToEcDetail = mapper.map(this)

fun PaymentEcStatus.toDto() = mapper.map(this)
@Mapper
interface PaymentApplicationsToEcMapper {
    fun map(dto: PaymentApplicationToEcDTO): PaymentApplicationToEc
    fun map(model: PaymentApplicationToEc): PaymentApplicationToEcDTO
    fun map(dto: PaymentApplicationToEcSummaryUpdateDTO): PaymentApplicationToEcSummaryUpdate
    fun map(model: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcSummaryUpdateDTO
    fun map(dto: PaymentApplicationToEcSummaryDTO): PaymentApplicationToEcSummary
    fun map(model: PaymentApplicationToEcSummary): PaymentApplicationToEcSummaryDTO
    fun map(dto: PaymentApplicationToEcDetailDTO): PaymentApplicationToEcDetail
    fun map(model: PaymentApplicationToEcDetail): PaymentApplicationToEcDetailDTO
    fun map(model: PaymentEcStatus): PaymentEcStatusDTO
}
