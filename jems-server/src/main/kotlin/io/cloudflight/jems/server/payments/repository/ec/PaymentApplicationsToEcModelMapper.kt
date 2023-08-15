package io.cloudflight.jems.server.payments.repository.ec

import io.cloudflight.jems.server.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.PaymentApplicationsToEcEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcSummary
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page


private val mapper = Mappers.getMapper(PaymentApplicationsToEcModelMapper::class.java)

fun PaymentApplicationsToEcEntity.toModel(): PaymentApplicationsToEc = mapper.map(this)
fun PaymentApplicationsToEc.toEntity(): PaymentApplicationsToEcEntity = mapper.map(this)
fun Page<PaymentApplicationsToEcEntity>.toModel() = map { it.toModel() }

fun PaymentApplicationsToEcEntity.toDetailModel() = PaymentApplicationsToEcDetail(
    id = id,
    status = status,
    paymentApplicationsToEcSummary = PaymentApplicationsToEcSummary(
        programmeFund.toModel(),
        accountingYear.toModel()
    )
)

@Mapper
interface PaymentApplicationsToEcModelMapper {
    @Named("toFundModel")
    fun toFundModel(entity: ProgrammeFundEntity) = entity.toModel()

    @Mapping(source = "programmeFund", target = "programmeFund", qualifiedByName = ["toFundModel"])
    fun map(entity: PaymentApplicationsToEcEntity): PaymentApplicationsToEc
    fun map(model: PaymentApplicationsToEc): PaymentApplicationsToEcEntity
}
