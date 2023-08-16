package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page


private val mapper = Mappers.getMapper(PaymentApplicationsToEcModelMapper::class.java)

fun PaymentApplicationToEcEntity.toModel(): PaymentApplicationToEc = mapper.map(this)
fun PaymentApplicationToEc.toEntity(): PaymentApplicationToEcEntity = mapper.map(this)
fun Page<PaymentApplicationToEcEntity>.toModel() = map { it.toModel() }

fun PaymentApplicationToEcEntity.toDetailModel() = PaymentApplicationToEcDetail(
    id = id,
    status = status,
    paymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
        programmeFund.toModel(),
        accountingYear.toModel()
    )
)

@Mapper
interface PaymentApplicationsToEcModelMapper {
    @Named("toFundModel")
    fun toFundModel(entity: ProgrammeFundEntity) = entity.toModel()

    @Mapping(source = "programmeFund", target = "programmeFund", qualifiedByName = ["toFundModel"])
    fun map(entity: PaymentApplicationToEcEntity): PaymentApplicationToEc
    fun map(model: PaymentApplicationToEc): PaymentApplicationToEcEntity
}
