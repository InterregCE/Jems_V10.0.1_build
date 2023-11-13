package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcCreateDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcSummaryUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentEcStatusDTO
import io.cloudflight.jems.api.payments.dto.PaymentEcStatusUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentSearchRequestScoBasisDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcOverviewTypeDTO
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

private val mapper = Mappers.getMapper(PaymentApplicationsToEcMapper::class.java)

fun Page<PaymentApplicationToEc>.toDto() = map { it.toDto() }
fun PaymentApplicationToEc.toDto(): PaymentApplicationToEcDTO = mapper.map(this)
fun PaymentApplicationToEcDTO.toModel(): PaymentApplicationToEc = mapper.map(this)

fun PaymentApplicationToEcCreateDTO.toModel(): PaymentApplicationToEcCreate = mapper.map(this)

fun PaymentApplicationToEcSummaryUpdateDTO.toModel(): PaymentApplicationToEcSummaryUpdate = mapper.map(this)
fun PaymentApplicationToEcSummaryUpdate.toDto(): PaymentApplicationToEcSummaryUpdateDTO = mapper.map(this)

fun PaymentApplicationToEcSummary.toDto(): PaymentApplicationToEcSummaryDTO = mapper.map(this)
fun PaymentApplicationToEcSummaryDTO.toModel(): PaymentApplicationToEcSummary = mapper.map(this)

fun PaymentApplicationToEcDetail.toDto() = PaymentApplicationToEcDetailDTO(
    id = id,
    status = status.toDto(),
    availableToReOpen = isAvailableToReOpen,
    paymentApplicationToEcSummary = mapper.map(this.paymentApplicationToEcSummary)
)
fun PaymentApplicationToEcDetailDTO.toModel(): PaymentApplicationToEcDetail = mapper.map(this)
fun PaymentEcStatus.toDto() = mapper.map(this)
fun Page<PaymentToEcPayment>.toPaymentToEcLinkingDTO() = map { it.toDto() }
fun PaymentToEcPayment.toDto(): PaymentToEcLinkingDTO = mapper.map(this)
fun PaymentToEcLinkingUpdateDTO.toModel(): PaymentToEcLinkingUpdate = mapper.map(this)

fun PaymentToEcAmountSummaryDTO.toModel() = mapper.map(this)
fun PaymentToEcAmountSummary.toDto() = mapper.map(this)
fun PaymentSearchRequestScoBasisDTO.toModel() = mapper.map(this)
fun PaymentToEcOverviewTypeDTO.toModel() = mapper.map(this)

fun PaymentApplicationToEcDetail.toStatusUpdateDto() = PaymentEcStatusUpdateDTO(
    status = status.toDto(),
    availableToReOpen = isAvailableToReOpen
)

@Mapper
interface PaymentApplicationsToEcMapper {
    fun map(dto: PaymentApplicationToEcDTO): PaymentApplicationToEc
    fun map(model: PaymentApplicationToEc): PaymentApplicationToEcDTO
    fun map(dto: PaymentApplicationToEcSummaryUpdateDTO): PaymentApplicationToEcSummaryUpdate
    fun map(model: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcSummaryUpdateDTO
    fun map(dto: PaymentApplicationToEcSummaryDTO): PaymentApplicationToEcSummary
    fun map(model: PaymentApplicationToEcSummary): PaymentApplicationToEcSummaryDTO
    fun map(dto: PaymentApplicationToEcDetailDTO): PaymentApplicationToEcDetail
    fun map(model: PaymentEcStatus): PaymentEcStatusDTO
    fun map(model: PaymentToEcPayment): PaymentToEcLinkingDTO
    fun map(dto: PaymentToEcLinkingUpdateDTO): PaymentToEcLinkingUpdate
    fun map(dto: PaymentApplicationToEcCreateDTO): PaymentApplicationToEcCreate

    fun map(dto: PaymentToEcAmountSummaryDTO): PaymentToEcAmountSummary
    fun map(model: PaymentToEcAmountSummary): PaymentToEcAmountSummaryDTO
    fun map(model: PaymentSearchRequestScoBasisDTO): PaymentSearchRequestScoBasis
    fun map(model: PaymentToEcOverviewTypeDTO): PaymentToEcOverviewType
}
