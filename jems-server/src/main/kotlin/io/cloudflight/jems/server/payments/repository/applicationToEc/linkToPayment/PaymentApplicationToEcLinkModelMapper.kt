package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToPayment

import io.cloudflight.jems.server.payments.entity.PaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisCumulativeOverviewEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers


private val mapper = Mappers.getMapper(PaymentApplicationToEcLinkModelMapper::class.java)

fun PaymentToEcExtensionEntity.toModel() = PaymentToEcExtension(
    paymentId = paymentId,
    ecPaymentId = paymentApplicationToEc?.id,
    ecPaymentStatus = paymentApplicationToEc?.status,
)

fun List<PaymentToEcPriorityAxisOverviewEntity>.toModel() =
    associate { Pair(it.priorityAxis?.id, mapper.map(it)) }

fun List<PaymentToEcPriorityAxisCumulativeOverviewEntity>.toOverviewModels() =
    associate { Pair(it.priorityAxis?.id, mapper.map(it)) }


@Mapper
interface PaymentApplicationToEcLinkModelMapper {

    @Mappings(
        Mapping(source = "priorityAxis.code", target = "priorityAxis"),
    )
    fun map(entity: PaymentToEcPriorityAxisOverviewEntity): PaymentToEcAmountSummaryLine

    @Mappings(
        Mapping(source = "priorityAxis.code", target = "priorityAxis"),
    )
    fun map(entity: PaymentToEcPriorityAxisCumulativeOverviewEntity): PaymentToEcAmountSummaryLine


}
